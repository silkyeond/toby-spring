package toby.spring.object.dependecy.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import toby.spring.object.dependecy.user.domain.Level;
import toby.spring.object.dependecy.user.domain.User;

public class UserDaoJdbc implements UserDao {
  private JdbcTemplate jdbcTemplate;

  public void setDataSource(DataSource dataSource) {
    // dataSource 오브젝트는 JdbcTemplate을 만든 후에는 사용하지 않아 저장해두지 않아도 됨
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public void add(final User user) {
    this.jdbcTemplate.update(
        "insert into users(id, name, password, level, login, recommend, mail) values(?,?,?,?,?,?,?)",
        user.getId(),
        user.getName(),
        user.getPassword(),
        user.getLevel().intVal(),
        user.getLogin(),
        user.getRecommend(),
        user.getMail());
  }

  public User get(String id) {
    return this.jdbcTemplate.queryForObject(
        "select * from users where id=?", new Object[] {id}, this.userMapper);
  }

  public void deleteAll() {
    this.jdbcTemplate.update("delete from users");
  }

  public int getCount() {
    return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
  }

  public List<User> getAll() {
    return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
  }

  @Override
  public void update(User user) {
    this.jdbcTemplate.update(
        "update users set name = ?, password = ?, level = ?, login = ?, recommend = ?, mail = ? where id = ?",
        user.getName(),
        user.getPassword(),
        user.getLevel().intVal(),
        user.getLogin(),
        user.getRecommend(),
        user.getMail(),
        user.getId());
  }

  private RowMapper<User> userMapper =
      new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
          User user = new User();
          user.setId(rs.getString("id"));
          user.setName(rs.getString("name"));
          user.setPassword(rs.getString("password"));
          user.setLevel(Level.valueOf(rs.getInt("level")));
          user.setLogin(rs.getInt("login"));
          user.setRecommend(rs.getInt("recommend"));
          user.setMail(rs.getString("mail"));
          return user;
        }
      };
}
