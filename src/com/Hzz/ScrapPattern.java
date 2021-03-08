package com.Hzz;



import java.util.regex.Pattern;


public class ScrapPattern {
    public String[] regexp = {

            // home page of each course
            // commented-out because it is modified: now we add manually each of the course home page into UrlEntryPoint
            // "https://scele\\.cs\\.ui\\.ac\\.id/course/view\\.php\\?id=\\d+",

            // sub course
            //"https://scele\\.cs\\.ui\\.ac\\.id/course/view\\.php\\?id=\\d+&section=\\d+",

            // course forum
            //"https://scele\\.cs\\.ui\\.ac\\.id/mod/forum/view\\.php\\?id=\\d+"

            // ulangan
            // "https\\://scele.cs.ui.ac.id/mod/assign/view\\.php\\?id=\\d+",

    };

    Pattern[] compiled = new Pattern[regexp.length];

    ScrapPattern(){
        for (int i = 0; i < regexp.length; i++){
            compiled[i] = Pattern.compile(regexp[i]);
        }

    }
}
