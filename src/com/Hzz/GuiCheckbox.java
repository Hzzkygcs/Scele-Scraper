package com.Hzz;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.awt.Desktop;
import java.util.List;



class CheckboxAndData{
    public JCheckBox checkbox;
    public String url;
    public String page_title;
    public String msg;

    public JCheckBox getCheckbox() { return checkbox; }
    public void setCheckbox(JCheckBox checkbox) { this.checkbox = checkbox; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPageTitle() { return page_title; }
    public void setPageTitle(String page_title) { this.page_title = page_title; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    CheckboxAndData (){}
    CheckboxAndData (JCheckBox checkbox, String url, String page_title, String msg){
        this.checkbox = checkbox;
        this.url = url;
        this.page_title = page_title;
        this.msg = msg;
    }

    CheckboxAndData (JCheckBox checkbox, ScrapData data){
        this.checkbox = checkbox;
        this.url = data.getUrl();
        this.page_title = data.getPageTitle();
        this.msg = data.getMsg();
    }

    CheckboxAndData (JCheckBox checkbox, DataDifference data){
        this.checkbox = checkbox;
        this.url = data.getUrl();
        this.page_title = data.getPageTitle();
        this.msg = data.difference_note;
    }
}


interface ICloneableList<T> extends Cloneable, List<T>{}

public class GuiCheckbox {

    /*  ------------------------- Instance Variables -------------------------  */

    private Collection<ScrapData> data_to_be_shown;
    private JFrame main_frame;

    private JPanel checkbox_panel;
    private JScrollPane checkbox_scrollpane;

    // I declare this so that I can flexibly change the List type without have to do much work, and so that
    // I can use BOTH Cloneable and List interface in the same variable by using ICloneableList
    class MyList<T>  extends ArrayList<T> implements ICloneableList<T>{
    }
    private MyList<CheckboxAndData> checkbox_and_data = new MyList<>();
    private static GridBagConstraints default_checkbox_gridbagconst;
    private final static int default_checkbox_max_x = 2;  // max number of elements in a row

    // max char in a checkbox, if exceeded, it will take extra grid space
    private final static int default_checkbox_max_char = 40;

    private JPanel main_btn_panel;
    private JPanel top_btn_panel;
    private JPanel bottom_btn_panel;




    /*  ------------------------- Setters and getters -------------------------  */

    public JFrame getMainFrame(){ return this.main_frame;}


    /*  -------------------------     Constructor     -------------------------  */


    GuiCheckbox(Collection<ScrapData> data_to_be_shown) {
        default_checkbox_gridbagconst = new GridBagConstraints();
        default_checkbox_gridbagconst.anchor = GridBagConstraints.WEST;
        default_checkbox_gridbagconst.ipadx = 10;
        default_checkbox_gridbagconst.ipady = 4;
        default_checkbox_gridbagconst.weightx = 1;

//        this.data_to_be_shown = data_to_be_shown;

        data_to_be_shown.forEach(
                x -> checkbox_and_data.add(new CheckboxAndData(null, x))
        );


        main_frame = new JFrame();
        main_frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        main_frame.setVisible(true);
        main_frame.setSize(500, 500);
        main_frame.setLayout(new GridBagLayout());
        buildMainFrame();

    }

    private void buildMainFrame(){
        GridBagConstraints c = new GridBagConstraints();

        buildCheckboxPanel();
        buildCheckboxes(checkbox_panel);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;  c.gridy = 0;
        main_frame.add(checkbox_scrollpane, c);

        buildButtonPanel();
        buildButtons(top_btn_panel, bottom_btn_panel);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;  c.gridy = 1;
        main_frame.add(main_btn_panel, c);

        main_frame.revalidate();  // update frame
        main_frame.repaint();  // update frame
    }


    private void buildCheckboxPanel() {
        JPanel checkbox_panel = new JPanel(new GridBagLayout());
        checkbox_panel.setMinimumSize(new Dimension(500, 500));
        checkbox_panel.setAutoscrolls(true);
        JScrollPane scroll_frame = new JScrollPane(checkbox_panel);
        scroll_frame.getVerticalScrollBar().setUnitIncrement(35);  // scroll speed

        this.checkbox_panel = checkbox_panel;
        this.checkbox_scrollpane = scroll_frame;
    }

    private <T> void buildCheckboxes(JPanel checkbox_panel){
        int counter = 0;
        int x_pos = 0;
        int y_pos = 0;
        Iterator<CheckboxAndData> iterator = checkbox_and_data.iterator();
        GridBagConstraints checkbox_gridbagconst = (GridBagConstraints) default_checkbox_gridbagconst.clone();

        while (iterator.hasNext()){
            CheckboxAndData iter_item = iterator.next();
            String msg = iter_item.getMsg();
            String url = iter_item.getUrl();
            String page_title = iter_item.getPageTitle();

            String label = "%s (%s)".formatted(page_title, msg);
            JCheckBox checkbox = new JCheckBox(label);
            checkbox_gridbagconst.gridx = x_pos;
            checkbox_gridbagconst.gridy = y_pos;

            if (label.length() > default_checkbox_max_char   &&   x_pos+1 < default_checkbox_max_x){
                checkbox_gridbagconst.gridwidth = 2;
                x_pos += 1;
            }else{
                checkbox_gridbagconst.gridwidth = 1;
            }

            x_pos += 1;

            if (x_pos >= default_checkbox_max_x){
                x_pos = 0;
                y_pos += 1;
            }

            iter_item.checkbox = checkbox;
            checkbox_panel.add(checkbox, checkbox_gridbagconst);

            counter += 1;
        }
    }


    private void buildButtonPanel() {
        main_btn_panel = new JPanel();
        main_btn_panel.setLayout(new BoxLayout(main_btn_panel, BoxLayout.Y_AXIS));
        top_btn_panel = new JPanel(new GridLayout(1, 3));
        bottom_btn_panel = new JPanel(new GridLayout(1, 4));

        main_btn_panel.add(top_btn_panel);
        main_btn_panel.add(bottom_btn_panel);
    }

    private void buildButtons(JPanel top_btn_panel, JPanel bottom_btn_panel){
        JButton open_btn = new JButton("Open");
        open_btn.addActionListener(new OpenBtnEventListener());

        JButton open_remove_btn = new JButton("Open & Remove");
        open_remove_btn.addActionListener(new OpenRemoveBtnEventListener());

        JButton open_exit_btn = new JButton("Open & Exit");
        open_exit_btn.addActionListener(new OpenExitBtnEventListener());

        JButton check_all_btn = new JButton("Check all");
        check_all_btn.addActionListener(new CheckAllBtnEventListener());

        JButton uncheck_all_btn = new JButton("Uncheck all");
        uncheck_all_btn.addActionListener(new UncheckAllBtnEventListener());

        JButton toggle_check_btn = new JButton("toggle check");
        toggle_check_btn.addActionListener(new ToggleCheckBtnEventListener());

        JButton check_plus_10 = new JButton("check +10");
        check_plus_10.addActionListener(new CheckPlusTenBtnEventListener());


        top_btn_panel.add(open_btn);
        top_btn_panel.add(open_remove_btn);
        top_btn_panel.add(open_exit_btn);

        bottom_btn_panel.add(check_all_btn);
        bottom_btn_panel.add(uncheck_all_btn);
        bottom_btn_panel.add(toggle_check_btn);
        bottom_btn_panel.add(check_plus_10);
    }

    /*  ------------------------- Static Methods -------------------------  */

    // open a URI in computer's default browser
    public static void openUriInBrowser(String uri_) throws URISyntaxException, IOException {
        URI uri = new URI(uri_);

        if (Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(uri);
        }
    }



    /*  ------------------------- Event Listener -------------------------  */

    class OpenBtnEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            for (CheckboxAndData obj: checkbox_and_data)
                if (obj.checkbox.isSelected())
                    try {
                        openUriInBrowser(obj.url);
                    } catch (URISyntaxException | IOException uriSyntaxException) {
                        uriSyntaxException.printStackTrace();
                    }
        }
    }


