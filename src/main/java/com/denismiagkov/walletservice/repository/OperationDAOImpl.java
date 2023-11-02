package com.denismiagkov.walletservice.repository;

import com.denismiagkov.walletservice.domain.model.Operation;
import com.denismiagkov.walletservice.domain.model.OperationStatus;
import com.denismiagkov.walletservice.domain.model.OperationType;
import com.denismiagkov.walletservice.init.DatabaseConnection;
import com.denismiagkov.walletservice.repository.interfaces.OperationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

/**
 * Класс отвечает за доступ к данным о действиях игроков в приложении, хранящимся в базе данных. Предоставляет методы
 * для создания, чтения, обновления и удаления данных.
 */
@Repository
public class OperationDAOImpl implements OperationDAO {

    /**
     * Соединение с базой данных
     */
    DatabaseConnection dbConnection;

    /**
     * Конструктор класса
     */
    @Autowired
    public OperationDAOImpl() {
        this.dbConnection = new DatabaseConnection();
    }

    /**
     * Конструктор класса с параметром(для тестирования)
     *
     * @param dbConnection подключение к базе данных
     */
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
     */
    @Override
    public List<Operation> getLog() {
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM wallet.operations");
            List<Operation> log = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String stringType = rs.getString("operation_type");
                OperationType type = OperationType.valueOf(stringType);
                Timestamp time = rs.getTimestamp("perform_time");
                String stringStatus = rs.getString("operation_status");
                OperationStatus status = OperationStatus.valueOf(stringStatus);
                int playerId = rs.getInt("player_id");
                Operation operation = new Operation(type, time, status, playerId);
                operation.setId(id);
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
     */
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
