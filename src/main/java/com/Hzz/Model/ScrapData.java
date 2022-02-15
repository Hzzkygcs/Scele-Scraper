package com.Hzz.Model;



import com.Hzz.Model.GroupedData;

import java.io.Serializable;
import java.util.*;




public class ScrapData extends GroupedData {
    public String msg = "";
    public String url = "";
    public String page_title = "";
    public String page_content_hash = "";
    public SortedMap<String, String> content_by_id = new TreeMap<>(new TreeMapComparator());
    
    
    /*
    content_by_id's key is usually "section-(digit number)" where we want it to be sorted by its digit, not
    its string, so we do need a comparator
     */
    static class TreeMapComparator implements Comparator<String>, Serializable {
        public int extractInt(String str_to_be_extracted){
            String pattern = "section-";
            int pattern_len = pattern.length();
            String substring = str_to_be_extracted.substring(pattern_len);
            return Integer.parseInt(substring);
        }
        
        @Override
        public int compare(String o1, String o2) {
            Integer o1_extracted_key = extractInt(o1);
            Integer o2_extracted_key = extractInt(o2);
            return o1_extracted_key.compareTo(o2_extracted_key);
        }
    }
    

    public ScrapData() {}
    ScrapData(GroupedData data, String msg){
        this.msg = msg;
        this.url = data.getUrl();
        this.page_title = data.getPageTitle();
        this.page_content_hash = data.getPageContentHash();
    }

    public ScrapData(GroupedData prev, GroupedData curr, String msg){
        this.msg = msg;

        if (! "".equals(prev.getUrl()))
            this.url = prev.getUrl();
        else if (! "".equals(curr.getUrl()))
            this.url = curr.getUrl();
        else throw new RuntimeException("Shouldn't be occurred. There's a bug");


        if (! "".equals(prev.getPageTitle()))
            this.page_title = prev.getPageTitle();
        else if (! "".equals(curr.getPageTitle()))
            this.page_title = curr.getPageTitle();
        else throw new RuntimeException("Shouldn't be occurred. There's a bug");


        if (! "".equals(prev.getPageContentHash()))
            this.page_content_hash = prev.getPageContentHash();
        else if (! "".equals(curr.getPageContentHash()))
            this.page_content_hash = curr.getPageContentHash();
        else throw new RuntimeException("Shouldn't be occurred. There's a bug");
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
    
    public SortedMap<String, String> getContentMap(){ return content_by_id; }
    public void setContentMap(SortedMap<String, String> x){ this.content_by_id = x; }
    
    
}