    class OpenRemoveBtnEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e){

            // remove all of the checked checkbox by creating new list
            // and open it in default browser
            MyList<CheckboxAndData> filtered_checkbox = new MyList<>();

            for (CheckboxAndData obj: checkbox_and_data) {
                if (! obj.checkbox.isSelected()) {
                    filtered_checkbox.add(obj);
                }else{
                    try {
                        openUriInBrowser(obj.url);
                    } catch (URISyntaxException | IOException uriSyntaxException) {
                        uriSyntaxException.printStackTrace();
                        break;
                    }
                }
            }


            checkbox_and_data = filtered_checkbox;  // update the list with the new one
            main_frame.getContentPane().removeAll();
            buildMainFrame();
        }
    }

    class OpenExitBtnEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            for (CheckboxAndData obj: checkbox_and_data)
                if (obj.checkbox.isSelected())
                    try {
                        openUriInBrowser(obj.url);
                    } catch (URISyntaxException | IOException uriSyntaxException) {
                        uriSyntaxException.printStackTrace();
                    }
            main_frame.dispatchEvent(new WindowEvent(main_frame, WindowEvent.WINDOW_CLOSING));
        }
    }

    class CheckAllBtnEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            for (CheckboxAndData obj: checkbox_and_data)
                obj.checkbox.setSelected(true);
        }
    }

    class UncheckAllBtnEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            for (CheckboxAndData obj: checkbox_and_data)
                obj.checkbox.setSelected(false);
        }
    }

    class ToggleCheckBtnEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            for (CheckboxAndData obj: checkbox_and_data)
                obj.checkbox.setSelected(! obj.checkbox.isSelected());
        }
    }

    class CheckPlusTenBtnEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            Iterator<CheckboxAndData> iterator = checkbox_and_data.iterator();
            int count = 10;
            while (iterator.hasNext() && count > 0){
                CheckboxAndData obj = iterator.next();
                if (! obj.checkbox.isSelected()) {
                    obj.checkbox.setSelected(true);
                    count -= 1;
                }
            }
        }
    }



    public static void main(String[] args) {
        ArrayList<ScrapData> data;
        try {
            data = (ArrayList<ScrapData>) Pickle.load(Main.difference_data_location);
            GuiCheckbox test_app = new GuiCheckbox(data);

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }


    }

}
