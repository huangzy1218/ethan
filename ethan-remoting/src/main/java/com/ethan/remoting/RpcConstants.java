package com.ethan.remoting;

/**
 * Remoting module constants.
 *
 * @author Huang Z.Y.
 */
public interface RpcConstants {

    String BIND_IP_KEY = "bind.ip";

    String BIND_PORT_KEY = "bind.port";

    String NETTY_EPOLL_ENABLE_KEY = "netty.epoll.enable";

    String OS_NAME_KEY = "os.name";

    String OS_LINUX_PREFIX = "linux";

    int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

    byte TOTAL_LENGTH = 16;

    /**
     * Magic number to verify RpcMessage.
     */
    byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};

    byte VERSION = 1;


}
