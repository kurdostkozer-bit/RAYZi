package com.example.rayzi.modelclass;

public class ProfileRoot {

    private int getImages;
    private String getText;

    public ProfileRoot(int getImages, String getText) {
        this.getImages = getImages;
        this.getText = getText;
    }

    public int getGetImages() {
        return getImages;
    }

    public void setGetImages(int getImages) {
        this.getImages = getImages;
    }

    public String getGetText() {
        return getText;
    }

    public void setGetText(String getText) {
        this.getText = getText;
    }
}
