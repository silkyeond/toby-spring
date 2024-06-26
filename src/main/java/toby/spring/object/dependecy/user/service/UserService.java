package toby.spring.object.dependecy.user.service;

import toby.spring.object.dependecy.user.domain.User;

public interface UserService {
  void add(User user);

  void upgradeLevels();
}
