package toby.spring.test.template;

public interface LineCallback<T> {
  T doSomethingWithLine(String line, T value);
}
