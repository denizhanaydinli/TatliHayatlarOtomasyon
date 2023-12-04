package com.example.tatlihayatlar.tatlihayatlarotomasyon.Entity;

public class Masa {
    private int masaNo;
    private boolean dolu;

    public Masa(int masaNo, boolean dolu) {
        this.masaNo = masaNo;
        this.dolu = dolu;
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
}
