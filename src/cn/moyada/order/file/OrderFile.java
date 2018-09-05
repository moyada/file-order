package cn.moyada.order.file;

import cn.moyada.order.file.read.ReadAction;
import cn.moyada.order.file.read.ReadFileHandler;
import cn.moyada.order.file.write.SplitFile;
import cn.moyada.order.file.write.WriteAction;
import cn.moyada.order.file.write.WriteFileHandler;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author xueyikang
 * @create 2018-09-05 11:48
 */
public class OrderFile extends SplitFile {

    private final ReadAction readAction;
    private final Path rs;

    public OrderFile(Path rs, Path ouput, int size) throws IOException {
        super(ouput, size);
        this.readAction = new ReadFileHandler(rs);
        this.rs = rs;
    }

    public void work() throws IOException {
        split(readAction);
        flush();
        readAction.close();

        order();

        gather();
    }

    private void order() throws IOException {
        Stream<Path> files = Files.list(output);
        files.forEach(file -> {
            try {
                ReadAction read = new ReadFileHandler(file);

                List<Long> list = new ArrayList<>(SplitConstant.TMP_SIZE);

                Long value;
                while (null != (value = read.getNextLong())) {
                    list.add(value);
                }
                Collections.sort(list);

                read.close();

                WriteAction write = new WriteFileHandler(file);
                list.forEach(item -> write.write(item.toString().getBytes()));
                write.flush();
                write.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void gather() throws IOException {
        FileChannel ch = FileChannel.open(rs, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

        Stream<Path> files = Files.list(output);
        files.forEach(file -> {
            try {
                FileChannel open = FileChannel.open(file, StandardOpenOption.READ);
                open.transferTo(0, open.size(), ch);
                open.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ch.close();
    }
}
