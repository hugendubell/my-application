package com.example.Model;

public class Galleries {
    private String galName;
    private String galImg;

    public Galleries() {
    }

    public Galleries(String galName, String galImg) {
        this.galName = galName;
        this.galImg = galImg;
    }

    public String getGalName() {
        return galName;
    }

    public void setGalName(String galName) {
        this.galName = galName;
    }

    public String getGalImg() {
        return galImg;
    }

    public void setGalImg(String galImg) {
        this.galImg = galImg;
    }
}
