package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

public class User implements Serializable {
    ///////////////////////////////////////////////////////////////////
    //do not change this number
    private static final long serialVersionUID = 6529685098267757690L;
    //////////////////////////////////////////////////////////////////
    private String firstname, lastname, email, password, phone;
    private LocalDate birthday;
    public User(String firstname, String lastname, String email, String password, String phone, LocalDate birthday){
        setFirstname(firstname);
        setLastname(lastname);
        setEmail(email);
        setPassword(password);
        setPhone(phone);
        setBirthday(birthday);
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

    public void setBirthday(LocalDate birthday)
    {
        this.birthday = birthday;
    }
    public LocalDate getBirthday()
    {
        return birthday;
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
    @Override public String toString(){
        String s = firstname + " " + lastname + ": " + email + ", " + password;
        return s;
    }
}
