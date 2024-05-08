package model;

import java.util.ArrayList;

public class UserList {
    ArrayList<User> users;
    public UserList(){
        users = new ArrayList<>();
        addUser("A","B","x@y","pass","420");
    }

    public void addUser(String firstname, String lastname, String email, String password, String phone){
        try{
            users.add(new User(firstname,lastname,email,password,phone));
            System.out.println("USER-LIST: Added: " + firstname + " " + lastname);
        }
        //  Forwarding the exception to model
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    public void removeUser(String email){
        for (User user : users){
            if(user.getEmail().equals(email)){
                users.remove(user);
            }
        }
    }
    public User getUser(String email){
        for(User user : users){
            if(user.getEmail().equals(email)){
                return user;
            }
        }
        return null;
    }
    public boolean passValidation(String email, String password){
        if(email.isEmpty() || password.isEmpty()){
            return false;
        }
        for(User user : users){
            if(user.getEmail().equals(email)){
                if(user.getPassword().equals(password)){
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
    }

}
