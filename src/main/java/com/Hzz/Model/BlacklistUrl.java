package com.Hzz.Model;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.Hzz.Utility.Constants.BLACKLIST_TXT_FILE_PATH;

public class BlacklistUrl {
    String file_path;
    HashSet<String> blacklisted_urls = new HashSet<>(107);
    
    /**
     * Returns uncopied mutable set. Use with caution!
     */
    public Set<String> __get_set(){
        return blacklisted_urls;
    }
    public boolean contains(String o) {
        return blacklisted_urls.contains(o);
    }
    public boolean add(String url) throws IOException {
        return add(url, null);
    }
    
    @Deprecated  // comment feature will drastically make the code unreadable
    public boolean add(String url, String comment) throws IOException {
        var writer = new BufferedWriter(new FileWriter(file_path, true));
        writer.write(url);
        /*if (comment != null){
            writer.write("  >>  ");
            writer.write(comment);
        }*/
        
        writer.write("\n");
        writer.close();
        
        return blacklisted_urls.add(url);
    }
    public boolean remove(String o) throws IOException {
        var ret = blacklisted_urls.remove(o);
    
        var writer = new BufferedWriter(new FileWriter(file_path, false));
        var iter = blacklisted_urls.iterator();
        while (iter.hasNext()){
            var temp = iter.next();
            writer.write(temp);
            writer.write("\n");
        }
        writer.close();
        
        return ret;
    }
    
    private BlacklistUrl(String file_path) throws IOException {
        this.file_path = file_path;
        String file_contents;
        List<String> file_lines = Files.readAllLines(Paths.get(file_path), StandardCharsets.UTF_8);
        
        for (var line : file_lines) {
            // character ">>" without quotes is single line comments for blacklist.txt
            // we want to remove them.
            // Update: comment feature may not be implemented as it will drastically increase program complexity
            
            String[] splitted = line.split("\\>\\>");
            var without_comment = splitted[0].trim();
            if ( ! without_comment.isEmpty())
                blacklisted_urls.add(without_comment);
        }
    }
    
    private static BlacklistUrl existing_obj;
    
    public static @NonNull BlacklistUrl create(){
        if (existing_obj == null){
            try {
                existing_obj = new BlacklistUrl(BLACKLIST_TXT_FILE_PATH);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                                              "Unable to access file: " + BLACKLIST_TXT_FILE_PATH,
                                              "ERROR", JOptionPane.INFORMATION_MESSAGE);
                e.printStackTrace();
                throw new IllegalStateException("");
            }
        }
        return existing_obj;
    }
    
    
}
