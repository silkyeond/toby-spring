package toby.spring.object.dependecy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NUserDao extends UserDao {
  public Connection getConnection() throws ClassNotFoundException, SQLException {
    // N사 DB 생성코드
    Class.forName("com.mysql.jdbc.Driver");
    Connection c =
        DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
    return c;
  }
}
