package cn.moyada.order.file;

import cn.moyada.order.file.read.ReadAction;
import cn.moyada.order.file.read.ReadFileHandler;
import cn.moyada.order.file.write.SplitFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;

/**
 * @author xueyikang
 * @create 2018-09-04 14:27
 */
public class OrderManager2 {

    private static final int MAX_THREAD = 20;

    private final Path rsPath;
    private final Path outPath;
    private final int tmpSize;
    private final int dataSize;
    private final ThreadPoolExecutor executor;

    public OrderManager2(Path rsPath, Path outPath, int tmpSize, int dataSize) {
        this.rsPath = rsPath;
        this.outPath = outPath;
        this.tmpSize = tmpSize;
        this.dataSize = dataSize;
        this.executor = new ThreadPoolExecutor(5, MAX_THREAD, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    public void order() throws IOException {
        long start = System.nanoTime();

        int fileSize = (int) Files.list(rsPath).count();

        CountDownLatch split = new CountDownLatch(fileSize);

        SplitFile splitFile = new SplitFile(SplitConstant.TMP_PATH, tmpSize);
        splitFile.setDataSize(dataSize);

        Files.list(rsPath).forEach(file -> {
            ReadAction readHandler;
            try {
                readHandler = new ReadFileHandler(file);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            executor.execute(() -> {
                splitFile.split(readHandler);

                split.countDown();

                try {
                    readHandler.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

        try {
            split.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        splitFile.flush();
        splitFile.close();

        System.out.println("split done.");
        System.out.println((System.nanoTime() - start) / 1_000_000L);

        CountDownLatch sort = new CountDownLatch(tmpSize);
        Semaphore semaphore = new Semaphore(MAX_THREAD);
        Files.list(SplitConstant.TMP_PATH).forEach(
            file -> executor.execute(() -> {

                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    new SortFile(file, SplitConstant.TMP_SIZE).run();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                semaphore.release();
                sort.countDown();
            }
        ));

        try {
            sort.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("sort done.");
        System.out.println((System.nanoTime() - start) / 1_000_000L);

        CountDownLatch finish = new CountDownLatch(fileSize);

        Files.list(rsPath).forEach(f ->
            executor.execute(() -> {
                @SuppressWarnings("ConstantConditions")
                int num = PathUtil.getNum(f);
                try {
                    new GatherFile(SplitConstant.TMP_PATH, num, outPath).transfer();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                finish.countDown();
            })
        );

        try {
            finish.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("gather done.");
        System.out.println((System.nanoTime() - start) / 1_000_000L);

        executor.shutdown();
    }

    public static void main(String[] args) throws IOException {
        OrderManager2 orderManager =
                new OrderManager2(SplitConstant.RS_PATH, SplitConstant.OUT_PATH,
                        SplitConstant.TMP_FILE_TOTAL, SplitConstant.BOUND);

        orderManager.order();
    }
}
