package com.techtack.blue.dto.stock;

import lombok.Data;

import java.util.List;

@Data
public class UpdateNotificationDto {
    private String id;
    private String iud_action;
    private String wifeed_api_id;
    private List<UpdateInfo> update_info;
    private String created_at;
    private String name;
    private String url_demo;

    public static class UpdateInfo {
        private String code;
        private String year;
        private String quarter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIud_action() {
        return iud_action;
    }

    public void setIud_action(String iud_action) {
        this.iud_action = iud_action;
    }

    public String getWifeed_api_id() {
        return wifeed_api_id;
    }

    public void setWifeed_api_id(String wifeed_api_id) {
        this.wifeed_api_id = wifeed_api_id;
    }

    public List<UpdateInfo> getUpdate_info() {
        return update_info;
    }

    public void setUpdate_info(List<UpdateInfo> update_info) {
        this.update_info = update_info;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl_demo() {
        return url_demo;
    }

    public void setUrl_demo(String url_demo) {
        this.url_demo = url_demo;
    }
}
