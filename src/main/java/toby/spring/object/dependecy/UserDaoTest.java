package toby.spring.object.dependecy;

import java.sql.SQLException;

public class UserDaoTest {
  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    ConnectionMaker connectionMaker = new DConnectionMaker();

    UserDao dao = new UserDao(connectionMaker);
    User user = new User();

    user.setId("whiteship");
    user.setName("백기선");
    user.setPassword("married");

    dao.add(user);

    System.out.println(user.getId() + "등록 성공");
  }
}
