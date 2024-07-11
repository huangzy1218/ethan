package com.ethan.remoting;

public interface RemotingConstants {

    String EVENT_LOOP_BOSS_POOL_NAME = "NettyServerBoss";

    String EVENT_LOOP_WORKER_POOL_NAME = "NettyServerWorker";

    String IO_THREADS_KEY = "iothreads";

    int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

}
