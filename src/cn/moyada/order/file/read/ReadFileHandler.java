package cn.moyada.order.file.read;

import cn.moyada.order.file.AbstractFileHandler;
import cn.moyada.order.file.SplitConstant;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author xueyikang
 * @create 2018-09-04 01:16
 */
public class ReadFileHandler extends AbstractFileHandler implements ReadAction {

    private final byte[] data = new byte[64];
    private int index = -1;

    public ReadFileHandler(Path path) throws IOException {
        super(path, StandardOpenOption.READ);
    }

    @Override
    public String getNext() {
        int size = readBuf();
        if (size == SplitConstant.EOS) {
            return getStr();
        }

        int position = buffer.position();

        byte next;
        for (; position < size; position++) {
            next = buffer.get();
            if (next == SplitConstant.N) {
                return getStr();
            }
            data[index++] = next;
        }

        return getNext();
    }

    private String getStr() {
        String str;
        if (index == SplitConstant.END) {
            str = null;
        } else {
            str = new String(data, 0, index);
        }
        index = 0;
        return str;
    }

    private int readBuf() {
        int size;

        if (!buffer.hasRemaining() || init()) {
            try {
                buffer.clear();
                size = fileChannel.read(buffer);
                buffer.flip();
            } catch (IOException e) {
                e.printStackTrace();
                size = SplitConstant.EOS;
            }
        } else {
            size = buffer.limit();
        }
        return size;
    }

    private boolean init() {
        if(index != -1) {
            return false;
        }
        index = 0;
        return true;
    }

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/Users/xueyikang/file-order/resource/rs_0.data");
        ReadAction readAction = new ReadFileHandler(path);
        String line;

        while (true) {
            line = readAction.getNext();
            if (null == line) {
                break;
            }
            System.out.println(line);
        }
        readAction.close();
    }
}
