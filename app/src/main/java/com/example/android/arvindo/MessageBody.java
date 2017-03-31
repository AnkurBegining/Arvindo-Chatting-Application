package com.example.android.arvindo;

/**
 * Created by Lenovo on 30-03-2017.
 */

public class MessageBody {

    private String name;
    private String text;
    private String photoUrl;

    public MessageBody(){

    }

    public MessageBody(String text, String name, String photoUrl){
        this.name=name;
        this.text=text;
        this.photoUrl=photoUrl;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }
}
