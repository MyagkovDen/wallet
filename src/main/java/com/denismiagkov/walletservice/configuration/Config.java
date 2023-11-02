package com.denismiagkov.walletservice.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@ComponentScan(basePackages = "com.denismiagkov.walletservice")
@EnableWebMvc
@EnableAspectJAutoProxy
public class Config {


}
