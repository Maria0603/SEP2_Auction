package model;

import model.domain.User;
import utility.observer.javaobserver.NamedPropertyChangeSubject;
import java.time.LocalDate;

public interface UserModel extends NamedPropertyChangeSubject
{
  String addUser(String firstname, String lastname, String email,
      String password, String repeatedPassword, String phone,
      LocalDate birthday) throws IllegalArgumentException;
  String login(String email, String password) throws IllegalArgumentException;
  void resetPassword(String userEmail, String oldPassword, String newPassword,
      String repeatPassword) throws IllegalArgumentException;
  User getUser(String email) throws IllegalArgumentException;
  User getModeratorInfo() throws IllegalArgumentException;
  boolean isModerator(String email) throws IllegalArgumentException;
  User editInformation(String oldEmail, String firstname, String lastname,
      String email, String password, String phone, LocalDate birthday)
      throws IllegalArgumentException;
  void deleteAccount(String email, String password) throws IllegalArgumentException;
}
