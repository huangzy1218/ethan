package com.ethan.rpc.config;

import com.ethan.rpc.RpcConfigProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Rpc configuration.
 *
 * @author Huang Z.Y.
 */
@Configuration
@EnableConfigurationProperties(RpcConfigProperties.class)
@ComponentScan(basePackages = "com.ethan")
public class RpcConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
