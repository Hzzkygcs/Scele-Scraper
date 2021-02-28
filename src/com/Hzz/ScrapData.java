package com.Hzz;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ScrapData extends GroupedData{
    public String msg = "";
    public String url = "";
    public String page_title = "";
    public String page_content_hash = "";
    public Map<String, String> content_by_id = new HashMap<>();
    
    

    ScrapData() {}
    ScrapData(GroupedData data, String msg){
        this.msg = msg;
        this.url = data.getUrl();
        this.page_title = data.getPageTitle();
        this.page_content_hash = data.getPageContentHash();
    }

    ScrapData(GroupedData prev, GroupedData curr, String msg){
        this.msg = msg;

        if (! "".equals(prev.getUrl()))
            this.url = prev.getUrl();
        else if (! "".equals(curr.getUrl()))
            this.url = curr.getUrl();
        else throw new RuntimeException("Shouldn't be occured. There's a bug");


        if (! "".equals(prev.getPageTitle()))
            this.page_title = prev.getPageTitle();
        else if (! "".equals(curr.getPageTitle()))
            this.page_title = curr.getPageTitle();
        else throw new RuntimeException("Shouldn't be occured. There's a bug");


        if (! "".equals(prev.getPageContentHash()))
            this.page_content_hash = prev.getPageContentHash();
        else if (! "".equals(curr.getPageContentHash()))
            this.page_content_hash = curr.getPageContentHash();
        else throw new RuntimeException("Shouldn't be occured. There's a bug");
    }

    ScrapData(String msg, String url, String page_title, String page_content_hash) {
        this.msg = msg;
        this.url = url;
        this.page_title = page_title;
        this.page_content_hash = page_content_hash;
    }




    /*   ---------------------  setters and getters  --------------------- */
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPageTitle() { return page_title; }
    public void setPageTitle(String page_title) { this.page_title = page_title; }

    public String getPageContentHash() { return page_content_hash; }
    public void setPageContentHash(String page_content_hash) { this.page_content_hash = page_content_hash; }
    
    public Map getContentMap(){ return content_by_id; }
    public void setContetnMap(Map x){ this.content_by_id = x; }
}
