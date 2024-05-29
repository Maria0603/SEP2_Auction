package model;

import model.domain.NotificationList;
import model.domain.User;
import utility.observer.javaobserver.NamedPropertyChangeSubject;
import java.util.ArrayList;

/**
 * The UserListModel interface defines the methods for interacting with the user list,
 * including getting notifications, retrieving all users, banning and unbanning participants,
 * and extracting the reason for banning a participant. It extends NamedPropertyChangeSubject
 * to support property change notifications.
 */
public interface UserListModel extends NamedPropertyChangeSubject {

  /**
   * Retrieves the list of notifications for a receiver.
   *
   * @param receiver the receiver of the notifications.
   * @return the list of notifications.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  NotificationList getNotifications(String receiver) throws IllegalArgumentException;

  /**
   * Retrieves the list of all users.
   *
   * @return the list of all users.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  ArrayList<User> getAllUsers() throws IllegalArgumentException;

  /**
   * Bans a participant.
   *
   * @param moderatorEmail the email of the moderator.
   * @param participantEmail the email of the participant to ban.
   * @param reason the reason for banning the participant.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  void banParticipant(String moderatorEmail, String participantEmail, String reason) throws IllegalArgumentException;

  /**
   * Extracts the reason for banning a participant.
   *
   * @param email the email of the participant.
   * @return the reason for banning the participant.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  String extractBanningReason(String email) throws IllegalArgumentException;

  /**
   * Unbans a participant.
   *
   * @param moderatorEmail the email of the moderator.
   * @param participantEmail the email of the participant to unban.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  void unbanParticipant(String moderatorEmail, String participantEmail) throws IllegalArgumentException;
}
