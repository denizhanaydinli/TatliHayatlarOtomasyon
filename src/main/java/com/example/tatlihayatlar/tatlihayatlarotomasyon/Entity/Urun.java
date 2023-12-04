package com.example.tatlihayatlar.tatlihayatlarotomasyon.Entity;

public class Urun {
    private String ad;
    private Integer id;
    private double fiyat;

    public Urun(int id, String ad, double fiyat) {
        this.id=id;
        this.ad = ad;
        this.fiyat = fiyat;
    }

    public String getAd() {
        return ad;
    }

    public Integer getId() {
        return id;
    }

    public double getFiyat() {
        return fiyat;
    }
}
