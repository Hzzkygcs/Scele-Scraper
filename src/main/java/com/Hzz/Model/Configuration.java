package com.Hzz.Model;

import com.google.gson.Gson;

import java.io.*;

import static com.Hzz.Utility.Constants.CONSTANT_JSON_FILE_PATH;

public class Configuration {  // Singleton
    public boolean is_multithreading() {return multithreading;}
    public boolean is_show_deleted_object() {
        return show_deleted_object;
    }
    
    private boolean multithreading = false;
    private boolean show_deleted_object = true;
    
    
    private Configuration(){}
    private Configuration(boolean multithreading, boolean show_deleted_object){
        this.multithreading = multithreading;
        this.show_deleted_object = show_deleted_object;
    }
    
    
    private static Configuration existing_object;
    public static Configuration create(){
        if (existing_object == null){
            Gson gson = new Gson();
            try(Reader reader = new FileReader(CONSTANT_JSON_FILE_PATH)) {
                existing_object = gson.fromJson(reader, Configuration.class);
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + CONSTANT_JSON_FILE_PATH);
                e.printStackTrace();
                throw new IllegalStateException("File not found: " + CONSTANT_JSON_FILE_PATH);
            } catch (IOException e) {
                System.out.println("IO exception when loading: " + CONSTANT_JSON_FILE_PATH);
                e.printStackTrace();
                throw new IllegalStateException("IO exception when loading: " + CONSTANT_JSON_FILE_PATH);
            }
        }
        return existing_object;
    }
    
}
