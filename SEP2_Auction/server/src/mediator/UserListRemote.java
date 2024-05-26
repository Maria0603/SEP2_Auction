package mediator;

import model.domain.NotificationList;
import model.domain.User;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface UserListRemote extends Remote
{

  NotificationList getNotifications(String receiver) throws RemoteException, SQLException;
  ArrayList<User> getAllUsers() throws RemoteException, SQLException;
  void banParticipant(String moderatorEmail, String participantEmail, String reason) throws RemoteException, SQLException;
  String extractBanningReason(String email) throws RemoteException, SQLException;
  void unbanParticipant(String moderatorEmail, String participantEmail) throws RemoteException, SQLException;
  boolean addListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException;
  boolean removeListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException;
}
