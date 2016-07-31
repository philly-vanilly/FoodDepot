package de.uni_hamburg.vsis.fooddepot.fooddepotclient.model;

public class Account {
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mUsername;
    private String mPassword;

    public Account() {}

    public String getFirstName() {
        return mFirstName;
    }
    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }
    public String getLastName() {
        return mLastName;
    }
    public void setLastName(String lastName) {
        mLastName = lastName;
    }
    public String getEmail() {
        return mEmail;
    }
    public void setEmail(String email) {
        mEmail = email;
    }
    public String getUsername() {
        return mUsername;
    }
    public void setUsername(String username) {
        mUsername = username;
    }
    public String getPassword() {
        return mPassword;
    }
    public void setPassword(String password) {
        mPassword = password;
    }
}
