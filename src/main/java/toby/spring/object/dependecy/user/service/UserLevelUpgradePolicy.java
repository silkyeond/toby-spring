package toby.spring.object.dependecy.user.service;

import toby.spring.object.dependecy.user.domain.User;

public interface UserLevelUpgradePolicy {

  boolean canUpgradeLevel(User user);

  void upgradeLevel(User user);
}
