package cn.moyada.order.file;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author xueyikang
 * @create 2018-09-06 06:16
 */
public class GatherFile {

    private final Path rs;
    private final int index;
    private final Path out;

    public GatherFile(Path rs, int index, Path out) throws IOException {
        this.rs = rs;
        this.index = index;

        String name = out.toString() + "/out_" + index + ".data";
        this.out = Paths.get(name);

        Files.deleteIfExists(this.out);
        Files.createFile(this.out);
    }

    public void transfer() throws IOException {
        FileChannel output = FileChannel.open(out, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

        Files.list(rs)
                .filter(f -> {
                    Integer num = PathUtil.getNum(f);
                    return null != num && num / 100 == index;
                })
                .sorted((f1, f2) -> {
                    int num1 = PathUtil.getNum(f1);
                    int num2 = PathUtil.getNum(f2);

                    return Integer.compare(num1, num2);
                })
                .forEach(f -> {
                    try {
                        FileChannel ch = FileChannel.open(f);
                        ch.transferTo(0, ch.size(), output);
                        ch.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        output.close();
    }

    public static void main(String[] args) throws IOException {
        GatherFile gatherFile = new GatherFile(SplitConstant.TMP_PATH, 0, SplitConstant.OUT_PATH);
        gatherFile.transfer();

//        int index = 0;
//        Path path = Paths.get("/Users/xueyikang/file-order/tmp/");
//
//        Files.list(path)
//                .filter(f -> {
//                    String name = f.toString();
//                    int start = name.indexOf("out_");
//                    int end = name.lastIndexOf(".");
//                    if (start == -1 || end == -1) {
//                        return false;
//                    }
//                    int num = Integer.valueOf(name.substring(start + 4, end));
//                    return num / 100 == index;
//                })
//                .sorted((f1, f2) -> {
//                    String n1 = f1.toString();
//                    String n2 = f2.toString();
//
//                    n1 = n1.substring(n1.lastIndexOf('_'));
//                    n2 = n2.substring(n2.lastIndexOf('_'));
//                    return n1.compareTo(n2);
//                })
//                .forEach(f -> System.out.println(f.toString()));
//
//        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
//
//            @Override
//            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                String name = file.toString();
//                int start = name.indexOf("out_") + 4;
//                int end = name.lastIndexOf(".");
//                if (start == -1 || end == -1) {
//                    return FileVisitResult.CONTINUE;
//                }
//
//                int num = Integer.valueOf(name.substring(start, end));
//                if (num / 100 == index) {
//                    System.out.println(num);
//                }
//
//                return FileVisitResult.CONTINUE;
//            }
//        });
    }
}
