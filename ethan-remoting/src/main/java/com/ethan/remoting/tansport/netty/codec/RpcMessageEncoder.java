package com.ethan.remoting.tansport.netty.codec;

/**
 * Custom protocol encoder.<br/>
 * <pre>
 *  0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *  +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *  |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *  +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *  |                                                                                                       |
 *  |                                         body                                                          |
 *  |                                                                                                       |
 *  |                                        ... ...                                                        |
 *  +-------------------------------------------------------------------------------------------------------+
 * </pre>
 *
 * @author Huang Z.Y,
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder</a>
 */
public class RpcMessageEncoder {
}
