package com.denismiagkov.walletservice.domain.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Класс описывает транзакцию - кредитную (добавление денежных средств)
 * или дебетовую (списание денежных средств) операцию по счету игрока
 */
public class Transaction {
    /**
     * Уникальный идентификатор транзакции
     */
    String id;
    /**
     * Номер счета, на котором выполняется транзакция
     */
    String accountNumber;
    /**
     * Дата и время выполнения транзакции
     */
    Timestamp time;
    /**
     * Тип транзакции - дебетовая или кредитная
     *
     * @see TransactionType
     */
    TransactionType type;
    /**
     * Сумма транзакции
     */
    BigDecimal amount;


    /**
     * Конструктор класса
     */
    public Transaction(String id, Account account, Timestamp time, TransactionType type,
                       BigDecimal amount) {
        this.id = id;
        this.accountNumber = account.getNumber();
        this.time = time;
        this.type = type;
        this.amount = amount;
    }

    /**
     * Мнтод возвращает номер счета
     */
    public String getAccount() {
        return accountNumber;
    }

    /**
     * Метод устанавливает номер счета
     */
    public void setAccount(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Метод toString()
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' + "\n" +
                ", accountNumber=" + accountNumber + "\n" +
                ", time=" + time + "\n" +
                ", type=" + type + "\n" +
                ", amount=" + amount +
                '}';
    }

    /**
     * Метод equals()
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Метод hashcode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
