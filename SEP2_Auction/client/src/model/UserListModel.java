package model;

import model.domain.NotificationList;
import model.domain.User;
import utility.observer.javaobserver.NamedPropertyChangeSubject;
import java.util.ArrayList;

public interface UserListModel extends NamedPropertyChangeSubject
{
  NotificationList getNotifications(String receiver) throws IllegalArgumentException;
  ArrayList<User> getAllUsers() throws IllegalArgumentException;
  void banParticipant(String moderatorEmail, String participantEmail,
      String reason) throws IllegalArgumentException;
  String extractBanningReason(String email) throws IllegalArgumentException;
  void unbanParticipant(String moderatorEmail, String participantEmail)
      throws IllegalArgumentException;
}
