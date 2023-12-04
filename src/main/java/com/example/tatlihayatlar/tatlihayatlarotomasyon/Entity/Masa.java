package com.example.tatlihayatlar.tatlihayatlarotomasyon.Entity;

import java.util.ArrayList;
import java.util.List;

public class Masa {
    private int masaNo;
    private boolean dolu;
    private List<String> adisyon;

    public Masa(int masaNo, boolean dolu) {
        this.masaNo = masaNo;
        this.dolu = dolu;
        this.adisyon = new ArrayList<>();
    }

    public int getMasaNo() {
        return masaNo;
    }

    public void setMasaNo(int masaNo) {
        this.masaNo = masaNo;
    }

    public boolean isDolu() {
        return dolu;
    }

    public void setDolu(boolean dolu) {
        this.dolu = dolu;
    }
    public List<String> getAdisyon() {
        return adisyon;
    }

    public void setAdisyon(List<String> adisyon) {
        this.adisyon = adisyon;
    }

    public void adisyonEkle(String urun) {
        this.adisyon.add(urun);
    }
}
