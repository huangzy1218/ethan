package com.ethan.common.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.BitSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Huang Z.Y.
 */
public class NetUtils {

    // Returned port range is [30000, 39999]
    private static final int RND_PORT_START = 30000;
    private static final int RND_PORT_RANGE = 10000;

    // Valid port range is (0, 65535]
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;

    private static BitSet USED_PORT = new BitSet(65536);

    public static synchronized int getAvailablePort() {
        int randomPort = getRandomPort();
        return getAvailablePort(randomPort);
    }

    public static int getRandomPort() {
        return RND_PORT_START + ThreadLocalRandom.current().nextInt(RND_PORT_RANGE);
    }

    public static synchronized int getAvailablePort(int port) {
        if (port < MIN_PORT) {
            return MIN_PORT;
        }

        for (int i = port; i < MAX_PORT; i++) {
            if (USED_PORT.get(i)) {
                continue;
            }
            try (ServerSocket ignored = new ServerSocket(i)) {
                USED_PORT.set(i);
                port = i;
                break;
            } catch (IOException e) {
                // Continue
            }
        }
        return port;
    }

}
    