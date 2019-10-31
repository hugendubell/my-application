package com.example.Model;

public class Rating {
    private String userPhone;//both key and value
    private String PartyId;
    private String rateValue;
    private String comment;

    public Rating() {

    }

    public Rating(String userPhone, String partyId, String rateValue, String comment) {
        this.userPhone = userPhone;
        PartyId = partyId;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getPartyId() {
        return PartyId;
    }

    public void setPartyId(String partyId) {
        PartyId = partyId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
