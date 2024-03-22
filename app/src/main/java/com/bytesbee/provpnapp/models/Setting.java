package com.bytesbee.provpnapp.models;

import java.io.Serializable;

/**
 * Created by BytesBee.
 *
 * @author BytesBee
 * @link http://bytesbee.com
 */
public class Setting implements Serializable {

    private int id;
    private String app_fcm_key;
    private String api_key;
    private int app_mandatory_login;
    private int app_channel_grid;
    private String app_name;
    private String app_logo;
    private String app_email;
    private String app_version;
    private String app_author;
    private String app_contact;
    private String app_website;
    private String app_developed_by;
    private String app_description;
    private String app_privacy_policy;
    private String publisher_id;
    private int interstital_ad;
    private String interstital_ad_id;
    private int interstital_ad_click;
    private int banner_ad;

    private int native_ad;
    private String native_ad_id;

    public int force_version_code; //This must be integer and updated versionCode from build.gradle
    public int force_update; //1= Yes , 0=No
    public String force_title;
    public String force_message;
    public String force_yes_button;
    public String force_no_button;
    public String force_source;//Google Playstore OR Live Server APK URL
    public String force_apk_link;

    private String banner_ad_id;
    private String createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp_fcm_key() {
        return app_fcm_key;
    }

    public void setApp_fcm_key(String app_fcm_key) {
        this.app_fcm_key = app_fcm_key;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public int getApp_mandatory_login() {
        return app_mandatory_login;
    }

    public void setApp_mandatory_login(int app_mandatory_login) {
        this.app_mandatory_login = app_mandatory_login;
    }

    public int getApp_channel_grid() {
        return app_channel_grid;
    }

    public void setApp_channel_grid(int app_channel_grid) {
        this.app_channel_grid = app_channel_grid;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_logo() {
        return app_logo;
    }

    public void setApp_logo(String app_logo) {
        this.app_logo = app_logo;
    }

    public String getApp_email() {
        return app_email;
    }

    public void setApp_email(String app_email) {
        this.app_email = app_email;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getApp_author() {
        return app_author;
    }

    public void setApp_author(String app_author) {
        this.app_author = app_author;
    }

    public String getApp_contact() {
        return app_contact;
    }

    public void setApp_contact(String app_contact) {
        this.app_contact = app_contact;
    }

    public String getApp_website() {
        return app_website;
    }

    public void setApp_website(String app_website) {
        this.app_website = app_website;
    }

    public String getApp_developed_by() {
        return app_developed_by;
    }

    public void setApp_developed_by(String app_developed_by) {
        this.app_developed_by = app_developed_by;
    }

    public String getApp_description() {
        return app_description;
    }

    public void setApp_description(String app_description) {
        this.app_description = app_description;
    }

    public String getApp_privacy_policy() {
        return app_privacy_policy;
    }

    public void setApp_privacy_policy(String app_privacy_policy) {
        this.app_privacy_policy = app_privacy_policy;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(String publisher_id) {
        this.publisher_id = publisher_id;
    }

    public int getInterstital_ad() {
        return interstital_ad;
    }

    public void setInterstital_ad(int interstital_ad) {
        this.interstital_ad = interstital_ad;
    }

    public String getInterstital_ad_id() {
        return interstital_ad_id;
    }

    public void setInterstital_ad_id(String interstital_ad_id) {
        this.interstital_ad_id = interstital_ad_id;
    }

    public int getInterstital_ad_click() {
        return interstital_ad_click;
    }

    public void setInterstital_ad_click(int interstital_ad_click) {
        this.interstital_ad_click = interstital_ad_click;
    }

    public int getBanner_ad() {
        return banner_ad;
    }

    public void setBanner_ad(int banner_ad) {
        this.banner_ad = banner_ad;
    }

    public int getForce_version_code() {
        return force_version_code;
    }

    public void setForce_version_code(int force_version_code) {
        this.force_version_code = force_version_code;
    }

    public int getForce_update() {
        return force_update;
    }

    public void setForce_update(int force_update) {
        this.force_update = force_update;
    }

    public String getForce_title() {
        return force_title;
    }

    public void setForce_title(String force_title) {
        this.force_title = force_title;
    }

    public String getForce_message() {
        return force_message;
    }

    public void setForce_message(String force_message) {
        this.force_message = force_message;
    }

    public String getForce_yes_button() {
        return force_yes_button;
    }

    public void setForce_yes_button(String force_yes_button) {
        this.force_yes_button = force_yes_button;
    }

    public String getForce_no_button() {
        return force_no_button;
    }

    public void setForce_no_button(String force_no_button) {
        this.force_no_button = force_no_button;
    }

    public String getForce_source() {
        return force_source;
    }

    public void setForce_source(String force_source) {
        this.force_source = force_source;
    }

    public String getForce_apk_link() {
        return force_apk_link;
    }

    public void setForce_apk_link(String force_apk_link) {
        this.force_apk_link = force_apk_link;
    }

    public String getBanner_ad_id() {
        return banner_ad_id;
    }

    public void setBanner_ad_id(String banner_ad_id) {
        this.banner_ad_id = banner_ad_id;
    }

    public int getNative_ad() {
        return native_ad;
    }

    public void setNative_ad(int native_ad) {
        this.native_ad = native_ad;
    }

    public String getNative_ad_id() {
        return native_ad_id;
    }

    public void setNative_ad_id(String native_ad_id) {
        this.native_ad_id = native_ad_id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "id=" + id +
                ", app_fcm_key='" + app_fcm_key + '\'' +
                ", api_key='" + api_key + '\'' +
                ", app_mandatory_login=" + app_mandatory_login +
                ", app_channel_grid=" + app_channel_grid +
                ", app_name='" + app_name + '\'' +
                ", app_logo='" + app_logo + '\'' +
                ", app_email='" + app_email + '\'' +
                ", app_version='" + app_version + '\'' +
                ", app_author='" + app_author + '\'' +
                ", app_contact='" + app_contact + '\'' +
                ", app_website='" + app_website + '\'' +
                ", app_developed_by='" + app_developed_by + '\'' +
                ", app_description='" + app_description + '\'' +
                ", app_privacy_policy='" + app_privacy_policy + '\'' +
                ", publisher_id='" + publisher_id + '\'' +
                ", interstital_ad=" + interstital_ad +
                ", interstital_ad_id='" + interstital_ad_id + '\'' +
                ", interstital_ad_click=" + interstital_ad_click +
                ", banner_ad=" + banner_ad +
                ", force_version_code=" + force_version_code +
                ", force_update=" + force_update +
                ", force_title='" + force_title + '\'' +
                ", force_message='" + force_message + '\'' +
                ", force_yes_button='" + force_yes_button + '\'' +
                ", force_no_button='" + force_no_button + '\'' +
                ", force_source='" + force_source + '\'' +
                ", force_apk_link='" + force_apk_link + '\'' +
                ", banner_ad_id='" + banner_ad_id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}