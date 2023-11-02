package com.denismiagkov.walletservice.infrastructure.liquibase;

import com.denismiagkov.walletservice.init.DatabaseConnection;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;

@Component
public class LiquibaseApp {
    DatabaseConnection dbConnection;

    public LiquibaseApp(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public Liquibase start() {
        String queryCreateMigrationSchema = "CREATE SCHEMA IF NOT EXISTS migration";

        try (Connection connection = this.dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryCreateMigrationSchema)) {
            statement.executeUpdate();

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                    new JdbcConnection(connection));
            database.setLiquibaseSchemaName("migration");
            Liquibase liquibase = new Liquibase("db/changelog/changelog.xml",
                    new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("Миграции успешно выполнены!");
            return liquibase;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
