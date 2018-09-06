package cn.moyada.order.file;

import java.nio.file.Path;

/**
 * @author xueyikang
 * @create 2018-09-06 06:46
 */
public class PathUtil {

    public static Integer getNum(Path path) {
        String name = path.toString();
        int start = name.lastIndexOf('_');
        int end = name.lastIndexOf('.');
        if (start == -1 || end == -1) {
            return null;
        }

        try {
            return Integer.valueOf(name.substring(start + 1, end));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
