package com.example.kushal.homerentapplication;

public class Model
{
    private int id;
    private String name;
    private String number;
    private String address;
    private String room;
    private String price;
    private byte[] image;

    public Model(int id, String name,  String address, String number, String room, String price, byte[] image) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.address = address;
        this.room = room;
        this.price = price;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
