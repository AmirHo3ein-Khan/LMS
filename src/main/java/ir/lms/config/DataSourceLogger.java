package ir.lms.config;

import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DataSourceLogger {

    private final DataSource dataSource;

    public DataSourceLogger(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logDatabaseInfo() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            var meta = conn.getMetaData();
            System.out.println("\u001B[93m=== Database Info ===\u001B[0m");
            System.out.println("\u001B[32mURL:\u001B[0m " + meta.getURL());
            System.out.println("\u001B[32mDriver:\u001B[0m " + meta.getDriverName());
            System.out.println("\u001B[32mVersion:\u001B[0m " + meta.getDatabaseProductVersion());
            System.out.println("\u001B[32mAutocommit:\u001B[0m " + conn.getAutoCommit());
            System.out.println("\u001B[32mIsolation:\u001B[0m " + conn.getTransactionIsolation());
        }
    }
}
