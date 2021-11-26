package com.example.scannercheck;


import java.io.Serializable;

public class Nhacungcap implements Serializable {
    private String id;
    private String tenncc;
    private int image;
    private String diachi,mota,sdt;

    public Nhacungcap(){
    }

    public Nhacungcap(String id, String tenncc, String mota, String diachi, String sdt,int image){
        this.id = id;
        this.tenncc = tenncc;
        this.image = image;
        this.mota = mota;
        this.diachi = diachi;
        this.sdt = sdt;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return tenncc;
    }

    public void setName(String tenncc) {
        this.tenncc = tenncc;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    @Override
    public String toString() {
        return "Nhacungcap{" +
                "id='" + id + '\'' +
                ", tenncc='" + tenncc + '\'' +
                ", image=" + image +
                ", diachi='" + diachi + '\'' +
                ", mota='" + mota + '\'' +
                ", sdt='" + sdt + '\'' +
                '}';
    }
}
