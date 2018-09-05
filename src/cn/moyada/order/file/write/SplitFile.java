package cn.moyada.order.file.write;

import cn.moyada.order.file.read.ReadAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xueyikang
 * @create 2018-09-04 18:57
 */
public abstract class SplitFile {

    private float dataSize = -1;
    protected final Path output;
    protected final List<WriteAction> writeActions;

    public SplitFile(Path ouput, int size) throws IOException {
        if (Files.notExists(ouput)) {
            Files.createDirectory(ouput);
        }

        writeActions = new ArrayList<>();

        String dir = ouput.toString();
        Path file;
        for (int i = 0; i < size; i++) {
            file = Paths.get(dir + "/out_" + i + ".data");
            writeActions.add(new WriteLockFileHandler(file));
        }

        this.output = ouput;
    }

    public void split(ReadAction readAction) {
        Long value = readAction.getNextLong();
        if (null == value) {
            throw new IllegalStateException("init split dataSize error.");
        }

        firstInsert(readAction.getNextLong());

        while (true) {
            value = readAction.getNextLong();
            if (null == value) {
                break;
            }

            insert(value);
        }
    }

    private void insert(long value) {
        int index = getIndex(value);
        WriteAction writeAction = writeActions.get(index);
        writeAction.write(String.valueOf(value).getBytes());
    }

    private void firstInsert(long value) {
        if (dataSize > -1) {
            return;
        }
        this.dataSize = getDataSize(value);
        insert(value);
    }

    public void flush() {
        writeActions.forEach(WriteAction::flush);
    }

    private int getIndex(long value) {
        float result = value / dataSize;
        int size = writeActions.size();
        int index = (int) (result * size);
        return index < size ? index : size - 1;
    }

    private static float getDataSize(long value) {
        long size = 1;
        while (true) {
            value = value / 10;
            if (value == 0) {
                break;
            }
            size = size * 10;
        }
        return size * 11.0F;
    }
}
