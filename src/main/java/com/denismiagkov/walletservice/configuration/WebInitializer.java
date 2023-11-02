package com.denismiagkov.walletservice.configuration;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {

        System.out.println("ENTERED INTO CLASS");
        return new Class[]{Config.class};
    }

    @Override
    protected String[] getServletMappings() {

        System.out.println("ENTERED INTO PROGRAM");
        return new String[]{"/"};
    }
}
