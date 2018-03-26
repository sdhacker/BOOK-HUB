package com.gmonetix.bookspivot.model;

/**
 * Created by SHUBHAM on 09-01-2018.
 */

public class Upload {

    public String name,author,edition;
    public String url;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Upload(String s, String toString) {
    }

    public Upload(String name,String author,String edition, String url) {
        this.name = name;
       // this.author=author;
        //this.edition=edition;
        this.url = url;
    }

    public String getName() {
        return name;
    }
   // public String getAuthor() {
    //    return author;
    //}
    //public String getEdition() {return edition;}

    public String getUrl() {
        return url;
    }
}

