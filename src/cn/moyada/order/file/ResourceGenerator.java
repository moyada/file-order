package cn.moyada.order.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @author xueyikang
 * @create 2018-09-04 00:35
 */
public class ResourceGenerator {

    private static void createRoot() throws IOException {
        Path root = Paths.get(SplitConstant.RS_DIR);
        if (Files.notExists(root)) {
            Files.createDirectory(root);
        } else if (!Files.isDirectory(root)) {
            throw new IOException(SplitConstant.RS_DIR + " is not a directory.");
        }
    }

    private static void createFile() throws IOException {
        Path file;
        for (int i = 0; i < SplitConstant.FILE_TOTAL; i++) {
            file = Paths.get(SplitConstant.RS_DIR + "/rs_" + i + ".data");
            Files.deleteIfExists(file);
            Files.createFile(file);
        }
    }

    private static void generateData(int size) throws IOException {
        Path root = Paths.get(SplitConstant.RS_DIR);
        Stream<Path> list = Files.list(root);
        list.parallel().forEach((file) -> {
            try {
                FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.APPEND);
                ByteBuffer buf = ByteBuffer.allocate(48);
                Random random = new Random();
                long value;
                for (int i = 0; i < size; i++) {
                    value = random.nextInt(SplitConstant.BOUND);
                    if (value < 0) {
                        value = -value;
                    }
                    if (value == 0) {
                        value = 100;
                    }

                    buf.put(String.valueOf(value).getBytes());
                    if (i < (size - 1)) {
                        buf.put(SplitConstant.N);
                    }

                    buf.flip();
                    fileChannel.write(buf);
                    buf.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) throws IOException {
        createRoot();
        createFile();
        generateData(SplitConstant.DATA_SIZE);
    }
}
