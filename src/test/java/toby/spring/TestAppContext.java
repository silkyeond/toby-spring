package toby.spring;

import static toby.spring.object.dependecy.user.service.UserServiceTest.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import toby.spring.object.dependecy.dao.UserDao;
import toby.spring.object.dependecy.user.service.DummyMailSender;
import toby.spring.object.dependecy.user.service.UserService;

@Configuration
public class TestAppContext {
  @Autowired UserDao userDao;

  @Bean
  public UserService testUserService() {
    return new TestUserService();
  }

  @Bean
  public MailSender mailSender() {
    return new DummyMailSender();
  }
}
