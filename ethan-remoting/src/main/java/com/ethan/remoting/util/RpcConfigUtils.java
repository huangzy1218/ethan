package com.ethan.remoting.util;

import com.ethan.rpc.RpcConfigProperties;
import com.ethan.rpc.config.RpcConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RpcConfigUtils {

    private static final AnnotationConfigApplicationContext context;

    static {
        context = new AnnotationConfigApplicationContext(RpcConfiguration.class);
    }

    private RpcConfigUtils() {
        // Private constructor to prevent instantiation
    }

    public static RpcConfigProperties getRpcConfigProperties() {
        return context.getBean(RpcConfigProperties.class);
    }

}
