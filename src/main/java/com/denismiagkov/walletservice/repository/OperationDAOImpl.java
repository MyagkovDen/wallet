package com.denismiagkov.walletservice.repository;

import com.denismiagkov.walletservice.domain.model.Operation;
import com.denismiagkov.walletservice.infrastructure.DatabaseConnection;
import com.denismiagkov.walletservice.repository.interfaces.OperationDAO;

import java.sql.*;
import java.util.*;

/**
 * Класс отвечает за доступ к данным о действиях игроков в приложении, хранящимся в базе данных. Предоставляет методы
 * для создания, чтения, обновления и удаления данных.
 */
public class OperationDAOImpl implements OperationDAO {

    /**
     * Соединение с базой данных
     */
    DatabaseConnection dbConnection;

    /**
     * Конструктор класса
     */
    public OperationDAOImpl() {
        this.dbConnection = new DatabaseConnection();
    }

    /**
     * Конструктор класса с параметром(для тестирования)
     *
     * @param dbConnection подключение к базе данных
     * */
    public OperationDAOImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    /**
     * Метод сохраняет данные о совершенном дейстии игрока в базе данных
     *
     * @param operation действие игрока в приложении
     */
    @Override
    public void saveOperation(Operation operation) {
        String insertOperation = "INSERT INTO wallet.operations (operation_type, perform_time, operation_status," +
                "player_id) VALUES (?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement prStatement = connection.prepareStatement(insertOperation)) {
            prStatement.setString(1, operation.getType().toString());
            prStatement.setTimestamp(2, operation.getTime());
            prStatement.setString(3, operation.getStatus().toString());
            prStatement.setInt(4, operation.getPlayerId());
            prStatement.executeUpdate();
            operation.setId(getOperationId(operation));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод вохвращает сведения о действиях, совершенных всеми игроками в приложении
     *
     * @return List<String>
     * */
    @Override
    public List<String> getLog() {
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM wallet.operations");
            List<String> log = new ArrayList<>();
            while (rs.next()) {
                String type = rs.getString("operation_type");
                String time = rs.getString("perform_time");
                String status = rs.getString("operation_status");
                String playerId = rs.getString("player_id");
                String operation = "{" + type + " - " + time + " - " + status + " - " + playerId + "}";
                log.add(operation);
            }
            return log;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Метод возвращает id определенного действия, совершенного игроком
     *
     * @param operation действие, совершенное игроком
     * @return int id действия
     * */
    public int getOperationId(Operation operation) {
        String getOperationId = "SELECT id FROM wallet.operations WHERE operation_type = ? AND perform_time = ? " +
                "AND operation_status = ? AND player_id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement prStatement = connection.prepareStatement(getOperationId)) {
            prStatement.setString(1, operation.getType().toString());
            prStatement.setTimestamp(2, operation.getTime());
            prStatement.setString(3, operation.getStatus().toString());
            prStatement.setInt(4, operation.getPlayerId());
            ResultSet rs = prStatement.executeQuery();
            while (rs.next()) {
                int operationId = rs.getInt("id");
                return operationId;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }
}
