package toby.spring.object.dependecy.user.service;

import java.util.List;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import toby.spring.object.dependecy.dao.UserDao;
import toby.spring.object.dependecy.user.domain.Level;
import toby.spring.object.dependecy.user.domain.User;

public class UserService {
  UserDao userDao;

  public void setUserDao(UserDao userDao) {
    this.userDao = userDao;
  }

  private PlatformTransactionManager transactionManager;

  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  private MailSender mailSender;

  public void setMailSender(MailSender mailSender) {
    this.mailSender = mailSender;
  }

  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECCOMEND_ROR_GOLD = 30;

  public void upgradeLevels() {
    TransactionStatus status =
        // DI 받은 트랜잭션 매니저를 공유해서 사용, 멀티 스레드 환경에서 안전하다.
        this.transactionManager.getTransaction(new DefaultTransactionDefinition());

    try {
      List<User> users = userDao.getAll();
      for (User user : users) {
        if (canUpgradeLevel(user)) {
          upgradeLevel(user);
        }
      }
      this.transactionManager.commit(status);
    } catch (Exception e) {
      this.transactionManager.rollback(status);
      throw e;
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
    sendUpgradeEMail(user);
  }

  private void sendUpgradeEMail(User user) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(user.getMail());
    mailMessage.setFrom("useradmin@ksug.org");
    mailMessage.setSubject("Upgrade 안내");
    mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());

    this.mailSender.send(mailMessage);
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
