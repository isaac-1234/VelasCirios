import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "postgresql://postgres:AkUVKvfvREvkMkTpbIRivNHjsNIQCtHq@postgres.railway.internal:5432/railway";
    private static final String USER = "postgres";
    private static final String PASSWORD = "AkUVKvfvREvkMkTpbIRivNHjsNIQCtHq";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}