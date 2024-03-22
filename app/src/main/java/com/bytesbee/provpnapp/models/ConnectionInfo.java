package com.bytesbee.provpnapp.models;

import androidx.annotation.NonNull;

import com.bytesbee.provpnapp.managers.SessionManager;

/**
 * The type Data info.
 */
public class ConnectionInfo {
    private long date;
    private final String location;
    private final String timer;
    private String download;
    private final String upload;

    public ConnectionInfo() {
        this.date = Long.parseLong(SessionManager.get().getDeviceCreated());
        this.location = "--";
        this.timer = "00:00:00";
        this.download = "0.0";
        this.upload = "0.0";
    }

    /**
     * Instantiates a new Data info.
     *
     * @param date     the date
     * @param ping     the ping
     * @param download the download
     * @param upload   the upload
     */
    public ConnectionInfo(long date, String location, String ping, String download, String upload) {
        this.date = date;
        this.location = location;
        this.timer = ping;
        this.download = download;
        this.upload = upload;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public long getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(long date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public String getTimer() {
        return timer;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getUpload() {
        return upload;
    }

    @NonNull
    @Override
    public String toString() {
        return "ConnectionInfo{" +
                "date=" + date +
                ", location='" + location + '\'' +
                ", timer='" + timer + '\'' +
                ", download='" + download + '\'' +
                ", upload='" + upload + '\'' +
                '}';
    }
}
