package model;

import java.io.Serializable;

public class User implements Serializable {
    ///////////////////////////////////////////////////////////////////
    //do not change this number
    private static final long serialVersionUID = 6529685098267757690L;
    //////////////////////////////////////////////////////////////////
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    public User(String firstname, String lastname, String email, String password, String phone){
        setFirstname(firstname);
        setLastname(lastname);
        setEmail(email);
        setPassword(password);
        setPhone(phone);

    }
    public void setFirstname(String firstname){


        this.firstname = firstname;
    }
    public void setLastname(String lastname){


        this.lastname = lastname;
    }
    public void setEmail(String email){

        this.email = email;
    }
    public void setPassword(String password){

        this.password = password;
    }
    public void setPhone(String phone){

        this.phone = phone;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }
}
