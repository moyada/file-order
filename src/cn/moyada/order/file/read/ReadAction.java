package cn.moyada.order.file.read;

import cn.moyada.order.file.ChannelCloseable;

/**
 * @author xueyikang
 * @create 2018-09-04 00:34
 */
public interface ReadAction extends ChannelCloseable {

    String getNext();

    default Long getNextLong() {
        String next = getNext();
        if (null == next) {
            return null;
        }

        Long value;
        try {
            value = Long.valueOf(next);
        } catch (NumberFormatException e) {
            return null;
        }

        return value;
    }
}
