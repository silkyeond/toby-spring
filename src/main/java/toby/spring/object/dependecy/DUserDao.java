package toby.spring.object.dependecy;

import java.sql.Connection;

public class DUserDao extends UserDao {
  public Connection getConnection() {
    // D사 DB 생성코드
    return null;
  }
}