package com.example.scannercheck;


import java.io.Serializable;

public class Donvitinh implements Serializable {
    private String id, ma_mh;
    private String tendvt;
    private float dongia;
    private int quydoi;

    public Donvitinh(){

    }

    public Donvitinh(String id, String tendvt, float dongia, int quydoi){
        this.id = id;
        this.tendvt = tendvt;
        this.dongia = dongia;
        this.quydoi = quydoi;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTendvt() {
        return tendvt;
    }

    public void setTendvt(String tendvt) {
        this.tendvt = tendvt;
    }

    public float getDongia() {
        return dongia;
    }

    public void setDongia(float dongia) {
        this.dongia = dongia;
    }

    public int getQuydoi() {
        return quydoi;
    }

    public void setQuydoi(int quydoi) {
        this.quydoi = quydoi;
    }

    @Override
    public String toString() {
        return "Donvitinh{" +
                "id='" + id + '\'' +
                ", tendvt='" + tendvt + '\'' +
                ", dongia='" + dongia + '\'' +
                ", quydoi=" + quydoi +
                '}';
    }
}
