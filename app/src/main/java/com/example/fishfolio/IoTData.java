package com.example.fishfolio;

public class IoTData {
    String temp = "25", turbo = "890", oxy = "89", ph = "7.1";

    public IoTData(String temp, String turbo, String ph, String oxy) {
        this.temp = temp;
        this.turbo = turbo;
        this.oxy = oxy;
        this.ph = ph;
    }

    public IoTData() {
    }

    public String getTemp() {
        return temp;
    }

    public String getTurbo() {
        return turbo;
    }

    public String getOxy() {
        return oxy;
    }

    public String getPh() {
        return ph;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setTurbo(String turbo) {
        this.turbo = turbo;
    }

    public void setOxy(String oxy) {
        this.oxy = oxy;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }
}
