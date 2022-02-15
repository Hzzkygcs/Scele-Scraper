package com.Hzz.Model;

import java.io.Serializable;

public class GroupedData implements Serializable {
    public String url = "";
    public String page_title = "";
    public String page_content_hash = "";


    public GroupedData(String url, String page_title, String page_content_hash) {
        this.url = url;
        this.page_title = page_title;
        this.page_content_hash = page_content_hash;
    }
    
    public GroupedData() {
    }


    /*   ---------------------  setters and getters  --------------------- */
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPageTitle() { return page_title; }
    public void setPageTitle(String page_title) { this.page_title = page_title; }

    public String getPageContentHash() { return page_content_hash; }
    public void setPageContentHash(String page_content_hash) { this.page_content_hash = page_content_hash; }
}
