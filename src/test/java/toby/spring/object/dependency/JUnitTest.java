package toby.spring.object.dependency;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/junit.xml")
public class JUnitTest {
  @Autowired ApplicationContext context;
  static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
  static ApplicationContext contextObject = null;

  @Test
  public void test1() {
    System.out.println("####### test1 #######");
    System.out.println("testObjects : " + testObjects);
    System.out.println("this : " + this);

    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);
    System.out.println("testObject add : " + testObjects);

    assertThat(contextObject == null || contextObject == this.context).isTrue();
    contextObject = this.context;
    System.out.println("context : " + this.context);
    System.out.println("####### test1 #######");
  }

  @Test
  public void test2() {
    System.out.println("####### test2 #######");
    System.out.println("testObjects : " + testObjects);
    System.out.println("this : " + this);

    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);
    System.out.println("testObject add : " + testObjects);

    assertThat(contextObject == null || contextObject == this.context).isTrue();
    contextObject = this.context;
    System.out.println("context : " + this.context);
    System.out.println("####### test2 #######");
  }

  @Test
  public void test3() {
    System.out.println("####### test3 #######");
    System.out.println("testObjects : " + testObjects);
    System.out.println("this : " + this);

    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);
    System.out.println("testObject add : " + testObjects);

    assertThat(contextObject == null || contextObject == this.context).isTrue();
    contextObject = this.context;
    System.out.println("context : " + this.context);
    System.out.println("####### test3 #######");
  }
}
