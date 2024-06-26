package toby.spring.object.dependecy.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.beans.factory.annotation.Autowired;
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
  @Autowired UserServiceImpl userServiceImpl;
  @Autowired UserDao userDao;
  @Autowired DataSource dataSource;
  @Autowired PlatformTransactionManager transactionManager;
  @Autowired MailSender mailSender;
  List<User> users;

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
    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    MocMailSender mocMailSender = new MocMailSender();
    userServiceImpl.setMailSender(mocMailSender);

    userService.upgradeLevels();

    checkLevel1Upgraded(users.get(0), false);
    checkLevel1Upgraded(users.get(1), true);
    checkLevel1Upgraded(users.get(2), false);
    checkLevel1Upgraded(users.get(3), true);
    checkLevel1Upgraded(users.get(4), false);

    List<String> request = mocMailSender.getRequests();
    assertThat(request.size()).isEqualTo(2);
    assertThat(request.get(0)).isEqualTo(users.get(0).getMail());
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
  public void upgradeAllOrNothing() {
    TestUserService testUserService = new TestUserService(users.get(3).getId());
    // 테스트 메소드에서만 특별한 목적으로 사용되는 것으로 동작하는데 필요한 DAO만 수동 DI해준다.
    testUserService.setUserDao(this.userDao);
    testUserService.setMailSender(mailSender);

    UserServiceTx txUserService = new UserServiceTx();
    txUserService.setTransactionManager(transactionManager);
    txUserService.setUserService(testUserService);

    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    try {
      txUserService.upgradeLevels();
      fail("TestUserServiceException expected");
    } catch (TestUserServiceException e) {
    }

    checkLevel1Upgraded(users.get(1), false);
  }

  private void checkLevel(User user, Level expectedLevel) {
    User userUpdate = userDao.get(user.getId());
    assertThat(userUpdate.getLevel()).isEqualTo(expectedLevel);
  }

  private void checkLevel1Upgraded(User user, boolean upgraded) {
    User userUpdate = userDao.get(user.getId());
    if (upgraded) {
      assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
    } else {
      assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
    }
  }

  static class TestUserService extends UserServiceImpl {
    private String id;

    private TestUserService(String id) {
      this.id = id;
    }

    // UserService method override
    protected void upgradeLevel(User user) {
      if (user.getId().equals(this.id)) throw new TestUserServiceException();
      super.upgradeLevel(user);
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
}
