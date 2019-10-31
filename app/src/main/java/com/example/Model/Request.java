package com.example.Model;

import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String id_number;
    private String total;
    private List<Cart> parties;

    public Request() {
    }

    public Request(String phone, String name, String id_number, String total, List<Cart> parties) {
        this.phone = phone;
        this.name = name;
        this.id_number = id_number;
        this.total = total;
        this.parties = parties;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Cart> getParties() {
        return parties;
    }

    public void setParties(List<Cart> parties) {
        this.parties = parties;
    }
}

