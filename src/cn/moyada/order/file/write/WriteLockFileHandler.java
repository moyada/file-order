package cn.moyada.order.file.write;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xueyikang
 * @create 2018-09-04 14:13
 */
public class WriteLockFileHandler extends WriteFileHandler implements WriteAction {

    private final Lock lock = new ReentrantLock();

    public WriteLockFileHandler(Path path) throws IOException {
        super(path);
    }

    @Override
    public void write(byte[] data) {
        lock.lock();
        try {
            super.write(data);
        } finally {
            lock.unlock();
        }
    }
}
