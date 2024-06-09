package com.ethan.rpc;

import com.ethan.common.enumeration.CompressType;
import com.ethan.common.enumeration.SerializationType;

/**
 * @author Huang Z.Y.
 */
public interface Constants {

    CompressType DEFAULT_REMOTING_COMPRESS = CompressType.GZIP;

    SerializationType DEFAULT_REMOTING_SERIALIZATION = SerializationType.FASTJSON2;

    String BIND_IP_KEY = "bind.ip";

    String COMPRESS_KEY = "message.compress";
    String DECOMPRESS_KEY = "message.decompress";

    String SERIALIZATION_KEY = "message.serialize";

    String BIND_PORT_KEY = "bind.port";

    String NETTY_EPOLL_ENABLE_KEY = "netty.epoll.enable";

    String OS_NAME_KEY = "os.name";

    String OS_LINUX_PREFIX = "linux";

    int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

    byte TOTAL_LENGTH = 16;
    int HEAD_LENGTH = 16;

    /**
     * Magic number to verify RpcMessage.
     */
    byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};

    byte VERSION = 1;

    byte REQUEST_TYPE = 1;
    byte RESPONSE_TYPE = 2;
    /**
     * Ping.
     */
    byte HEARTBEAT_REQUEST_TYPE = 3;
    /**
     * Pong.
     */
    byte HEARTBEAT_RESPONSE_TYPE = 4;

    String PING = "ping";
    String PONG = "pong";
    int DEFAULT_CONNECT_TIMEOUT = 3000;
    String CONNECT_TIMEOUT_KEY = "connect.timeout";
    String HEARTBEAT_KEY = "heartbeat";
    String TIMEOUT_KEY = "timeout";

    int DEFAULT_TIMEOUT = 1000;

}
    