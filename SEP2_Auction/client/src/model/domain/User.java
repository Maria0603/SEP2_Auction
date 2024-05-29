package model.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * The User class represents a user with personal information such as firstname, lastname, email, password, phone, and birthday.
 * It implements Serializable for object serialization.
 */
public class User implements Serializable {

  ///////////////////////////////////////////////////////////////////
  // Do not change this number
  @Serial
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  private String firstname;
  private String lastname;
  private String email;
  private String password;
  private String phone;
  private LocalDate birthday;

  /**
   * Constructs a new User object with the specified parameters.
   *
   * @param firstname the first name of the user.
   * @param lastname the last name of the user.
   * @param email the email of the user.
   * @param password the password of the user.
   * @param phone the phone number of the user.
   * @param birthday the birthday of the user.
   */
  public User(String firstname, String lastname, String email, String password,
              String phone, LocalDate birthday) {
    setFirstname(firstname);
    setLastname(lastname);
    setEmail(email);
    setPassword(password);
    setPhone(phone);
    setBirthday(birthday);
  }

  /**
   * Sets the first name of the user.
   *
   * @param firstname the first name to set.
   */
  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  /**
   * Sets the last name of the user.
   *
   * @param lastname the last name to set.
   */
  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  /**
   * Sets the email of the user.
   *
   * @param email the email to set.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Sets the password of the user.
   *
   * @param password the password to set.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Sets the phone number of the user.
   *
   * @param phone the phone number to set.
   */
  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * Sets the birthday of the user.
   *
   * @param birthday the birthday to set.
   */
  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }

  /**
   * Gets the first name of the user.
   *
   * @return the first name of the user.
   */
  public String getFirstname() {
    return firstname;
  }

  /**
   * Gets the last name of the user.
   *
   * @return the last name of the user.
   */
  public String getLastname() {
    return lastname;
  }

  /**
   * Gets the email of the user.
   *
   * @return the email of the user.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Gets the password of the user.
   *
   * @return the password of the user.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Gets the phone number of the user.
   *
   * @return the phone number of the user.
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Gets the birthday of the user.
   *
   * @return the birthday of the user.
   */
  public LocalDate getBirthday() {
    return birthday;
  }

  /**
   * Returns a string representation of the user.
   *
   * @return a string representation of the user.
   */
  @Override
  public String toString() {
    return firstname + " " + lastname + ": Email: " + email + "; Phone: " + phone;
  }
}
