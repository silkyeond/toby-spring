import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import toby.spring.object.dependecy.User;
import toby.spring.object.dependecy.UserDao;

public class UserDaoTest {
  // setUp() 메소드에서 만드는 오브젝트를 테스트 메소드로 사용할 수 있도록 인스턴스 변수로 선언
  private UserDao dao;
  private User user1;
  private User user2;
  private User user3;

  @BeforeEach
  public void setUp() {
    ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
    this.dao = context.getBean("userDao", UserDao.class);

    this.user1 = new User("gyumee", "박상철", "springno1");
    this.user2 = new User("leegw700", "이길원", "springno2");
    this.user3 = new User("bumjin", "박범진", "springno3");
  }

  @Test
  public void addAndGet() throws SQLException {
    dao.deleteAll();
    assertThat(dao.getCount()).isEqualTo(0);

    dao.add(user1);
    dao.add(user2);
    assertThat(dao.getCount()).isEqualTo(2);

    User userGet1 = dao.get(user1.getId());
    assertThat(userGet1.getName()).isEqualTo(user1.getName());
    assertThat(userGet1.getPassword()).isEqualTo(user1.getPassword());

    User userGet2 = dao.get(user2.getId());
    assertThat(userGet2.getName()).isEqualTo(user2.getName());
    assertThat(userGet2.getPassword()).isEqualTo(user2.getPassword());
  }

  @Test
  public void count() throws SQLException {
    dao.deleteAll();
    assertThat(dao.getCount()).isEqualTo(0);

    dao.add(user1);
    assertThat(dao.getCount()).isEqualTo(1);

    dao.add(user2);
    assertThat(dao.getCount()).isEqualTo(2);

    dao.add(user3);
    assertThat(dao.getCount()).isEqualTo(3);
  }

  @Test
  public void getUserFailure() throws SQLException {
    dao.deleteAll();
    assertThat(dao.getCount()).isEqualTo(0);
    assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unknown_id"));
  }
}
