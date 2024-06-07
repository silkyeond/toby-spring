package toby.spring.object.dependecy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
// 파라미터가 없는 디폴트 생성자 생성
@NoArgsConstructor
// 모든 필드 값을 파라미터로 받는 생성자 생성
@AllArgsConstructor
public class User {
  String id;
  String name;
  String password;
}
