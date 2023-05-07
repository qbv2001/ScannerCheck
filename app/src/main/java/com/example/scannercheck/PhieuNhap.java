package com.example.scannercheck;


import java.io.Serializable;

public class PhieuNhap implements Serializable {
    private String maphieu, mamh, tenmh, mancc, tenncc, madvt, tendvt;
    private Integer soluong;
    Long ngaynhap;
    private Float dongia,thanhtien;
    public PhieuNhap(){

    }

    public PhieuNhap(String maphieu, String mamh, String tenmh, String mancc, String tenncc, String madvt, String tendvt, Integer soluong, Float dongia, Float thanhtien, Long ngaynhap){
        this.maphieu = maphieu;
        this.mamh = mamh;
        this.mancc = mancc;
        this.madvt = madvt;
        this.soluong = soluong;
        this.dongia = dongia;
        this.thanhtien = thanhtien;
        this.ngaynhap = ngaynhap;
        this.tendvt = tendvt;
        this.tenncc = tenncc;
        this.tenmh = tenmh;
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

    public Long getNgaynhap() {
        return ngaynhap;
    }

    public void setNgaynhap(Long ngaynhap) {
        this.ngaynhap = ngaynhap;
    }

    public Float getThanhtien() {
        return thanhtien;
    }

    public void setThanhtien(Float thanhtien) {
        this.thanhtien = thanhtien;
    }

    public String getTenmh() {
        return tenmh;
    }

    public void setTenmh(String tenmh) {
        this.tenmh = tenmh;
    }

    public String getTenncc() {
        return tenncc;
    }

    public void setTenncc(String tenncc) {
        this.tenncc = tenncc;
    }

    public String getTendvt() {
        return tendvt;
    }

    public void setTendvt(String tendvt) {
        this.tendvt = tendvt;
    }
}
