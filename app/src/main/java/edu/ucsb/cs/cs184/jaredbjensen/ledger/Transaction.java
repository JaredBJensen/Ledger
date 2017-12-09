package edu.ucsb.cs.cs184.jaredbjensen.ledger;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {

    String type;
    long date;
    float amount;
    String description;
    String category;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public Transaction(String type, long date, float amount, String description, String category) {
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.category = category;
    }

    public Transaction(Parcel in){
        type = in.readString();
        date = in.readLong();
        amount = in.readFloat();
        description = in.readString();
        category = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeLong(date);
        dest.writeFloat(amount);
        dest.writeString(description);
        dest.writeString(category);
    }

}
