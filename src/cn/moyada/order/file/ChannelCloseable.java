package cn.moyada.order.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * @author xueyikang
 * @create 2018-09-05 11:36
 */
public interface ChannelCloseable extends Closeable {

    default void close(List<? extends Closeable> closeableList) {
        if(null != closeableList) {
            closeableList.forEach(item -> {
                try {
                    item.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            closeableList = null;
        }
    }
}
