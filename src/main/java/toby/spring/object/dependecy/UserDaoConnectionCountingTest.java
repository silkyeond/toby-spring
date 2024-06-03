package toby.spring.object.dependecy;

import java.sql.SQLException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class UserDaoConnectionCountingTest {
  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(CountingDaoFactory.class);
    UserDao dao = context.getBean("userDao", UserDao.class);

    User user = new User();

    user.setId("aaaa");
    user.setName("백기선");
    user.setPassword("married");

    dao.add(user);

    System.out.println(user.getId() + "등록 성공");

    // DAO 사용 코드
    // DL(의존관계 검색)을 사용하면 이름을 이용해 어떤 빈이든 가져올 수 있다.
    CountingConnectionMaker ccm = context.getBean("connectionMaker", CountingConnectionMaker.class);
    System.out.println("Connection counter : " + ccm.getCounter());
  }
}
