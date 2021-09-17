package ch.puzzle.marinabackend;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

public class LiquibaseTest {

    private Database database;
    private java.sql.Connection conn = null;

    @Test
    public void shouldLoadInitialDataSetInEmptyDatabase()
            throws LiquibaseException, SQLException, ClassNotFoundException {
        // given
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:liquibase;MODE=PostgreSQL", "sa", "");

        database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
        Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.yaml", new ClassLoaderResourceAccessor(),
                database);

        // when
        liquibase.update(new Contexts(), new LabelExpression());

        // then must not fail
        if (conn != null) {
            conn.close();
        }
    }
}
