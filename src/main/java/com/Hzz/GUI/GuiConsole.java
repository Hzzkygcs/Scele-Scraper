package com.Hzz.GUI;


import com.Hzz.Utility.CustomPrintStream;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;


public class GuiConsole {
    private PrintStream old_print_stream;
    private ByteArrayOutputStream this_print_stream_bytearray;
    private PrintStream this_print_stream;
    private boolean active;

    private JFrame main_frame;
    private JPanel text_panel;
    private JScrollPane text_area_scroll;
    private JTextArea text_area;
    private JPanel button_panel;

    /*   ----------------------------    Methods   ----------------------------   */

    public static void main(String[] args) {
        GuiConsole console = new GuiConsole();
        console.activate();
        console.getMainFrame().setVisible(true);

        System.out.println("Hello zz");
        System.out.println("World zz");
        
        while (true){
            for (int i=0; i<10; i++){
                System.out.println("World zz");
            }
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //console.updateConsole();
        }
        // console.updateConsole();
    }


    public GuiConsole() {
        backupOldStream();
        this_print_stream_bytearray = new ByteArrayOutputStream();
        this_print_stream = new CustomPrintStream(this_print_stream_bytearray,
                                                  true){
            @Override
            public void flush(){
                super.flush();
                GuiConsole.this.updateConsole();
            }
            
            @Override
            public void onPrint(Object ... printed){
                this.flush();
            }
        };
        
        old_print_stream = this_print_stream;

        main_frame = new JFrame();
        main_frame.setTitle("Scele-scrapper");
        main_frame.setLayout(new BorderLayout());
        main_frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        main_frame.setSize(500, 500);


        buildTextPart();
        buildButtonPart();

        main_frame.revalidate();
        main_frame.repaint();

    }

    public void buildTextPart() {
        text_panel = new JPanel();
        text_panel.setMinimumSize(new Dimension(500, 500));
        text_panel.setLayout(new BoxLayout(text_panel, BoxLayout.PAGE_AXIS));

        Border border = new EtchedBorder(EtchedBorder.RAISED, new Color(50,50,50),
                new Color(0,0,0));
        Border margin = new EmptyBorder(10, 10, 10, 10);
        text_panel.setBorder(BorderFactory.createCompoundBorder(margin, border));

        text_area = new JTextArea("");
        text_area.setWrapStyleWord(true);
        text_area.setEditable(false);
        text_area.setBackground(new Color(200, 200, 200));
        text_area.setFont(new Font("courier new", Font.BOLD, 15));
        text_area.setMargin(new Insets(10, 10, 10, 10));  // text area padding

        text_area_scroll = new JScrollPane(text_area);
        text_area_scroll.getVerticalScrollBar().setUnitIncrement(35);  // scroll speed

        text_panel.add(text_area_scroll);
        
        main_frame.add(text_panel, BorderLayout.CENTER);
    }


    public void buildButtonPart(){
        button_panel = new JPanel(new GridLayout(1, 5, 10, 10));
        // button_panel.setBackground(new Color(255, 0, 0));

        JButton clear_btn = new JButton("clear");
        clear_btn.addActionListener(e -> {text_area.setText("");});
        clear_btn.setBorder(new EmptyBorder(5, 5, 5, 5));
        clear_btn.setBackground(new Color(196, 219, 255));
        
        button_panel.add(clear_btn);
        button_panel.add(new JPanel());  // blank grid cell
        button_panel.add(new JPanel());  // blank grid cell
        button_panel.add(new JPanel());  // blank grid cell
        button_panel.add(new JPanel());  // blank grid cell

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;  c.gridy = 1;
        main_frame.add(button_panel, BorderLayout.SOUTH);

    }


    public void activate(){
        if (!active) {
            toggleStream();
            active = true;
        }
    }

    public void deactivate(){
        if (active) {
            toggleStream();
            active = false;
        }
    }

    private void backupOldStream(){
        old_print_stream = System.out;
    }

    private void toggleStream(){
        PrintStream tmp = System.out;
        System.setOut(this.old_print_stream);
        this.old_print_stream = tmp;
    }
    
    
    public void updateConsole(){
        String text = this_print_stream_bytearray.toString();
        this_print_stream_bytearray.reset();
        text_area.append(text);
    }

    private String getString(){ return this_print_stream_bytearray.toString(); }
    private void clearString(){ this_print_stream_bytearray.reset(); }


    /*   ----------------------------  Setters and Getters ----------------------------   */
    public PrintStream getOldPrintStream() { return old_print_stream; }
    public void setOldPrintStream(PrintStream old_print_stream) { this.old_print_stream = old_print_stream; }

    public PrintStream getCurrentPrintStreamBytearray() { return this_print_stream; }
    public void setCurrentPrintStreamBytearray(ByteArrayOutputStream this_print_stream) {
        this.this_print_stream_bytearray = this_print_stream;
    }

    public PrintStream getThisPrintStream() { return this_print_stream; }
    public void setThisPrintStream(PrintStream this_print_stream) { this.this_print_stream = this_print_stream; }

    public boolean isActive(){ return this.active; }

    public JFrame getMainFrame(){ return this.main_frame; }
}
