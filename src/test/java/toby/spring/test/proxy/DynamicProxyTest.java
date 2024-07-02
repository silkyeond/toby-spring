package toby.spring.test.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import java.util.Objects;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

class DynamicProxyTest {
  @Test
  public void simpleProxy() {
    Hello proxiedHello =
        (Hello)
            Proxy.newProxyInstance(
                getClass().getClassLoader(), // 동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용할 클래스 로더
                new Class[] {Hello.class}, // 구현할 인터페이스
                new UppercaseHandler(new HelloTarget())); // 부가기능과 위임 코드를 담은 InvocationHandler
  }

  @Test
  public void proxyFactoryBean() {
    ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
    proxyFactoryBean.setTarget(new HelloTarget());
    proxyFactoryBean.addAdvice(new UppercaseAdvice());

    Hello proxiedHello = (Hello) proxyFactoryBean.getObject();
    assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
    assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
    assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
  }

  static class UppercaseAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
      // 리플렉션의 Method와 달리 메소드 실행 시 타깃 오브젝트를 전달할 필요가 없다.
      String ret = (String) invocation.proceed();
      return Objects.requireNonNull(ret).toUpperCase();
    }
  }

  // 타깃과 프록시가 구현할 인터페이스
  static interface Hello {
    String sayHello(String name);

    String sayHi(String name);

    String sayThankYou(String name);
  }

  // 타깃 클래스
  static class HelloTarget implements Hello {
    public String sayHello(String name) {
      return "Hello " + name;
    }

    public String sayHi(String name) {
      return "Hi " + name;
    }

    public String sayThankYou(String name) {
      return "Thank you " + name;
    }
  }

  @Test
  public void pointcutAdvisor() {
    ProxyFactoryBean pfBean = new ProxyFactoryBean();
    pfBean.setTarget(new HelloTarget());

    NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
    pointcut.setMappedName("sayH*");

    pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

    Hello proxiedHello = (Hello) pfBean.getObject();

    assertThat(Objects.requireNonNull(proxiedHello).sayHello("Toby")).isEqualTo("HELLO TOBY");
    assertThat(Objects.requireNonNull(proxiedHello).sayHi("Toby")).isEqualTo("HI TOBY");
    assertThat(Objects.requireNonNull(proxiedHello).sayThankYou("Toby"))
        .isEqualTo("Thank you Toby");
  }

  @Test
  public void classNamePointcutAdvisor() {
    // 포인트컷 준비
    NameMatchMethodPointcut classMethodPointcut =
        new NameMatchMethodPointcut() {
          @Override
          public ClassFilter getClassFilter() {
            return new ClassFilter() {
              @Override
              public boolean matches(Class<?> clazz) {
                return clazz.getSimpleName().startsWith("HelloT");
              }
            };
          }
        };
    classMethodPointcut.setMappedName("sayH*");

    // 테스트
    checkAdviced(new HelloTarget(), classMethodPointcut, true);

    class HelloWorld extends HelloTarget {}
    checkAdviced(new HelloWorld(), classMethodPointcut, false);

    class HelloToby extends HelloTarget {}
    checkAdviced(new HelloToby(), classMethodPointcut, true);
  }

  private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
    ProxyFactoryBean pfBean = new ProxyFactoryBean();
    pfBean.setTarget(target);
    pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
    Hello proxiedHello = (Hello) pfBean.getObject();

    if (adviced) {
      assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
      assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
      assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("Thank you Toby");
    } else {
      assertThat(proxiedHello.sayHello("Toby")).isEqualTo("Hello Toby");
      assertThat(proxiedHello.sayHi("Toby")).isEqualTo("Hi Toby");
      assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("Thank you Toby");
    }
  }
}
