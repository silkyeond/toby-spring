package toby.spring.object.dependecy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class UserDao {
  private SimpleConnectionMaker simpleConnectionMaker;

  // 한 번만 만들어 인스턴스 변수에 저장해두고 메소드에서 사용하게 한다.
  public UserDao() {
    simpleConnectionMaker = new SimpleConnectionMaker();
  }

  public void add(User user) throws ClassNotFoundException, SQLException {
    Connection c = simpleConnectionMaker.makeNewConnection();
    PreparedStatement ps =
        c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
    ps.setString(1, user.getId());
    ps.setString(2, user.getName());
    ps.setString(3, user.getPassword());

    ps.executeUpdate();

    ps.close();
    c.close();
  }

  public User get(String id) throws ClassNotFoundException, SQLException {
    Connection c = simpleConnectionMaker.makeNewConnection();
    PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
    ps.setString(1, id);

    ResultSet rs = ps.executeQuery();
    rs.next();
    User user = new User();
    user.setId(rs.getString("id"));
    user.setName(rs.getString("name"));
    user.setPassword(rs.getString("password"));

    rs.close();
    ps.close();
    c.close();
    return user;
  }
}
