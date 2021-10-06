package com.example.scannercheck;


public class Nhacungcap {
    private String id;
    private String tenncc;
    private int image;
    private String diachi,mota;

    public Nhacungcap(String id, String tenncc,int image){
        this.id = id;
        this.tenncc = tenncc;
        this.image = image;
        this.mota = null;
        this.diachi = null;

    }

    public Nhacungcap(String id, String tenncc,String diachi,String mota,int image){
        this.id = id;
        this.tenncc = tenncc;
        this.image = image;
        this.mota = mota;
        this.diachi = diachi;
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

}
