package mediator;

import model.domain.User;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;

public interface UserRemote extends Remote
{
  String addUser(String firstname, String lastname, String email,
      String password, String repeatedPassword, String phone,
      LocalDate birthday) throws RemoteException, SQLException;
  String login(String email, String password)
      throws RemoteException, SQLException;

  void resetPassword(String userEmail, String oldPassword, String newPassword,
      String repeatPassword) throws RemoteException, SQLException;
  User getUser(String email) throws RemoteException, SQLException;
  User getModeratorInfo() throws RemoteException, SQLException;
  boolean isModerator(String email) throws RemoteException, SQLException;
  User editInformation(String oldEmail, String firstname, String lastname,
      String email, String password, String phone, LocalDate birthday)
      throws RemoteException, SQLException;
  void deleteAccount(String email, String password)
      throws RemoteException, SQLException;
  boolean addListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException;
  boolean removeListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException;
}
