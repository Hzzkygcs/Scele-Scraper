package com.Hzz;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;


public class UrlEntryPoint {

    static String[] URL_entry_points = {
            "https://scele.cs.ui.ac.id/course/view.php?id=3053",
            "https://scele.cs.ui.ac.id/course/view.php?id=3066",
            "https://scele.cs.ui.ac.id/course/view.php?id=3059",
            "https://scele.cs.ui.ac.id/course/view.php?id=3068",
            "https://scele.cs.ui.ac.id/course/view.php?id=3137",
            "https://scele.cs.ui.ac.id/course/view.php?id=3062"
    };

    public static Deque<String> getList(){
        return new LinkedList<>(Arrays.asList(URL_entry_points));
    }

}
