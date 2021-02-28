package com.Hzz;

import java.io.Serializable;


@Deprecated
public class DataDifference implements Serializable {
    GroupedData previous_data;
    GroupedData current_data;
    String difference_note = "";

    DataDifference() {
        this.previous_data = new GroupedData();
        this.current_data = new GroupedData();
    }

    DataDifference(GroupedData previous_data, GroupedData current_data, String difference_note) {
        this.previous_data = previous_data;
        this.current_data = current_data;
        this.difference_note = difference_note;
    }


    public String getUrl(){
        GroupedData prev = this.previous_data;
        GroupedData curr = this.current_data;

        if ("".equals(prev.url)){
            assert !"".equals(curr.url);
            return curr.url;
        } else if ("".equals(curr.url)){
            assert !"".equals(prev.url);
            return prev.url;
        }else if (curr.url.equals(prev.url)){
            return prev.url;
        }else // most cases, prev.url and curr.url is equal or either blank
            throw new RuntimeException("This shouldn't be occured. There's a bug");
    }

    public String getPageTitle(){
        GroupedData prev = this.previous_data;
        GroupedData curr = this.current_data;

        if ("".equals(prev.page_title)){
            assert !"".equals(curr.page_title);
            return curr.page_title;
        } else if ("".equals(curr.url)){
            assert !"".equals(prev.page_title);
            return prev.page_title;
        }else if (curr.page_title.equals(prev.page_title)){
            return prev.page_title;
        }else // most cases, prev.page_title and curr.page_title is equal or either blank
            throw new RuntimeException("This shouldn't be occured. There's a bug");
    }


}
