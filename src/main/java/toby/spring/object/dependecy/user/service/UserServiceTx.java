package toby.spring.object.dependecy.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import toby.spring.object.dependecy.user.domain.User;

public class UserServiceTx implements UserService {
  UserService userService;

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  PlatformTransactionManager transactionManager;

  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  @Override
  public void add(User user) {
    userService.add(user);
  }

  @Override
  public void upgradeLevels() {
    TransactionStatus status =
        this.transactionManager.getTransaction(new DefaultTransactionDefinition());
    try{
      userService.upgradeLevels();

      this.transactionManager.commit(status);
    } catch (RuntimeException e){
      this.transactionManager.rollback(status);
      throw e;
    }
  }
}
