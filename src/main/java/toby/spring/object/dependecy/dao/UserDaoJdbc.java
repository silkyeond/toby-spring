package toby.spring.object.dependecy.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import toby.spring.object.dependecy.user.domain.Level;
import toby.spring.object.dependecy.user.domain.User;
import toby.spring.object.dependecy.user.sqlservice.SqlService;

public class UserDaoJdbc implements UserDao {
  private JdbcTemplate jdbcTemplate;

  public void setDataSource(DataSource dataSource) {
    // dataSource 오브젝트는 JdbcTemplate을 만든 후에는 사용하지 않아 저장해두지 않아도 됨
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  private SqlService sqlService;

  public void setSqlService(SqlService sqlService) {
    this.sqlService = sqlService;
  }

  public void add(final User user) {
    this.jdbcTemplate.update(
        this.sqlService.getSql("userAdd"),
        user.getId(),
        user.getName(),
        user.getPassword(),
        user.getLevel().intVal(),
        user.getLogin(),
        user.getRecommend(),
        user.getMail());
  }

  public User get(String id) {
    return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGet"), this.userMapper);
  }

  public List<User> getAll() {
    return this.jdbcTemplate.query(this.sqlService.getSql("userGetAll"), this.userMapper);
  }

  public void deleteAll() {
    this.jdbcTemplate.update(this.sqlService.getSql("userDeleteAll"));
  }

  public int getCount() {
    return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGetCount"), int.class);
  }

  @Override
  public void update(User user) {
    this.jdbcTemplate.update(
        this.sqlService.getSql("userUpdate"),
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
