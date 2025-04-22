package com.ethicalhacking.filecmr;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DatabaseLogger {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void logDatabaseInfo() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            log.info("🔗 Datasource URL: {}", metaData.getURL());
            log.info("👤 Connected as: {}", metaData.getUserName());
            log.info("🛠️  Driver: {} {}", metaData.getDriverName(), metaData.getDriverVersion());
            log.info("🧩 Database: {} {}", metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
            log.info("🔄 Auto-commit: {}", connection.getAutoCommit());
            log.info("🔒 Isolation level: {}", resolveIsolationLevel(connection.getTransactionIsolation()));

        } catch (SQLException e) {
            log.error("❌ Errore durante il recupero delle info dal database: {}", e.getMessage(), e);
        }
    }

    private String resolveIsolationLevel(int level) {
        return switch (level) {
            case Connection.TRANSACTION_NONE -> "NONE";
            case Connection.TRANSACTION_READ_COMMITTED -> "READ_COMMITTED";
            case Connection.TRANSACTION_READ_UNCOMMITTED -> "READ_UNCOMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ -> "REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE -> "SERIALIZABLE";
            default -> "UNKNOWN";
        };
    }
}
