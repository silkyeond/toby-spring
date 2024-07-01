package toby.spring.test.factorybean;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/test-applicationContext.xml")
class FactoryBeanTest {
  @Autowired ApplicationContext context;

  @Test
  public void getMessageFromFactoryBean() {
    Object message = context.getBean("message");
    assertThat(message).isInstanceOf(Message.class);
    assertThat(((Message) message).getText()).isEqualTo("Factory Bean");
  }

  @Test
  public void getFactoryBean() {
    Object factory = context.getBean("&message");
    assertThat(factory).isInstanceOf(MessageFactoryBean.class);
  }
}
