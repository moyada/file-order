package cn.moyada.order.file.write;

import cn.moyada.order.file.ChannelCloseable;

/**
 * @author xueyikang
 * @create 2018-09-04 00:34
 */
public interface WriteAction extends ChannelCloseable {

    void write(byte[] data);

    void flush();
}
