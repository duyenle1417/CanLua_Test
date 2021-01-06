package com.example.canlua;

public class History {
    private String TenGiongLua, Timestamp;
    private int DonGia, BaoBi, SoBao, TienCoc;
    private long ID;//id khách hàng
    private double TongSoKG, ThanhTien;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getTenGiongLua() {
        return TenGiongLua;
    }

    public void setTenGiongLua(String tenGiongLua) {
        TenGiongLua = tenGiongLua;
    }

    public int getDonGia() {
        return DonGia;
    }

    public void setDonGia(int donGia) {
        DonGia = donGia;
    }

    public int getBaoBi() {
        return BaoBi;
    }

    public void setBaoBi(int baoBi) {
        BaoBi = baoBi;
    }

    public int getSoBao() {
        return SoBao;
    }

    public void setSoBao(int soBao) {
        SoBao = soBao;
    }

    public double getTongSoKG() {
        return TongSoKG;
    }

    public void setTongSoKG(double tongSoKG) {
        TongSoKG = tongSoKG;
    }

    public double getThanhTien() {
        return ThanhTien;
    }

    public void setThanhTien(double thanhTien) {
        ThanhTien = thanhTien;
    }

    public int getTienCoc() {
        return TienCoc;
    }

    public void setTienCoc(int tienCoc) {
        TienCoc = tienCoc;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }
}
