package com.example.lkimberly.userstories.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;


@ParseClassName("User")
public class User extends ParseUser{

    private  static final String KEY_NAME = "name";
    private  static final String KEY_LINKEDIN = "linkedin";
    private  static final String KEY_FACEBOOK = "facebook";
    private  static final String KEY_TWITTER = "twitter";
    private  static final String KEY_IMAGE = "image";
    private  static final String KEY_USER = "user";
    private static final String KEY_DATE = "createdAt";
    private static final String KEY_INSTITUTION = "institution";



    // Get name
    public String getName() {
        return  getString(KEY_NAME);
    }

    public void setDescription(String name) {
        put(KEY_NAME, name);
    }


    // Get profile pic
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }


    // Get name linkedin url
    public String getLinkedIn() {
        return  getString(KEY_LINKEDIN);
    }

    public void setLinkedIn(String name) {
        put(KEY_LINKEDIN, name);
    }


    // Get twitter url
    public String getTwitter() {
        return  getString(KEY_TWITTER);
    }

    public void setTwitter(String name) {
        put(KEY_TWITTER, name);
    }


    // Get institution
    public String getInstitution() {
        return  getString(KEY_INSTITUTION);
    }

    public void setInstitution(String name) {
        put(KEY_INSTITUTION, name);
    }


    //Get created at
    public String createdAt() {
        return getString(KEY_DATE);
    }



//    public ParseFile getMedia() {
//        return getParseFile(KEY_IMAGE);
//    }
//
//    public void setMedia(ParseFile parseFile) {
//        put(KEY_IMAGE, parseFile);
//    }

    public static class Query extends ParseQuery<User> {
        public Query() {
            super(User.class);
        }

        public Query getTop() {
            orderByDescending("createdAt");
            setLimit(20);
            return  this;
        }

//        public Query withUser() {
//            include("user");
//            return this;
//        }
    }

}