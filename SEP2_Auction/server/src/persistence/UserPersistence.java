package persistence;

import model.domain.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface UserPersistence
{
  User createUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday) throws SQLException;
  String login(String email, String password) throws SQLException;
  void resetPassword(String userEmail, String oldPassword, String newPassword, String repeatPassword)
      throws SQLException;
  User getUserInfo(String email) throws SQLException;
  User getModeratorInfo() throws SQLException;
  boolean isModerator(String email) throws SQLException;
  User editInformation(String oldEmail, String firstname, String lastname, String email, String password, String phone, LocalDate birthday) throws SQLException;
  void deleteAccount(String email, String password) throws SQLException;
  NotificationList getNotifications(String receiver) throws SQLException;
  ArrayList<User> getAllUsers() throws SQLException;
  void banParticipant(String moderatorEmail, String participantEmail, String reason) throws SQLException;
  String extractBanningReason(String email) throws SQLException;
  void unbanParticipant(String moderatorEmail, String participantEmail) throws SQLException;
}
