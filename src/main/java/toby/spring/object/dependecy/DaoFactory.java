package toby.spring.object.dependecy;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import toby.spring.object.dependecy.dao.UserDaoJdbc;

@Configuration
public class DaoFactory {

  @Bean
  public UserDaoJdbc userDao() {
    //        UserDao userDao = new UserDao();
    //    userDao.setDataSource(dataSource());
    // 수정자 메소드 DI를 사용하는 팩토리 메소드
    //    userDao.setConnectionMaker(connectionMaker());
    return new UserDaoJdbc();
  }

  @Bean
  public DataSource dataSource() {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
    dataSource.setUrl("jdbc:mysql://localhost/springbook");
    dataSource.setUsername("spring");
    dataSource.setPassword("book");

    return dataSource;
  }

  @Bean
  public ConnectionMaker connectionMaker() {
    // 분리해서 중복을 제거한 ConnectionMaker 타입 오브젝트 생성 코드
    return new DConnectionMaker();
  }
}
