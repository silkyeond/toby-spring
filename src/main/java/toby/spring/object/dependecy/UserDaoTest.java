package toby.spring.object.dependecy;

import java.sql.SQLException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import toby.spring.object.dependecy.dao.UserDaoJdbc;
import toby.spring.object.dependecy.user.domain.User;

public class UserDaoTest {
  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    // annotation을 이용해 애플리케이션 컨텍스트 생성
    //    ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

    // XML을 이용해 애플리케이션 컨텍스트 생성
    ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

    UserDaoJdbc dao = context.getBean("userDao", UserDaoJdbc.class);
    User user = new User();

    user.setId("whiteship");
    user.setName("백기선");
    user.setPassword("married");

    dao.add(user);

    User user2 = dao.get(user.getId());

    if (!user.getName().equals(user2.getName())) {
      System.out.println("테스트 실패 (name)");
    } else if (!user.getPassword().equals(user2.getPassword())) {
      System.out.println("테스트 실패 (password)");
    } else {
      System.out.println("조회 테스트 성공");
    }
  }
}
