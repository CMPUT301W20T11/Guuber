package com.example.guuber;


import android.app.Application;

import com.example.guuber.model.User;

/**
 * UserData is a singleton containing user information that can be called upon locally
 */
public class UserData extends Application {

    private User user = null;
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
