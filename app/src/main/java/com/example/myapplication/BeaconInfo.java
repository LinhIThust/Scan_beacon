package com.example.myapplication;

public class BeaconInfo {
    private String beaconName;
    private String beaconMac;
    private String beaconCompanyId;
    private Integer beaconLenData;
    private String beaconTypeData;
    private String beaconUuid;
    private Integer beaconMajor;
    private Integer beaconMinor;

    public BeaconInfo() {
    }

    public BeaconInfo(String beaconName, String beaconMac, String beaconCompanyId, Integer beaconLenData, String beaconTypeData, String beaconUuid, Integer beaconMajor, Integer beaconMinor) {
        this.beaconName = beaconName;
        this.beaconMac = beaconMac;
        this.beaconCompanyId = beaconCompanyId;
        this.beaconLenData = beaconLenData;
        this.beaconTypeData = beaconTypeData;
        this.beaconUuid = beaconUuid;
        this.beaconMajor = beaconMajor;
        this.beaconMinor = beaconMinor;
    }

    public String getBeaconName() {
        return beaconName;
    }

    public void setBeaconName(String beaconName) {
        this.beaconName = beaconName;
    }

    public String getBeaconMac() {
        return beaconMac;
    }

    public void setBeaconMac(String beaconMac) {
        this.beaconMac = beaconMac;
    }

    public String getBeaconCompanyId() {
        return beaconCompanyId;
    }

    public void setBeaconCompanyId(String beaconCompanyId) {
        this.beaconCompanyId = beaconCompanyId;
    }

    public Integer getBeaconLenData() {
        return beaconLenData;
    }

    public void setBeaconLenData(Integer beaconLenData) {
        this.beaconLenData = beaconLenData;
    }

    public String getBeaconTypeData() {
        return beaconTypeData;
    }

    public void setBeaconTypeData(String beaconTypeData) {
        this.beaconTypeData = beaconTypeData;
    }

    public String getBeaconUuid() {
        return beaconUuid;
    }

    public void setBeaconUuid(String beaconUuid) {
        this.beaconUuid = beaconUuid;
    }

    public Integer getBeaconMajor() {
        return beaconMajor;
    }

    public void setBeaconMajor(Integer beaconMajor) {
        this.beaconMajor = beaconMajor;
    }

    public Integer getBeaconMinor() {
        return beaconMinor;
    }

    public void setBeaconMinor(Integer beaconMinor) {
        this.beaconMinor = beaconMinor;
    }

    @Override
    public String toString() {
        return "BeaconInfo{" +
                "beaconName='" + beaconName + '\'' +
                ", beaconMac='" + beaconMac + '\'' +
                ", beaconCompanyId='" + beaconCompanyId + '\'' +
                ", beaconLenData=" + beaconLenData +
                ", beaconTypeData='" + beaconTypeData + '\'' +
                ", beaconUuid='" + beaconUuid + '\'' +
                ", beaconMajor=" + beaconMajor +
                ", beaconMinor=" + beaconMinor +
                '}';
    }
}
