package toby.spring.object.dependecy;

import java.sql.SQLException;

public class UserDaoTest {
  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    //    ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
    //    UserDao dao = context.getBean("userDao", UserDao.class);
    UserDao dao = new UserDao();
    User user = new User();

    user.setId("whiteship");
    user.setName("백기선");
    user.setPassword("married");

    dao.add(user);

    System.out.println(user.getId() + "등록 성공");
  }
}
