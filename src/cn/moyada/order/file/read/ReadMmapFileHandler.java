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
public class ReadMmapFileHandler extends AbstractFileHandler implements ReadAction {

    private final byte[] data = new byte[64];
    private int index = 0;

    public ReadMmapFileHandler(Path path) throws IOException {
        super(path, true, StandardOpenOption.READ);
    }

    @Override
    public String getNext() {

        byte next;
        while (buffer.hasRemaining()) {
            next = buffer.get();
            if (next == SplitConstant.N) {
                return getStr();
            }
            data[index++] = next;
        }

        return new String(data, 0, index);
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

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/Users/xueyikang/file-order/resource/rs_0.data");
        ReadAction readAction = new ReadMmapFileHandler(path);
        String line;

        for (int i = 0; i < 10; i++) {
            line = readAction.getNext();
            if (null == line) {
                break;
            }
            System.out.println(line);
        }
        readAction.close();
    }
}
