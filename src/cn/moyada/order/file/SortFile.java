package cn.moyada.order.file;

import cn.moyada.order.file.read.ReadAction;
import cn.moyada.order.file.read.ReadMmapFileHandler;
import cn.moyada.order.file.write.WriteAction;
import cn.moyada.order.file.write.WriteFileHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author xueyikang
 * @create 2018-09-04 18:57
 */
public class SortFile {

    private final Path path;
    private final int dataSize;

    public SortFile(Path file, int dataSize) {
        this.path = file;
        this.dataSize = dataSize;
    }

    public void run() throws IOException {
        ReadAction readAction = new ReadMmapFileHandler(path);

        Long value;

        List<Long> list = new ArrayList<>(dataSize);

        while (true) {
            value = readAction.getNextLong();
            if (null == value) {
                break;
            }

            list.add(value);
        }

        try {
            readAction.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(list);

        WriteAction writeAction = new WriteFileHandler(path);
        list.forEach(v -> writeAction.write(v.toString().getBytes()));
        writeAction.flush();
        writeAction.close();
    }
}
