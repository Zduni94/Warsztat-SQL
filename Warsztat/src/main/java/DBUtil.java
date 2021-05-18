import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static final String DB_USER = "root";
    private static final String DB_PASS = "coderslab";
    private static final String DB_SERVER_URL = "jdbc.mysql://localhost:3306/";
    private static final String DB_URL_PROPERTIES = "?useSSL=false&characterEncoding=UTF8";
    private static final String DEFAULT_DB = "workshop2";

    public static Connection connect() throws SQLException {
        return connect(DEFAULT_DB);
    }

    public static Connection connect(String dbName) throws SQLException {
        String url = DB_SERVER_URL + dbName + DB_URL_PROPERTIES;
        return DriverManager.getConnection(url, DB_USER, DB_PASS);
    }
}
