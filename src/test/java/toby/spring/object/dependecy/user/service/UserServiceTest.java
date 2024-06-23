package toby.spring.object.dependecy.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static toby.spring.object.dependecy.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static toby.spring.object.dependecy.user.service.UserService.MIN_RECCOMEND_ROR_GOLD;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import toby.spring.object.dependecy.dao.UserDao;
import toby.spring.object.dependecy.user.domain.Level;
import toby.spring.object.dependecy.user.domain.User;
import toby.spring.object.dependecy.user.service.UserService.TestUserServiceException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
class UserServiceTest {
  @Autowired UserService userService;
  @Autowired UserDao userDao;
  List<User> users;

  @BeforeEach
  public void setUp() {
    users =
        Arrays.asList(
            new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
            new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
            new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_ROR_GOLD - 1),
            new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_ROR_GOLD),
            new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE));
  }

  @Test
  public void bean() {
    assertThat(this.userService).isNotNull();
  }

  @Test
  public void upgradeLevels() {
    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    userService.upgradeLevels();

    checkLevel1Upgraded(users.get(0), false);
    checkLevel1Upgraded(users.get(1), true);
    checkLevel1Upgraded(users.get(2), false);
    checkLevel1Upgraded(users.get(3), true);
    checkLevel1Upgraded(users.get(4), false);
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
    UserService testUserService = new UserService.TestUserService(users.get(3).getId());
    // 테스트 메소드에서만 특별한 목적으로 사용되는 것으로 동작하는데 필요한 DAO만 수동 DI해준다.
    testUserService.setUserDao(this.userDao);
    userDao.deleteAll();
    for (User user : users) userDao.add(user);

    try {
      testUserService.upgradeLevels();
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
}
