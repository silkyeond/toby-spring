package toby.spring.object.dependecy;

import java.sql.SQLException;

public class UserDaoTest {
  public static void main(String[] args) throws SQLException, ClassNotFoundException {

    UserDao dao = new DaoFactory().userDao();
    User user = new User();

    user.setId("whiteship");
    user.setName("백기선");
    user.setPassword("married");

    dao.add(user);

    System.out.println(user.getId() + "등록 성공");
  }
}
