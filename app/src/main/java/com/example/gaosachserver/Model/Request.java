package com.example.gaosachserver.Model;

import java.util.List;

public class Request {

    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private List<Order> rices;


    public Request() {
    }

    public Request(String phone, String name, String address, String total, List<Order> rices) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.rices = rices;
        this.status= "0"; //mac dinh 0: dat,1:nguoi giao
        //
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getRices() {
        return rices;
    }

    public void setRices(List<Order> rices) {
        this.rices = rices;
    }

}
