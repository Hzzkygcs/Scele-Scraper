package com.Hzz.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class UrlEntryPoint {

    static String[] URL_entry_points = {

    };
    
    public static void load(String file_pos) throws FileNotFoundException {
        File file_obj = new File(file_pos);
        Scanner file = new Scanner(file_obj);
        
        List<String> queue = new ArrayList<>(100);
        
        while (file.hasNextLine())
            queue.add(file.nextLine());
    
        URL_entry_points = new String[queue.size()];
        URL_entry_points = queue.toArray(URL_entry_points);
        file.close();
    }
    
    public static Deque<String> getList(){
        return new LinkedList<>(Arrays.asList(URL_entry_points));
    }

}
