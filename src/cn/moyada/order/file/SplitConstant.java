package cn.moyada.order.file;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author xueyikang
 * @create 2018-09-04 10:34
 */
public class SplitConstant {

    public static final int END = 0;
    public static final int EOS = -1;

    public static final String RS_DIR;
    public static final String OUT_DIR;
    public static final String TMP_DIR;

    public static final Path RS_PATH;
    public static final Path OUT_PATH;
    public static final Path TMP_PATH;

    public static final byte N = '\n';

    public static final int BOUND = Integer.MAX_VALUE;
//    public static final int BOUND = 1_000_000_000;

    public static final int FILE_TOTAL = 10;
    public static final int TMP_TOTAL;

    public static final int DATA_SIZE = 10_000_000;
    public static final int TMP_SIZE = 1_000;
    public static final int FILE_SPLIT;

    public static final int TMP_FILE_TOTAL = 1_000;

    static {
        int quantity = DATA_SIZE / TMP_SIZE;
        FILE_SPLIT = quantity > 0 ? quantity : 1;

        TMP_TOTAL = DATA_SIZE / TMP_SIZE;

        RS_DIR = "/Users/xueyikang/file-order/resource";
        OUT_DIR = "/Users/xueyikang/file-order/output";
        TMP_DIR = "/Users/xueyikang/file-order/tmp";

        RS_PATH = Paths.get(RS_DIR);
        OUT_PATH = Paths.get(OUT_DIR);
        TMP_PATH = Paths.get(TMP_DIR);
//        URL resource = PrepareGenerator.class.getResource("/resource");
////        URL resource = PrepareGenerator.class.getResource("/resource");
//        if (null != resource) {
//            path = resource.getPath();
//        } else {
//            path = null;
//        }
    }

    public static void main(String[] args) {
        System.out.println("static");
    }
}
