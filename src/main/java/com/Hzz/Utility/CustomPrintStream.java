package com.Hzz.Utility;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;


public class CustomPrintStream extends PrintStream {
    public CustomPrintStream(OutputStream out)
        { super(out); }
    public CustomPrintStream(OutputStream out, boolean autoFlush)
        { super(out, autoFlush); }
    public CustomPrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException
        { super(out, autoFlush, encoding); }
    public CustomPrintStream(OutputStream out, boolean autoFlush, Charset charset)
         { super(out, autoFlush, charset); }
    public CustomPrintStream(String fileName) throws FileNotFoundException
        { super(fileName); }
    public CustomPrintStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException
        { super(fileName, csn); }
    public CustomPrintStream(String fileName, Charset charset) throws IOException
        { super(fileName, charset); }
    public CustomPrintStream(File file) throws FileNotFoundException
        { super(file); }
    public CustomPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException
        { super(file, csn); }
    public CustomPrintStream(File file, Charset charset) throws IOException
        { super(file, charset); }
    
        
        
    public void print(Object...objects){
        Iterator iter = Arrays.stream(objects).iterator();
        
        if (iter.hasNext())
            super.print(iter.next());
            
        while (iter.hasNext()){
            super.print(" ");
            super.print(iter.next());
        }
        this.onPrint(objects);
    }
    
    
    public void println(Object...objects){
        this.print(objects);
        super.println();
    }
    
    
    @Override
    public void print(boolean b) { super.print(b); this.onPrint(b); }
    @Override
    public void print(char c) { super.print(c); this.onPrint(c); }
    @Override
    public void print(int i) { super.print(i); this.onPrint(i); }
    @Override
    public void print(long l) { super.print(l); this.onPrint(l); }
    @Override
    public void print(float f) { super.print(f); this.onPrint(f); }
    @Override
    public void print(double d) { super.print(d); this.onPrint(d); }
    @Override
    public void print(char[] s) { super.print(s); this.onPrint(s); }
    @Override
    public void print(String s) { super.print(s); this.onPrint(s); }
    @Override
    public void print(Object obj) { super.print(obj); this.onPrint(obj); }
    @Override
    public void println() { super.println(); this.onPrint(); }
    @Override
    public void println(boolean x) { super.println(x); this.onPrint(x); }
    @Override
    public void println(char x) { super.println(x); this.onPrint(x); }
    @Override
    public void println(int x) { super.println(x); this.onPrint(x); }
    @Override
    public void println(long x) { super.println(x); this.onPrint(x); }
    @Override
    public void println(float x) { super.println(x); this.onPrint(x); }
    @Override
    public void println(double x) { super.println(x); this.onPrint(x); }
    @Override
    public void println(char[] x) { super.println(x); this.onPrint(x); }
    @Override
    public void println(String x) { super.println(x); this.onPrint(x); }
    @Override
    public void println(Object x) { super.println(x); this.onPrint(x); }
    @Override
    public PrintStream printf(String format, Object... args) { return super.printf(format, args); }
    @Override
    public PrintStream printf(Locale l, String format, Object... args) { return super.printf(l, format, args); }
    
    public static void setAsOutputStream(CustomPrintStream obj){
        System.setOut(obj);
    }
    public void onPrint(Object ... printed){}
    
    public static void main(String[] args) {
        CustomPrintStream my_stream = new CustomPrintStream(System.out);
        CustomPrintStream.setAsOutputStream(my_stream);
    
        my_stream.print("Aku", "adalah", "anak", "gembala");
    }
}
