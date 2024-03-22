package com.bytesbee.provpnapp.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by BytesBee.
 *
 * @author BytesBee
 * @link https://bytesbee.com
 */
public class VPNServers extends Pagination implements Serializable {

    //    public String api_key;
    public int id;
    public String serverName;
    public String flagURL;
    public String ovpnConfig;
    public String username;
    public String password;
    public int isPaid;//0=Free, 1=Paid
    public int active;//0=InActive, 1=Active
    public String createdAt;
    public String updatedAt;

    public VPNServers() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getFlagURL() {
        return flagURL;
    }

    public void setFlagURL(String flagURL) {
        this.flagURL = flagURL;
    }

    public String getOvpnConfig() {
        return ovpnConfig;
    }

    public void setOvpnConfig(String ovpnConfig) {
        this.ovpnConfig = ovpnConfig;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(int isPaid) {
        this.isPaid = isPaid;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @NonNull
    @Override
    public String toString() {
        return "VPNServers{" +
                "id=" + id +
                ", serverName='" + serverName + '\'' +
                ", flagURL='" + flagURL + '\'' +
                ", ovpnConfig='" + ovpnConfig + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isPaid=" + isPaid +
                ", active=" + active +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
