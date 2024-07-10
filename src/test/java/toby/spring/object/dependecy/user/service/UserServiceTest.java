package toby.spring.object.dependecy.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static toby.spring.object.dependecy.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static toby.spring.object.dependecy.user.service.UserServiceImpl.MIN_RECCOMEND_ROR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import toby.spring.object.dependecy.dao.UserDao;
import toby.spring.object.dependecy.user.domain.Level;
import toby.spring.object.dependecy.user.domain.User;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
class UserServiceTest {
  @Autowired UserService userService;
  @Autowired UserService testUserService;
  @Autowired UserDao userDao;
  @Autowired DataSource dataSource;
  @Autowired PlatformTransactionManager transactionManager;
  @Autowired MailSender mailSender;
  List<User> users;
  @Autowired ApplicationContext context;

  @BeforeEach
  public void setUp() {
    users =
        Arrays.asList(
            new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "mail"),
            new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "mail"),
            new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_ROR_GOLD - 1, "mail"),
            new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_ROR_GOLD, "mail"),
            new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "mail"));
  }

  @Test
  public void bean() {
    assertThat(this.userService).isNotNull();
  }

  @Test
  @DirtiesContext // 컨텍스트의 DI 설정을 변경하는 테스트
  public void upgradeLevels() throws Exception {
    // 고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성하면 된다.
    UserServiceImpl userServiceImpl = new UserServiceImpl();
    // 목 오브젝트로 만든 UserDao를 직접 DI 해준다.
    MockUserDao mockUserDao = new MockUserDao(this.users);

    userServiceImpl.setUserDao(mockUserDao);

    MocMailSender mocMailSender = new MocMailSender();
    userServiceImpl.setMailSender(mocMailSender);

    userServiceImpl.upgradeLevels();

    // MockUserDao로부터 업데이트 결과를 가져온다.
    List<User> updated = mockUserDao.getUpdated();
    assertThat(updated.size()).isEqualTo(2);
    checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
    checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

    List<String> request = mocMailSender.getRequests();
    assertThat(request.size()).isEqualTo(2);
    assertThat(request.get(0)).isEqualTo(users.get(1).getMail());
    assertThat(request.get(1)).isEqualTo(users.get(3).getMail());
  }

  @Test
  public void add() {
    userDao.deleteAll();
    // Gold level
    User userWithLevel = users.get(4);
    // null level
    User userWithoutLevel = users.get(0);
    userWithoutLevel.setLevel(null);
    userService.add(userWithLevel);
    userService.add(userWithoutLevel);

    User userWithLevelRead = userDao.get(userWithLevel.getId());
    User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

    assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
    assertThat(userWithoutLevelRead.getLevel()).isEqualTo(userWithoutLevel.getLevel());
  }

  @Test
  public void upgradeAllOrNothing() throws Exception {
    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    try {
      this.testUserService.upgradeLevels();
      fail("TestUserServiceException expected");
    } catch (TestUserServiceException e) {
    }

    checkLevel1Upgraded(users.get(1), false);
  }

  @Test
  public void mockUpgradeLevels() {
    UserServiceImpl userServiceImpl = new UserServiceImpl();

    UserDao mockUserDao = mock(UserDao.class);
    when(mockUserDao.getAll()).thenReturn(this.users);
    userServiceImpl.setUserDao(mockUserDao);

    MailSender mockMailSender = mock(MailSender.class);
    userServiceImpl.setMailSender(mockMailSender);

    userServiceImpl.upgradeLevels();

    verify(mockUserDao, times(2)).update(any(User.class));
    verify(mockUserDao, times(2)).update(any(User.class));
    verify(mockUserDao).update(users.get(1));
    assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
    verify(mockUserDao).update(users.get(3));
    assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

    ArgumentCaptor<SimpleMailMessage> mailMessageArg =
        ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mockMailSender, times(2)).send(mailMessageArg.capture());
    List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
    assertThat(Objects.requireNonNull(mailMessages.get(0).getTo())[0])
        .isEqualTo(users.get(1).getMail());
    assertThat(Objects.requireNonNull(mailMessages.get(1).getTo())[0])
        .isEqualTo(users.get(3).getMail());
  }

  @Test
  public void advisorAutoProxyCreator() {
    assertThat(java.lang.reflect.Proxy.isProxyClass(testUserService.getClass())).isTrue();
  }

  @Test
  public void readOnlyTransactionAttribute() {
    assertThrows(TransientDataAccessResourceException.class, () -> testUserService.getAll());
  }

  private void checkLevel1Upgraded(User user, boolean upgraded) {
    User userUpdate = userDao.get(user.getId());
    if (upgraded) {
      assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
    } else {
      assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
    }
  }

  private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
    assertThat(updated.getId()).isEqualTo(expectedId);
    assertThat(updated.getLevel()).isEqualTo(expectedLevel);
  }

  static class TestUserServiceImpl extends UserServiceImpl {
    private String id = "madnite1";

    // UserService method override
    protected void upgradeLevel(User user) {
      if (user.getId().equals(this.id)) throw new TestUserServiceException();
      super.upgradeLevel(user);
    }

    @Override
    public List<User> getAll() {
      for (User user : super.getAll()) {
        super.update(user);
      }
      return null;
    }
  }

  static class TestUserServiceException extends RuntimeException {}

  static class MocMailSender implements MailSender {
    private List<String> requests = new ArrayList<>();

    // UserService로부터 전송 요청을 받은 이메일 주소를 저장해두고 이를 읽을 수 있게 한다.
    public List<String> getRequests() {
      return requests;
    }

    @Override
    public void send(SimpleMailMessage mailMessage) throws MailException {
      // 전송 요청을 받은 이메일 주소를 저장해둔다.
      requests.add(Objects.requireNonNull(mailMessage.getTo())[0]);
    }

    @Override
    public void send(SimpleMailMessage... mailMessage) throws MailException {}
  }

  static class MockUserDao implements UserDao {
    private List<User> users;
    private List<User> updated = new ArrayList<>();

    private MockUserDao(List<User> users) {
      this.users = users;
    }

    public List<User> getUpdated() {
      return this.updated;
    }

    // 스텁 기능 제공
    @Override
    public List<User> getAll() {
      return this.users;
    }

    // 목 오브젝트 기능 제공
    @Override
    public void update(User user) {
      updated.add(user);
    }

    // 테스트에 사용되지 않는 메소드
    @Override
    public void add(User user) {
      throw new UnsupportedOperationException();
    }

    @Override
    public User get(String id) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getCount() {
      throw new UnsupportedOperationException();
    }
  }
}
