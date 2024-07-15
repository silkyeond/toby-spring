package toby.spring.object.dependecy.user.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import toby.spring.object.dependecy.user.domain.User;

@Transactional
public interface UserService {
  void add(User user);

  @Transactional(readOnly=true)
  User get(String id);

  @Transactional(readOnly=true)
  List<User> getAll();

  void deleteAll();

  void update(User user);

  void upgradeLevels();
}
