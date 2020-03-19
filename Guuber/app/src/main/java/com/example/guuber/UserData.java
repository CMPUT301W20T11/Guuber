package com.example.guuber;




/**
 * UserData is a signleton containing user information that can be called upon locally
 */
public class UserData {

    private static UserData single_instance = null;

    //private Wallet wallet;
    private String phoneNumber=null;
    private String email=null;
    private String firstName=null;
    private String lastName=null;
    private String uid=null;
    private String username=null;

//    public UserData(String phoneNumber, String email, String firstName, String lastName, String uid, String username){
//        this.phoneNumber = phoneNumber;
//        this.email = email;
//        this.firstName=firstName;
//        this.lastName=lastName;
//        this.uid=uid;
//        this.username=username;
//    }

    public UserData(){
    }

    public static UserData getInstance(){
        if (single_instance == null){
            single_instance = new UserData();
        }
        return single_instance;
    }

    /**
     * Get user phone number
     * @return - User phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set user phone number
     * @param phoneNumber - User phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Get user email
     * @return - User email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set user email
     * @param email - User email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get user first name
     * @return - User first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set user first name
     * @param firstName - User first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get user last name
     * @return - User last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set user last name
     * @param lastName - User last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
