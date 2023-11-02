package com.denismiagkov.walletservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "com.denismiagkov.walletservice")
@EnableWebMvc
@EnableAspectJAutoProxy
public class Config {

//    @Bean
//    public DataSource dataSource() {
//        ComboPooledDataSource dataSource = new ComboPooledDataSource();
//        try {
//            dataSource.setDriverClass("org.postgresql.Driver");
//            dataSource.setJdbcUrl("jdbc:postgresql://localhost:5431/wallet");
//            dataSource.setUser("wallet");
//            dataSource.setPassword("123");
//        } catch (PropertyVetoException e) {
//            throw new RuntimeException(e);
//        }
//        return dataSource;
//    }

//    @Bean
//    public LocalSessionFactoryBean sessionFactory() {
//        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//        sessionFactory.setDataSource(dataSource());
//        sessionFactory.setPackagesToScan("com.denismiagkov.walletservice.domain.entity");
//
//        Properties hibernateProperties = new Properties();
//        hibernateProperties.setProperty("hibernate.Dialect", "org.hibernate.dialect.PostgreSQL94Dialect");
//        hibernateProperties.setProperty("hibernate.show_sql", "true");
//
//        sessionFactory.setHibernateProperties(hibernateProperties);
//        return sessionFactory;
//    }
//
//    @Bean
//    HibernateTransactionManager transactionManager(){
//        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
//        transactionManager.setSessionFactory(sessionFactory().getObject());
//        return transactionManager;
//    }

    @Bean
    public ViewResolver viewResolver(){
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/view/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }


}
