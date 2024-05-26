package model;

import model.domain.NotificationList;
import model.domain.User;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.sql.SQLException;
import java.util.ArrayList;

public interface UserListModel extends NamedPropertyChangeSubject
{
  NotificationList getNotifications(String receiver) throws SQLException;
  ArrayList<User> getAllUsers() throws SQLException;
  void banParticipant(String moderatorEmail, String participantEmail,
      String reason) throws SQLException;
  String extractBanningReason(String email) throws SQLException;
  void unbanParticipant(String moderatorEmail, String participantEmail)
      throws SQLException;
}
