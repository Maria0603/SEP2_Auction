package utility;

import javafx.util.StringConverter;

/**
 * The IntStringConverter class extends StringConverter to provide
 * conversion between Integer and String types for use with JavaFX.
 */
public class IntStringConverter extends StringConverter<Number> {

  /**
   * Converts the given Number to its String representation.
   *
   * @param number the Number to be converted.
   * @return the String representation of the number, or an empty string if the number is null or zero.
   */
  @Override
  public String toString(Number number) {
    return number == null || number.intValue() == 0 ? "" : number.toString();
  }

  /**
   * Converts the given String to its Number representation.
   *
   * @param s the String to be converted.
   * @return the Number representation of the string, or zero if the string cannot be parsed.
   */
  @Override
  public Number fromString(String s) {
    try {
      return Integer.parseInt(s);
    } catch (Exception e) {
      return 0;
    }
  }
}
