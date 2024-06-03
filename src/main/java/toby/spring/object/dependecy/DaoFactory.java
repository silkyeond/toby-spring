package toby.spring.object.dependecy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {

  @Bean
  public UserDao userDao() {
    UserDao userDao = new UserDao();
    // 수정자 메소드 DI를 사용하는 팩토리 메소드
    userDao.setConnectionMaker(connectionMaker());
    return new UserDao();
  }

  @Bean
  public ConnectionMaker connectionMaker() {
    // 분리해서 중복을 제거한 ConnectionMaker 타입 오브젝트 생성 코드
    return new DConnectionMaker();
  }
}
