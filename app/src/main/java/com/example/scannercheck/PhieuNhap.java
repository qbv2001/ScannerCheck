package com.example.scannercheck;


import java.io.Serializable;

public class PhieuNhap implements Serializable {
    private String maphieu, mamh, mancc, madvt, ngaynhap;
    private Integer soluong;
    private Float dongia,thanhtien;
    public PhieuNhap(){

    }

    public PhieuNhap(String maphieu, String mamh, String mancc, String madvt, Integer soluong, Float dongia){
        this.maphieu = maphieu;
        this.mamh = mamh;
        this.mancc = mancc;
        this.madvt = madvt;
        this.soluong = soluong;
        this.dongia = dongia;
    }

    public String getMaphieu() {
        return maphieu;
    }

    public void setMaphieu(String maphieu) {
        this.maphieu = maphieu;
    }

    public String getMamh() {
        return mamh;
    }

    public void setMamh(String mamh) {
        this.mamh = mamh;
    }

    public String getMancc() {
        return mancc;
    }

    public void setMancc(String mancc) {
        this.mancc = mancc;
    }

    public String getMadvt() {
        return madvt;
    }

    public void setMadvt(String madvt) {
        this.madvt = madvt;
    }

    public Integer getSoluong() {
        return soluong;
    }

    public void setSoluong(Integer soluong) {
        this.soluong = soluong;
    }

    public Float getDongia() {
        return dongia;
    }

    public void setDongia(Float dongia) {
        this.dongia = dongia;
    }
}
