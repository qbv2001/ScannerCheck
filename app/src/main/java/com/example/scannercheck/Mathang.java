package com.example.scannercheck;


import java.io.Serializable;

public class Mathang implements Serializable {
    private String id;
    private String tenmh, dvt,image,tenimage;
    private float dongia;
    public Mathang(){

    }

    public Mathang(String id, String name, float dongia, String image,String tenimage, String dvt){
        this.id = id;
        this.tenmh = name;
        this.image = image;
        this.dvt = dvt;
        this.dongia = dongia;
        this.tenimage = tenimage;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getDvt() {
        return dvt;
    }

    public void setDvt(String dvt) {
        this.dvt = dvt;
    }

    public float getDongia() {
        return dongia;
    }

    public void setDongia(float dongia) {
        this.dongia = dongia;
    }

    public String getTenimage() {
        return tenimage;
    }

    public void setTenimage(String tenimage) {
        this.tenimage = tenimage;
    }

    @Override
    public String toString() {
        return "Mathang{" +
                "id='" + id + '\'' +
                ", tenmh='" + tenmh + '\'' +
                ", dvt='" + dvt + '\'' +
                ", image=" + image +
                ", dongia=" + dongia +
                '}';
    }
}
