package by.zgirskaya.course.task_4_web.exception;

public class ConnectionException extends RuntimeException {
  public ConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
