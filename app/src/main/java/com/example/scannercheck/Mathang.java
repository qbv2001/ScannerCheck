package com.example.scannercheck;


import java.io.Serializable;
import java.util.List;

public class Mathang implements Serializable {
    private String id;
    private String tenmh, image, tenimage, dvt;
    private Float dongia;
    private List<Donvitinh> donvitinhs;
    public Mathang(){

    }

    public Mathang(String id, String name, String image,String tenimage, List<Donvitinh> donvitinhs){
        this.id = id;
        this.tenmh = name;
        this.image = image;
        this.tenimage = tenimage;
        this.donvitinhs = donvitinhs;
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

    public String getTenimage() {
        return tenimage;
    }

    public void setTenimage(String tenimage) {
        this.tenimage = tenimage;
    }

    public String getDvt() {
        return dvt;
    }

    public void setDvt(String dvt) {
        this.dvt = dvt;
    }

    public Float getDongia() {
        return dongia;
    }

    public void setDongia(Float dongia) {
        this.dongia = dongia;
    }

    public List<Donvitinh> getDonvitinhs() {
        return donvitinhs;
    }

    public void setDonvitinhs(List<Donvitinh> donvitinhs) {
        this.donvitinhs = donvitinhs;
    }

    @Override
    public String toString() {
        return "Mathang{" +
                "id='" + id + '\'' +
                ", tenmh='" + tenmh + '\'' +
                ", image=" + image +
                '}';
    }
}
