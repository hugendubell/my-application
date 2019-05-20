package com.example.Model;

public class Party {
    private String Name,Image,Description,Price,PartyId;

    public Party() {
    }

    public Party(String name, String image, String description, String price, String partyId) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        PartyId = partyId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getPartyId() {
        return PartyId;
    }

    public void setPartyId(String partyId) {
        PartyId = partyId;
    }
}
