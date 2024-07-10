package toby.spring.object.dependecy.user.service;

import java.util.List;
import toby.spring.object.dependecy.user.domain.User;

public interface UserService {
  void add(User user);

  User get(String id);

  List<User> getAll();

  void deleteAll();

  void update(User user);

  void upgradeLevels();
}
