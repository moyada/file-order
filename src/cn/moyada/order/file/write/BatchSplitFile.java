package cn.moyada.order.file.write;

import cn.moyada.order.file.read.ReadAction;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xueyikang
 * @create 2018-09-05 11:30
 */
public class BatchSplitFile extends SplitFile {

    protected final Executor executor;
    private final AtomicInteger finish = new AtomicInteger(0);
    private int end;

    public BatchSplitFile(Path ouput, int size, Executor executor) throws IOException {
        super(ouput, size);
        this.executor = executor;
    }

    public void splitBatch(List<ReadAction> readActions, Runnable task) {
        end = readActions.size();
        for (ReadAction action : readActions) {
            executor.execute(() -> {
                split(action);
                finish(task);
            });
        }
    }

    private void finish(Runnable task) {
        int count = finish.incrementAndGet();
        if (count == end) {
            flush();
            if (null != task) {
                task.run();
            }
        }
    }
}
