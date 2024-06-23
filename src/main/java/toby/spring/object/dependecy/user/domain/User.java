package toby.spring.object.dependecy.user.domain;

import lombok.*;

@Getter
@Setter
// 파라미터가 없는 디폴트 생성자 생성
@NoArgsConstructor
// 모든 필드 값을 파라미터로 받는 생성자 생성
@AllArgsConstructor
@ToString
public class User {
  private static final int BASIC = 1;
  private static final int SILVER = 1;
  private static final int GOLD = 1;

  String id;
  String name;
  String password;
  Level level;
  int login;
  int recommend;

  public void upgradeLevel() {
    Level nextLevel = this.level.nextLevel();
    if (nextLevel == null) {
      throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");
    } else {
      this.level = nextLevel;
    }
  }
}
