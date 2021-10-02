package com.example.cryptochime;

import android.os.Parcel;
import android.os.Parcelable;

public class CurrencyRVModel implements Parcelable {

    String symbol, name, logoURL;
    double price, pc24h;

    protected CurrencyRVModel(Parcel in) {
        symbol = in.readString();
        name = in.readString();
        price = in.readDouble();
        pc24h = in.readDouble();
        logoURL = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(symbol);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeDouble(pc24h);
        dest.writeString(logoURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CurrencyRVModel> CREATOR = new Creator<CurrencyRVModel>() {
        @Override
        public CurrencyRVModel createFromParcel(Parcel in) {
            return new CurrencyRVModel(in);
        }

        @Override
        public CurrencyRVModel[] newArray(int size) {
            return new CurrencyRVModel[size];
        }
    };

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPc24h() {
        return pc24h;
    }

    public void setPc24h(double pc24h) {
        this.pc24h = pc24h;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public CurrencyRVModel(String symbol, String name, double price, double pc24h) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.pc24h = pc24h;
    }

    public CurrencyRVModel(String symbol, String name, String logoURL, double price) {
        this.symbol = symbol;
        this.name = name;
        this.logoURL = logoURL;
        this.price = price;
    }
}
