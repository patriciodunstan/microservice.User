package entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
public class User {
    

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

  // getters and setters

  public String getEmail() {

    return email;

}



public void setEmail(String email) {

    this.email = email;

}



public String getFirstName() {

    return firstName;

}



public void setFirstName(String firstName) {

    this.firstName = firstName;

}



public String getLastName() {

    return lastName;

}



public void setLastName(String lastName) {

    this.lastName = lastName;

}



public String getPassword() {

    return password;

}



public void setPassword(String password) {

    this.password = password;

}

}
