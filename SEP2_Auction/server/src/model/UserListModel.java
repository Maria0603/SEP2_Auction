package model;

import model.domain.NotificationList;
import model.domain.User;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The UserListModel interface provides methods for managing user lists and their notifications.
 * It extends the NamedPropertyChangeSubject to support property change notifications.
 */
public interface UserListModel extends NamedPropertyChangeSubject
{
  /**
   * Retrieves the notifications for a specific receiver.
   *
   * @param receiver the email or identifier of the receiver.
   * @return the list of notifications for the receiver.
   * @throws SQLException if there is a database access error.
   */
  NotificationList getNotifications(String receiver) throws SQLException;

  /**
   * Retrieves a list of all users.
   *
   * @return a list of all users.
   * @throws SQLException if there is a database access error.
   */
  ArrayList<User> getAllUsers() throws SQLException;

  /**
   * Bans a participant with a specified reason.
   *
   * @param moderatorEmail the email of the moderator performing the ban.
   * @param participantEmail the email of the participant to be banned.
   * @param reason the reason for the ban.
   * @throws SQLException if there is a database access error.
   */
  void banParticipant(String moderatorEmail, String participantEmail, String reason) throws SQLException;

  /**
   * Extracts the reason for banning a participant based on their email.
   *
   * @param email the email of the banned participant.
   * @return the reason for the ban.
   * @throws SQLException if there is a database access error.
   */
  String extractBanningReason(String email) throws SQLException;

  /**
   * Unbans a participant.
   *
   * @param moderatorEmail the email of the moderator performing the unban.
   * @param participantEmail the email of the participant to be unbanned.
   * @throws SQLException if there is a database access error.
   */
  void unbanParticipant(String moderatorEmail, String participantEmail) throws SQLException;
}
