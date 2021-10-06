package com.example.scannercheck;


import java.io.Serializable;

public class Mathang implements Serializable {
    private String id;
    private String tenmh;
    private int image,soluong;
    private String datetime;
    public Mathang(String id, String name, int soluong,String datetime,int image){
        this.id = id;
        this.tenmh = name;
        this.image = image;
        this.soluong = soluong;
        this.datetime = datetime;
    }

    public Mathang(String id, String tenmh,int image){
        this.id = id;
        this.tenmh = tenmh;
        this.image = image;
        this.soluong = 0;
        this.datetime = null;

    }

    public Mathang(String id, String tenmh,int soluong,int image){
        this.id = id;
        this.tenmh = tenmh;
        this.image = image;
        this.soluong = soluong;
        this.datetime = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return tenmh;
    }

    public void setName(String tenmh) {
        this.tenmh = tenmh;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDateTime() {
        return datetime;
    }

    public void setDateTime(String datetime) {
        this.datetime = datetime;
    }

}
