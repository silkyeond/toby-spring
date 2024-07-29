package toby.spring.object.dependecy.user.sqlservice.updatable;

public class SqlUpdateFailureException extends RuntimeException {
  public SqlUpdateFailureException(String message) {
    super(message);
  }

  public SqlUpdateFailureException(String message, Throwable cause) {
    super(message, cause);
  }
}
