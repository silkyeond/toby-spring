package toby.spring.test.proxy;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HelloTargetTest {
  @Test
  public void simpleProxy() {
    Hello hello = new HelloTarget();
    assertThat(hello.sayHello("Toby")).isEqualTo("Hello Toby");
    assertThat(hello.sayHi("Toby")).isEqualTo("Hi Toby");
    assertThat(hello.sayThankYou("Toby")).isEqualTo("Thank you Toby");

    // 프록시를 통해 타깃 오브젝트에 접근하도록 구성
    Hello proxiedHello = new HelloUppercase(new HelloTarget());
    assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
    assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
    assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
  }
}
