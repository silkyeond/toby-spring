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
import toby.spring.object.dependecy.dao.UserDao;
import toby.spring.object.dependecy.user.domain.Level;
import toby.spring.object.dependecy.user.domain.User;

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
    this.user1 = new User("gyumee", "박상철", "springno1", Level.BASIC, 1, 0,"mail");
    this.user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10,"mail");
    this.user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40,"mail");
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

  @Test
  public void update() {
    dao.deleteAll();

    // 수정할 사용자
    dao.add(user1);
    // 수정하지 않을 사용자
    dao.add(user2);

    user1.setName("오민규");
    user1.setPassword("springno6");
    user1.setLevel(Level.GOLD);
    user1.setLogin(1000);
    user1.setRecommend(999);
    dao.update(user1);

    User user1update = dao.get(user1.getId());
    checkSameUser(user1, user1update);

    User user2same = dao.get(user2.getId());
    checkSameUser(user2, user2same);
  }

  private void checkSameUser(User user1, User user2) {
    assertThat(user1.getId()).isEqualTo(user2.getId());
    assertThat(user1.getName()).isEqualTo(user2.getName());
    assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
    assertThat(user1.getLevel()).isEqualTo(user2.getLevel());
    assertThat(user1.getLogin()).isEqualTo(user2.getLogin());
    assertThat(user1.getRecommend()).isEqualTo(user2.getRecommend());
    assertThat(user1.getMail()).isEqualTo(user2.getMail());
  }
}
