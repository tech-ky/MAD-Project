package com.sp.mad_project;

public class UserInfo1 {

    private String username;
    private int points;
    private String userKey;

    public UserInfo1(String username, int points, String userKey) {
        this.username = username;
        this.points = points;
        this.userKey = userKey;
    }

    public String getUsername() {
        return username;
    }

    public int getPoints() {
        return points;
    }

    public String getUserKey() {
        return userKey;
    }
}
