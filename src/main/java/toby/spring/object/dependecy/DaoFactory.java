package toby.spring.object.dependecy;

public class DaoFactory {
  public UserDao userDao() {
    return new UserDao(connectionMaker());
  }

  public ConnectionMaker connectionMaker() {
    // 분리해서 중복을 제거한 ConnectionMaker 타입 오브젝트 생성 코드
    return new DConnectionMaker();
  }
}
