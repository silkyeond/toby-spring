package toby.spring;

import java.sql.SQLException;
import toby.spring.object.dependecy.NUserDao;
import toby.spring.object.dependecy.User;
import toby.spring.object.dependecy.UserDao;

public class Main {
  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    UserDao dao = new NUserDao();

    User user = new User();
    user.setId("whiteship");
    user.setName("백기선");
    user.setPassword("married");

    dao.add(user);

    System.out.println(user.getId() + "등록 성공");

    User user2 = dao.get(user.getId());
    System.out.println(user.getName());
    System.out.println(user.getPassword());

    System.out.println(user2.getId() + " 조회 성공");
  }
}
