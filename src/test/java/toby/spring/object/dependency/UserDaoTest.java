package toby.spring.object.dependency;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import toby.spring.object.dependecy.User;
import toby.spring.object.dependecy.UserDao;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserDaoTest {
  //  @Autowired private ApplicationContext context;

  // setUp() 메소드에서 만드는 오브젝트를 테스트 메소드로 사용할 수 있도록 인스턴스 변수로 선언
  @Autowired private UserDao dao;
  private User user1;
  private User user2;
  private User user3;

  @BeforeEach
  public void setUp() {
    //    System.out.println(this.context);
    //    System.out.println(this);
    //    this.dao = this.context.getBean("userDao", UserDao.class);
    this.user1 = new User("gyumee", "박상철", "springno1");
    this.user2 = new User("leegw700", "이길원", "springno2");
    this.user3 = new User("bumjin", "박범진", "springno3");
  }

  @Test
  public void addAndGet() {
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
  public void count() {
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
  public void getUserFailure() {
    dao.deleteAll();
    assertThat(dao.getCount()).isEqualTo(0);
    assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unknown_id"));
  }

  @Test
  public void getAll() {
    dao.deleteAll();

    List<User> users0 = dao.getAll();
    assertThat(users0.size()).isEqualTo(0);

    dao.add(user1);
    List<User> users1 = dao.getAll();
    assertThat(users1.size()).isEqualTo(1);
    checkSameUser(user1, users1.get(0));

    dao.add(user2);
    List<User> users2 = dao.getAll();
    assertThat(users2.size()).isEqualTo(2);
    checkSameUser(user1, users2.get(0));
    checkSameUser(user2, users2.get(1));

    dao.add(user3);
    List<User> users3 = dao.getAll();
    assertThat(users3.size()).isEqualTo(3);
    checkSameUser(user3, users3.get(0));
    checkSameUser(user1, users3.get(1));
    checkSameUser(user2, users3.get(2));
  }

  private void checkSameUser(User user1, User user2) {
    assertThat(user1.getId()).isEqualTo(user2.getId());
    assertThat(user1.getName()).isEqualTo(user2.getName());
    assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
  }
}
