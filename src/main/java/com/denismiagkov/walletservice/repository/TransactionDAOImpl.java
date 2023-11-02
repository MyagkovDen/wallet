package com.denismiagkov.walletservice.repository;

import com.denismiagkov.walletservice.domain.model.Transaction;
import com.denismiagkov.walletservice.init.DatabaseConnection;
import com.denismiagkov.walletservice.repository.interfaces.TransactionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;

/**
 * Класс отвечает за доступ к данным о транзакциях, хранящимся в базе данных. Предоставляет методы для создания,
 * чтения, обновления и удаления данных.
 */
@Repository
public class TransactionDAOImpl implements TransactionDAO {

    /**
     * Соединение с базой данных
     */
    DatabaseConnection dbConnection;

    /**
     * Базовый конструктор класса
     */
    @Autowired
    public TransactionDAOImpl() {
        this.dbConnection = new DatabaseConnection();
    }

    /**
     * Конструктор класса с параметром(для целей тестирования)
     *
     * @param dbConnection подключение к базе данных
     */
    public TransactionDAOImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    /**
     * Метод сохраняет в базе данных сведения о совершенной игроком транзакции
     *
     * @param transaction транзакция, совершенная игроком (пополнение/списание средств со счета)
     */
    @Override
    public void saveTransaction(Transaction transaction) {
        String insertTransaction = "INSERT INTO wallet.transactions (commit_time, item_type, amount, account) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement prStatement = connection.prepareStatement(insertTransaction)) {
            prStatement.setTimestamp(1, transaction.getTime());
            prStatement.setString(2, transaction.getType().toString());
            prStatement.setBigDecimal(3, transaction.getAmount());
            prStatement.setInt(4, transaction.getAccountId());
            prStatement.executeUpdate();
            transaction.setId(getTransactionId(transaction));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод возвращает id опредленной транзакции
     *
     * @return int id транзакции
     */
    public int getTransactionId(Transaction transaction) {
        String queryTransactionId = "SELECT id FROM wallet.transactions WHERE commit_time = ? AND item_type = ? " +
                "AND amount = ? AND account = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement prStatement = connection.prepareStatement(queryTransactionId)) {
            prStatement.setTimestamp(1, transaction.getTime());
            prStatement.setString(2, transaction.getType().toString());
            prStatement.setBigDecimal(3, transaction.getAmount());
            prStatement.setInt(4, transaction.getAccountId());
            ResultSet rs = prStatement.executeQuery();
            while (rs.next()) {
                int transactionId = rs.getInt("id");
                return transactionId;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }
}

