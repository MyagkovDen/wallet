package com.denismiagkov.walletservice.init;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

/**
 * Класс описывает конфигурационный файл, включая путь и способ извлечения данных.
 */
@Component
public class PropertyFile {

    private static final Properties PROPERTIES = new Properties();

    public PropertyFile() {
    }

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (var inputStream = PropertyFile.class.getClassLoader().getResourceAsStream("config.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperties(String parameter) {
        return PROPERTIES.getProperty(parameter);
    }

}
