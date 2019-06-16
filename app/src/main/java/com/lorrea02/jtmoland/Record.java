package com.lorrea02.jtmoland;


import android.os.Parcel;
import android.os.Parcelable;

public class Record implements Parcelable {
    private String meterNumber;
    private String name;
    private String address;
    private String subd;
    private String accountNum;
    private int previous;
    private int present;
    private float unpaid;
    private float charges;
    private String dueDate;
    private String startDate;
    private String endDate;
    private int read;
    private float amtDue;
    private String waterRemarks;
    private float monthlyBalance;
    private float monthlyCharge;
    private String monthlyRemarks;
    private String lastUpdate;
    private float netAmt;

    @Override
    public String toString() {
        return meterNumber + ","
                + name + ","
                + address + ","
                + subd + ","
                + accountNum + ","
                + previous + ","
                + present + ","
                + unpaid + ","
                + charges + ","
                + dueDate + ","
                + startDate + ","
                + endDate + ","
                + read + ","
                + amtDue + ","
                + waterRemarks + ","
                + monthlyBalance + ","
                + monthlyCharge + ","
                + monthlyRemarks + ","
                + lastUpdate + ","
                + netAmt;
    }

    public Record() {
    }

    public Record(String meterNumber, String name, String address, String subd, String accountNum, int previous, int present, float unpaid, float charges, String dueDate, String startDate, String endDate, int read, float amtDue, String waterRemarks, float monthlyBalance, float monthlyCharge, String monthlyRemarks, String lastUpdate, float netAmt) {
        this.meterNumber = meterNumber;
        this.name = name;
        this.address = address;
        this.subd = subd;
        this.accountNum = accountNum;
        this.previous = previous;
        this.present = present;
        this.unpaid = unpaid;
        this.charges = charges;
        this.dueDate = dueDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.read = read;
        this.amtDue = amtDue;
        this.waterRemarks = waterRemarks;
        this.monthlyBalance = monthlyBalance;
        this.monthlyCharge = monthlyCharge;
        this.monthlyRemarks = monthlyRemarks;
        this.lastUpdate = lastUpdate;
        this.netAmt = netAmt;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSubd() {
        return subd;
    }

    public void setSubd(String subd) {
        this.subd = subd;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public int getPrevious() {
        return previous;
    }

    public void setPrevious(int previous) {
        this.previous = previous;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public float getUnpaid() {
        return unpaid;
    }

    public void setUnpaid(float unpaid) {
        this.unpaid = unpaid;
    }

    public float getCharges() {
        return charges;
    }

    public void setCharges(float charges) {
        this.charges = charges;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public float getAmtDue() {
        return amtDue;
    }

    public void setAmtDue(float amtDue) {
        this.amtDue = amtDue;
    }

    public String getWaterRemarks() {
        return waterRemarks;
    }

    public void setWaterRemarks(String waterRemarks) {
        this.waterRemarks = waterRemarks;
    }

    public float getMonthlyBalance() {
        return monthlyBalance;
    }

    public void setMonthlyBalance(float monthlyBalance) {
        this.monthlyBalance = monthlyBalance;
    }

    public float getMonthlyCharge() {
        return monthlyCharge;
    }

    public void setMonthlyCharge(float monthlyCharge) {
        this.monthlyCharge = monthlyCharge;
    }

    public String getMonthlyRemarks() {
        return monthlyRemarks;
    }

    public void setMonthlyRemarks(String monthlyRemarks) {
        this.monthlyRemarks = monthlyRemarks;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public float getNetAmt() {
        return netAmt;
    }

    public void setNetAmt(float netAmt) {
        this.netAmt = netAmt;
    }

    protected Record(Parcel in) {
        meterNumber = in.readString();
        name = in.readString();
        address = in.readString();
        subd = in.readString();
        accountNum = in.readString();
        previous = in.readInt();
        present = in.readInt();
        unpaid = in.readFloat();
        charges = in.readFloat();
        dueDate = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        read = in.readInt();
        amtDue = in.readFloat();
        waterRemarks = in.readString();
        monthlyBalance = in.readFloat();
        monthlyCharge = in.readFloat();
        monthlyRemarks = in.readString();
        lastUpdate = in.readString();
        netAmt = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(meterNumber);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(subd);
        dest.writeString(accountNum);
        dest.writeInt(previous);
        dest.writeInt(present);
        dest.writeFloat(unpaid);
        dest.writeFloat(charges);
        dest.writeString(dueDate);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeInt(read);
        dest.writeFloat(amtDue);
        dest.writeString(waterRemarks);
        dest.writeFloat(monthlyBalance);
        dest.writeFloat(monthlyCharge);
        dest.writeString(monthlyRemarks);
        dest.writeString(lastUpdate);
        dest.writeFloat(netAmt);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Record> CREATOR = new Parcelable.Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };
}