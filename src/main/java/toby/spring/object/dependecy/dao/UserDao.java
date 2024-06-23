package toby.spring.object.dependecy.dao;

import java.util.List;
import toby.spring.object.dependecy.user.domain.User;

public interface UserDao {
  void add(User user);

  User get(String id);

  List<User> getAll();

  void update(User user);

  void deleteAll();

  int getCount();
}
