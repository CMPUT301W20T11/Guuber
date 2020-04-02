package com.example.guuber;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;


public class Requests implements Parcelable {

    private LatLng pickup;
    private LatLng dropoff;
    private String email;
    private String status;
    private Double cost;
    private Double tip;


    public Requests(LatLng pickup, LatLng dropoff, String email, String status, Double cost, Double tip){
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.email = email;
        this.status = status;
        this.cost = cost;
        this.tip = tip;
    }

    protected Requests(Parcel in) {
        pickup = in.readParcelable(LatLng.class.getClassLoader());
        dropoff = in.readParcelable(LatLng.class.getClassLoader());
        email = in.readString();
        status = in.readString();
        if (in.readByte() == 0) {
            cost = null;
        } else {
            cost = in.readDouble();
        }
        if (in.readByte() == 0) {
            tip = null;
        } else {
            tip = in.readDouble();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(pickup, flags);
        dest.writeParcelable(dropoff, flags);
        dest.writeString(email);
        dest.writeString(status);
        if (cost == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(cost);
        }
        if (tip == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(tip);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Requests> CREATOR = new Creator<Requests>() {
        @Override
        public Requests createFromParcel(Parcel in) {
            return new Requests(in);
        }

        @Override
        public Requests[] newArray(int size) {
            return new Requests[size];
        }
    };

    public LatLng getPickup(){
        return pickup;
    }

    public LatLng getDropoff(){
        return dropoff;
    }

    public String getRequestEmail(){
        return email;
    }

    public String getStatus(){
        return status;
    }

    public Double getTip(){
        return tip;
    }

    public Double getCost(){
        return cost;
    }

    public void setStatus(String status){
        this.status = status;
    }


}
