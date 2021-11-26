package com.example.scannercheck;


import java.io.Serializable;

public class Mathang implements Serializable {
    private String id;
    private String tenmh, dvt, mota, nhacc;
    private int soluong, image;
    private String datetime;
    private float dongia;
    public Mathang(){

    }

    public Mathang(String id, String name, int soluong, float dongia, String datetime, int image, String dvt, String mota, String nhacc){
        this.id = id;
        this.tenmh = name;
        this.image = image;
        this.soluong = soluong;
        this.datetime = datetime;
        this.dvt = dvt;
        this.mota = mota;
        this.nhacc = nhacc;
        this.dongia = dongia;
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

    public String getDvt() {
        return dvt;
    }

    public void setDvt(String dvt) {
        this.dvt = dvt;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getNhacc() {
        return nhacc;
    }

    public void setNhacc(String nhacc) {
        this.nhacc = nhacc;
    }

    public float getDongia() {
        return dongia;
    }

    public void setDongia(float dongia) {
        this.dongia = dongia;
    }

    @Override
    public String toString() {
        return "Mathang{" +
                "id='" + id + '\'' +
                ", tenmh='" + tenmh + '\'' +
                ", dvt='" + dvt + '\'' +
                ", mota='" + mota + '\'' +
                ", nhacc='" + nhacc + '\'' +
                ", soluong=" + soluong +
                ", image=" + image +
                ", datetime='" + datetime + '\'' +
                ", dongia=" + dongia +
                '}';
    }
}
