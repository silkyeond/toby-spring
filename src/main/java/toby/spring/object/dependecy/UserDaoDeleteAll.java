package toby.spring.object.dependecy;

import toby.spring.object.dependecy.dao.UserDaoJdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// makeStatement()를 구현한 UserDao 서브 클래스
public class UserDaoDeleteAll extends UserDaoJdbc {
  protected PreparedStatement makeStatement(Connection c) throws SQLException {
    PreparedStatement ps = c.prepareStatement("delete from users");
    return ps;
  }
}
