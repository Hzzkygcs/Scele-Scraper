package com.Hzz;



import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


public class ScrapPattern {
    public String[] regexp = {

            // home page of each course
            // commented-out because it is modified: now we add manually each of the course home page into UrlEntryPoint
            // "https://scele\\.cs\\.ui\\.ac\\.id/course/view\\.php\\?id=\\d+",

            // sub course
            "https://scele\\.cs\\.ui\\.ac\\.id/course/view\\.php\\?id=\\d+&section=\\d+",

            // course forum
            "https://scele\\.cs\\.ui\\.ac\\.id/mod/forum/view\\.php\\?id=\\d+",

            // assignment
            "https://scele\\.cs\\.ui\\.ac\\.id/mod/assign/view.php\\?id=\\d+$"
            
            // ulangan
            // "https\\://scele.cs.ui.ac.id/mod/assign/view\\.php\\?id=\\d+",

    };

    public void load(String file_pos) throws FileNotFoundException {
        File file_obj = new File(file_pos);
        Scanner file = new Scanner(file_obj);
        
        List<String> queue = new ArrayList<>(100);
        
        while (file.hasNextLine())
            queue.add(file.nextLine());
        
        regexp = new String[queue.size()];
        regexp = queue.toArray(regexp);
        file.close();
    }
    
    
    Pattern[] compiled = new Pattern[regexp.length];

    ScrapPattern(){
        try {
            load("data/scrap_pattern.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "data/scrap_pattern.txt.txt doesn't exist",
                                          "File not exists", JOptionPane.ERROR_MESSAGE);
            System.exit(130);  // random int
        }
        compileAll();
    }
    
    public void compileAll(){
        for (int i = 0; i < regexp.length; i++){
            compiled[i] = Pattern.compile(regexp[i]);
        }
    }
}
