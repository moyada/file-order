package cn.moyada.order.file;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author xueyikang
 * @create 2018-09-04 01:14
 */
public abstract class AbstractFileHandler implements Closeable {

    private static final int DEFAULT_SIZE = 1024;

    protected final FileChannel fileChannel;

    protected final ByteBuffer buffer;

    public AbstractFileHandler(Path path, StandardOpenOption... opts) throws IOException {
        this(path, DEFAULT_SIZE, opts);
    }

    public AbstractFileHandler(Path path, int size, StandardOpenOption... opts) throws IOException {
        this.fileChannel = FileChannel.open(path, opts);
        this.buffer = ByteBuffer.allocate(size);
    }

    @Override
    public void close() throws IOException {
        fileChannel.close();
    }
}
