package com.denismiagkov.walletservice.infrastructure.in.commands;

import com.denismiagkov.walletservice.infrastructure.in.Console;

/**
 * Класс описывает команду (действие) меню зарегистрировать нового игрока.
 * Является наследником класса {@link Command}.
 */
public class OptionRegisterPlayer extends Command {

    public OptionRegisterPlayer(Console console) {
        super(console);
    }

    /**
     * Метод определяет название команды (действия)
     *
     * @return название команды (действия)
     */
    @Override
    public String getDescription() {
        return "Зарегистрироваться";
    }

    /**
     * Метод вызывает метод {@link Console#getDataToRegisterPlayer()} (String, String)}
     * для регистрации нового игрока.
     */
    @Override
    public void execute() {
        getConsole().getDataToRegisterPlayer();
    }


    /**
     * Метод не используется
     * */
    @Override
    public void execute(String login, String password) {

    }
}
