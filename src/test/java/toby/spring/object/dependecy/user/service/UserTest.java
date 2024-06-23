package toby.spring.object.dependecy.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toby.spring.object.dependecy.user.domain.Level;
import toby.spring.object.dependecy.user.domain.User;

public class UserTest {
  User user;

  @BeforeEach
  public void setUp() {
    user = new User();
  }

  @Test
  public void upgradeLevel() {
    Level[] levels = Level.values();
    for (Level level : levels) {
      if (level.nextLevel() == null) continue;
      user.setLevel(level);
      user.upgradeLevel();
      assertThat(user.getLevel()).isEqualTo(level.nextLevel());
    }
  }

  @Test
  public void cannotUpgradeLevel() {
    Level[] levels = Level.values();
    assertThrows(
        IllegalStateException.class,
        () -> {
          for (Level level : levels) {
            if (level.nextLevel() != null) continue;
            user.setLevel(level);
            user.upgradeLevel();
          }
        });
  }
}
