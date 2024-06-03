package toby.spring.object.dependecy;

public class DaoFactory {
  public UserDao userDao() {
    ConnectionMaker connectionMaker = new DConnectionMaker();
    // DB 커넥션을 가져오도록 이미 설정된 UserDao 오브젝트를 리턴
    UserDao userDao = new UserDao(connectionMaker);
    return userDao;
  }
}
