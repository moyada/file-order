package cn.moyada.order.file;

import cn.moyada.order.file.read.ReadAction;
import cn.moyada.order.file.read.ReadFileHandler;
import cn.moyada.order.file.write.BatchSplitFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author xueyikang
 * @create 2018-09-04 14:27
 */
public class OrderManager extends BatchSplitFile implements ChannelCloseable {

    private volatile long startTime;

    private final List<ReadAction> resources = new ArrayList<>();
    private final Path ouput;
    private final ThreadPoolExecutor executor;

    public OrderManager(Path ouput, int size, ThreadPoolExecutor executor) throws IOException {
        super(ouput, size, executor);
        this.ouput = ouput;
        this.executor = executor;
    }

    public OrderManager buildResource() throws IOException {
        Stream<Path> list = Files.list(SplitConstant.RS_PATH);
        list.forEach(f -> {
            try {
                resources.add(new ReadFileHandler(f));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return this;
    }

    public void doOrder() {
        startTime = System.nanoTime();

        splitBatch(resources, () -> {
            try {
                orderSelf();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void orderSelf() throws IOException {
        Stream<Path> list = Files.list(ouput);
        AtomicInteger i = new AtomicInteger(0);

        list.forEach(item -> {
            int index = i.getAndIncrement();

            executor.execute(() -> {
                try {
                    new OrderFile(item, Paths.get(SplitConstant.TMP_DIR + "/" + index), SplitConstant.TMP_TOTAL).work();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (i.decrementAndGet() == 0) {
                    new Thread(() -> {
                        executor.shutdown();
                        System.out.println((System.nanoTime() - startTime) / 1_000_000L);
                    }).start();
                }
            });
        });
    }

    @Override
    public void close() {
        close(resources);
        close(writeActions);
    }

    public static void main(String[] args) throws IOException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 1L, TimeUnit.MINUTES, new LinkedBlockingDeque<>());

        OrderManager orderManager = new OrderManager(SplitConstant.OUT_PATH, SplitConstant.FILE_TOTAL, executor);
        orderManager
                .buildResource()
                .doOrder();
        // orderManager.close();
    }
}
