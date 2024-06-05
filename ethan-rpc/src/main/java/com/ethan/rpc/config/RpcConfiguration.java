package com.ethan.rpc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Rpc configuration.
 *
 * @author Huang Z.Y.
 */
@Configuration
@ComponentScan(basePackages = "com.ethan.rpc")
@PropertySource("classpath:application.yml")
public class RpcConfiguration {
}
