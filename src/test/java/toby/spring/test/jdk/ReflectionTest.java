package toby.spring.test.jdk;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

public class ReflectionTest {
  @Test
  public void invokeMethod()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String name = "Spring";

    // length()
    assertThat(name.length()).isEqualTo(6);

    Method lengthMethod = String.class.getMethod("length");
    assertThat((Integer) lengthMethod.invoke(name)).isEqualTo(6);

    // charAt()
    assertThat(name.charAt(0)).isEqualTo('S');

    Method charAtMethod = String.class.getMethod("charAt", int.class);
    assertThat((Character) charAtMethod.invoke(name, 0)).isEqualTo('S');
  }
}
