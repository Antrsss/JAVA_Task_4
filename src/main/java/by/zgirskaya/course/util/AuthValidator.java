package by.zgirskaya.course.util;

public class AuthValidator {
  private static final String PHONE_NUMBER_REGEX = "^[+]?[0-9\\s\\-()]+$";
  private static final String EMAIL_SYMBOL = "@";

  public static boolean validateRegistrationInput(
      String name,
      String identifier,
      String password,
      String role,
      String username,
      String passportId) {

    boolean isValid = validateNotEmpty(name) &&
        validateNotEmpty(identifier) &&
        validateNotEmpty(password) &&
        validateNotEmpty(role);

    if (AuthParameter.Roles.EMPLOYEE.equals(role)) {
      return isValid && validateNotEmpty(passportId);
    } else {
      return isValid && validateNotEmpty(username);
    }
  }

  public static boolean validateNotEmpty(String value) {
    return value != null && !value.isBlank();
  }

  public static boolean isValidEmail(String email) {
    if (email == null) return false;
    return email.contains(EMAIL_SYMBOL) && email.length() > 3;
  }

  public static boolean isValidPhoneNumber(String phoneNumber) {
    if (phoneNumber == null) return false;
    return !phoneNumber.contains(EMAIL_SYMBOL) && phoneNumber.matches(PHONE_NUMBER_REGEX);
  }

  private AuthValidator() {}
}