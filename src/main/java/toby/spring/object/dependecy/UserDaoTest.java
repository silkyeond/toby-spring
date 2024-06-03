package toby.spring.object.dependecy;

import java.sql.SQLException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class UserDaoTest {
  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    // annotation을 이용해 애플리케이션 컨텍스트 생성
    //    ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

    // XML을 이용해 애플리케이션 컨텍스트 생성
    ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

    UserDao dao = context.getBean("userDao", UserDao.class);
    //    UserDao dao = new UserDao();
    User user = new User();

    user.setId("whiteship");
    user.setName("백기선");
    user.setPassword("married");

    dao.add(user);

    System.out.println(user.getId() + " 등록 성공");
  }
}
