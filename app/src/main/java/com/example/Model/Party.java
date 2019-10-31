package com.example.Model;

public class Party {
    private String Name,Image,Description,Price,PartyId,Address,ImageMap;

    public Party() {
    }

    public Party(String name, String image, String description, String price, String partyId, String address, String imageMap) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        PartyId = partyId;
        Address = address;
        ImageMap = imageMap;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getImageMap() {
        return ImageMap;
    }

    public void setImageMap(String imageMap) {
        ImageMap = imageMap;
    }
}
