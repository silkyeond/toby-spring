package toby.spring.object.dependecy.user.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import toby.spring.object.dependecy.dao.UserDao;
import toby.spring.object.dependecy.user.domain.Level;
import toby.spring.object.dependecy.user.domain.User;

public class UserService {
  UserDao userDao;
  private DataSource dataSource;

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void setUserDao(UserDao userDao) {
    this.userDao = userDao;
  }

  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECCOMEND_ROR_GOLD = 30;

  public void upgradeLevels() throws Exception {
    // 트랜잭션 동기화 관리자를 이용해 동기화 작업 초기화
    TransactionSynchronizationManager.initSynchronization();
    // DB Connection를 생성하고 트랜잭션을 시작. 이후 DAO 작업은 모두 여기서 시작한 트랜잭션 안에서 진행
    Connection c = DataSourceUtils.getConnection(dataSource);
    c.setAutoCommit(false);
    try {
      List<User> users = userDao.getAll();
      for (User user : users) {
        if (canUpgradeLevel(user)) {
          upgradeLevel(user);
        }
      }
      c.commit();
    } catch (Exception e) {
      c.rollback();
      throw e;
    } finally {
      DataSourceUtils.releaseConnection(c, dataSource);
      TransactionSynchronizationManager.unbindResource(this.dataSource);
      TransactionSynchronizationManager.clearSynchronization();
    }
  }

  public void add(User user) {
    if (user.getLevel() == null) user.setLevel(Level.BASIC);
    userDao.add(user);
  }

  private boolean canUpgradeLevel(User user) {
    Level currentLevel = user.getLevel();
    switch (currentLevel) {
      case BASIC:
        return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
      case SILVER:
        return (user.getRecommend() >= MIN_RECCOMEND_ROR_GOLD);
      case GOLD:
        return false;
      default:
        throw new IllegalArgumentException("Unknown Level : " + currentLevel);
    }
  }

  protected void upgradeLevel(User user) {
    user.upgradeLevel();
    userDao.update(user);
  }

  static class TestUserService extends UserService {
    private String id;

    TestUserService(String id) {
      this.id = id;
    }

    // UserService method override
    protected void upgradeLevel(User user) {
      if (user.getId().equals(this.id)) throw new TestUserServiceException();
      super.upgradeLevel(user);
    }
  }

  static class TestUserServiceException extends RuntimeException {}
}
