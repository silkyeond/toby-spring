package toby.spring.object.dependecy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;

public class UserDao {
  private DataSource dataSource;

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  private ConnectionMaker connectionMaker;

  // 의존관계 주입
  //  public UserDao(ConnectionMaker connectionMaker) {
  //    this.connectionMaker = connectionMaker;
  //  }

  // 수정자 메소드 DI 방식
  public void setConnectionMaker(ConnectionMaker connectionMaker) {
    this.connectionMaker = connectionMaker;
  }

  //  //   의존 관계 검색
  //  public UserDao() {
  //    // IoC 컨테이너인 DaoFactory에게 요청
  //    DaoFactory daoFactory = new DaoFactory();
  //    this.connectionMaker = daoFactory.connectionMaker();
  //
  // 스프링 의존 관계 검색
  //    AnnotationConfigApplicationContext context =
  //        new AnnotationConfigApplicationContext(DaoFactory.class);
  //    this.connectionMaker = context.getBean("connectionMaker", ConnectionMaker.class);
  //  }

  public void add(final User user) throws SQLException {

    jdbcContextWithStatementStrategy(
        new StatementStrategy() {
          public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
            PreparedStatement ps =
                c.prepareStatement("insert into users(id, name, password) values (?,?,?)");
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());
            return ps;
          }
        });
  }

  public User get(String id) throws SQLException {
    //    Connection c = connectionMaker.makeConnection();
    Connection c = dataSource.getConnection();
    PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
    ps.setString(1, id);

    ResultSet rs = ps.executeQuery();
    User user = null;

    if (rs.next()) {
      user = new User();
      user.setId(rs.getString("id"));
      user.setName(rs.getString("name"));
      user.setPassword(rs.getString("password"));
    }

    rs.close();
    ps.close();
    c.close();

    if (user == null) {
      throw new EmptyResultDataAccessException(1);
    }

    return user;
  }

  public void deleteAll() throws SQLException {
jdbcContextWithStatementStrategy(
        new StatementStrategy() {
          public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
            return c.prepareStatement("delete from users");
          }
        }
);
  }

  /*
   * @Params StatementStrategy stmt 클라이언트가 컨텍스트 호출할 때 넘겨줄 전략 파라미터
   */
  public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
    Connection c = null;
    PreparedStatement ps = null;

    try {
      c = dataSource.getConnection();
      ps = stmt.makePreparedStatement(c);

      ps.executeUpdate();
    } catch (SQLException e) {
      throw e;
    } finally {
      if (ps != null) {
        try {
          ps.close();
        } catch (SQLException e) {
        }
      }
      if (c != null) {
        try {
          c.close();
        } catch (SQLException e) {
        }
      }
    }
  }

  public int getCount() throws SQLException {
    Connection c = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {

      c = dataSource.getConnection();
      ps = c.prepareStatement("select count(*) from users");
      rs = ps.executeQuery();
      rs.next();
      return rs.getInt(1);
    } catch (SQLException e) {
      throw e;
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
        }
      }
      if (ps != null) {
        try {
          ps.close();
        } catch (SQLException e) {
        }
      }
      if (c != null) {
        try {
          c.close();
        } catch (SQLException e) {
        }
      }
    }
  }
}
