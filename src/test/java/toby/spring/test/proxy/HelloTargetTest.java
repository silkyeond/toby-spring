package toby.spring.test.proxy;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;

class HelloTargetTest {
  @Test
  public void simpleProxy() {
    Hello proxiedHello =
        (Hello)
            Proxy.newProxyInstance(
                getClass().getClassLoader(), // 동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용할 클래스 로더
                new Class[] {Hello.class}, // 구현할 인터페이스
                new UppercaseHandler(new HelloTarget())); // 부가기능과 위임 코드를 담은 InvocationHandler
  }
}
