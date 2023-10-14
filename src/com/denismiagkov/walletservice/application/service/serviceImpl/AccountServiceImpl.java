package com.denismiagkov.walletservice.application.service.serviceImpl;

import com.denismiagkov.walletservice.application.service.Service;
import com.denismiagkov.walletservice.domain.model.Account;
import com.denismiagkov.walletservice.domain.model.Player;
import com.denismiagkov.walletservice.domain.model.Transaction;
import com.denismiagkov.walletservice.domain.service.AccountService;

import java.math.BigDecimal;
import java.util.*;

/**
 * Низкоуровневый сервис, реализующий методы, связанные с <strong>созданием денежного счета игрока,
 * просмотром текущего баланса и истории операций по нему</strong>.
 * Описанные в классе методы вызываются высокоуровневым сервисом для выполнения конкретных специализированных
 * операций, соответствующих бизнес-логике.
 */
public class AccountServiceImpl implements AccountService {

    /**
     * Перечень денежных счетов игроков
     */
    Set<String> accountsInventory;

    /**
     * Конструктор класса
     * */
    public AccountServiceImpl() {
        this.accountsInventory = new HashSet<>();
    }


    /**
     * Метод имитирует процесс присвоения номера создаваемому счету при регистрации игрока
     * путем использования генератора случайных чисел {@link AccountServiceImpl#createAccount(Player)}
     *
     * @return номер денежного счета
     */
    public String getAccountNumber() {
        while (true) {
            Random n = new Random();
            String number = String.valueOf(n.nextInt(100_000_000, 999_000_000));
            if (!accountsInventory.contains(number)) {
                return number;
            }
        }
    }

    /**
     * Метод создает денежный счет. Применяется высокоуровневым сервисом при создании
     * и регистрации нового игрока {@link Service#registerPlayer(String, String, String, String, String)}
     *
     * @param player игрок, для которого создается денежный счет
     */
    @Override
    public void createAccount(Player player) {
        Account account = new Account(getAccountNumber());
        player.setAccount(account);
        accountsInventory.add(account.getNumber());
    }

    /**
     * Метод возвращает текущий баланс денежного счета игрока
     *
     * @param player игрок, о состоянии баланса счета которого запрашивается информация
     * @return текущий баланс денежного счета игрока
     */
    @Override
    public BigDecimal getCurrentBalance(Player player) {
        return player.getAccount().getBalance();
    }

    /**
     * Метод сообщает историю дебетовых и кредитных операций по денежному счету игрока
     *
     * @param player игрок, об истории транзакций которого запрашивается информация
     * @return список дебетовых и кредитных операций по счету игрока
     */
    @Override
    public List<Transaction> showTransactionsHistory(Player player) {
        return player.getAccount().getTransactionInventory();
    }


}
