package model;

import model.domain.User;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.sql.SQLException;
import java.time.LocalDate;

public interface UserModel extends NamedPropertyChangeSubject
{
  String addUser(String firstname, String lastname, String email,
      String password, String repeatedPassword, String phone,
      LocalDate birthday) throws SQLException;
  String login(String email, String password) throws SQLException;
  void resetPassword(String userEmail, String oldPassword, String newPassword,
      String repeatPassword) throws SQLException;
  User getUser(String email) throws SQLException;
  User getModeratorInfo() throws SQLException;
  boolean isModerator(String email) throws SQLException;
  User editInformation(String oldEmail, String firstname, String lastname,
      String email, String password, String phone, LocalDate birthday)
      throws SQLException;
  void deleteAccount(String email, String password) throws SQLException;
}
