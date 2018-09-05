package cn.moyada.order.file.write;

import cn.moyada.order.file.AbstractFileHandler;
import cn.moyada.order.file.SplitConstant;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author xueyikang
 * @create 2018-09-04 14:13
 */
public class WriteFileHandler extends AbstractFileHandler implements WriteAction {

    public WriteFileHandler(Path path) throws IOException {
        super(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public WriteFileHandler(Path path, int size) throws IOException {
        super(path, size, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public void write(byte[] data) {
        putData(data.length);
        buffer.put(data);
        buffer.put(SplitConstant.N);
    }

    @Override
    public void flush() {
        putData(buffer.limit() + 1);
    }

    private void putData(final int nextSize) {
        int position = buffer.position();
        int limit = buffer.limit();

        if(position + nextSize >= limit) {
            try {
                buffer.flip();
                fileChannel.write(buffer);
                buffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
