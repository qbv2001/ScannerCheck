package com.example.scannercheck;


import java.io.Serializable;

public class Nhacungcap implements Serializable {
    private String id;
    private String tenncc,image;
    private String diachi,mota,sdt,tenimage;

    public Nhacungcap(){
    }

    public Nhacungcap(String id, String tenncc, String mota, String diachi, String sdt,String image,String tenimage){
        this.id = id;
        this.tenncc = tenncc;
        this.image = image;
        this.mota = mota;
        this.diachi = diachi;
        this.sdt = sdt;
        this.tenimage = tenimage;

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
