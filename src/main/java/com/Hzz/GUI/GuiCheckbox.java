package com.Hzz.GUI;



import com.Hzz.Main;
import com.Hzz.Model.BlacklistUrl;
import com.Hzz.Model.Configuration;
import com.Hzz.Utility.Pickle;
import com.Hzz.Model.ScrapData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.awt.Desktop;
import java.util.List;

import static com.Hzz.Utility.Constants.*;


class CheckboxAndData extends ScrapData{
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
        this.content_by_id = data.getContentMap();
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
    static class MyList<T>  extends ArrayList<T> implements ICloneableList<T>{
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
    public JFrame setMainFrame(JFrame x){ return this.main_frame = x;}


    /*  -------------------------     Constructor     -------------------------  */


    public GuiCheckbox(Collection<ScrapData> data_to_be_shown) {
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
        main_frame.setTitle("Scele-scrapper");
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
    
        Configuration conf = Configuration.create();
        
        while (iterator.hasNext()){
            CheckboxAndData iter_item = iterator.next();
            String msg = iter_item.getMsg();
            String url = iter_item.getUrl();
            String page_title = iter_item.getPageTitle();
            Map<String, String> id_of_changed_elements = iter_item.getContentMap();

            if (!conf.is_show_deleted_object() && msg.equals(DELETED_URL_SCRAP_MSG))
                continue;
            
            String label = "%s (%s)".formatted(page_title, msg);
            JCheckBox checkbox = new JCheckBox(label);
            checkbox_gridbagconst.gridx = 1;
            checkbox_gridbagconst.gridy = y_pos;
            checkbox.addMouseListener(new CheckboxEventListener(checkbox, url));
            
            iter_item.checkbox = checkbox;
            checkbox_panel.add(checkbox, checkbox_gridbagconst);
            
            JPanel element_id_label_container = new JPanel(new FlowLayout());
            checkbox_gridbagconst.gridx = 2;
            checkbox_gridbagconst.gridy = y_pos;
            checkbox_panel.add(element_id_label_container, checkbox_gridbagconst);
            
            for (String element_id: id_of_changed_elements.keySet()){
                String url_with_id = "%s#%s".formatted(url, element_id);
                
                JLabel element_id_label = new JLabel("#" + element_id + " ");
                addUnderline(element_id_label);
                element_id_label.setForeground(Color.blue);
                element_id_label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                element_id_label.addMouseListener(new HyperlinkLabelListener(url_with_id));
                
                element_id_label_container.add(element_id_label);
            }

            counter += 1;
            y_pos += 1;
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
    
        JButton open_all_id_btn = new JButton("Open all id");
        open_all_id_btn.addActionListener(new OpenAllIdBtnEventListener());

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
        top_btn_panel.add(open_all_id_btn);
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

    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addUnderline(JComponent component){
        Font component_font = component.getFont();
        Map attributes = component_font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        component.setFont(component_font.deriveFont(attributes));
    }

    /*  ------------------------- Event Listener -------------------------  */

    class CheckboxEventListener implements MouseListener{
        private boolean added_to_blacklist = false;
        private JCheckBox associated_checkbox;
        private String associated_scele_url;
        private String checkbox_pure_text;
        
        
        public CheckboxEventListener(JCheckBox associated_checkbox, String associated_scele_url){
            this.associated_checkbox = associated_checkbox;
            this.associated_scele_url = associated_scele_url;
        }
    
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && SwingUtilities.isRightMouseButton(e)){
                try {
                    if (!added_to_blacklist) {
                            BlacklistUrl.create().add(associated_scele_url);
                            added_to_blacklist = true;
                            associated_checkbox.setEnabled(false);
                    }else{
                        BlacklistUrl.create().remove(associated_scele_url);
                        added_to_blacklist = false;
                        associated_checkbox.setEnabled(true);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                                                  "Unable to access file: " + BLACKLIST_TXT_FILE_PATH,
                                                  "ERROR", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }
    
    class OpenBtnEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            for (CheckboxAndData obj: checkbox_and_data)
                if (obj.checkbox.isSelected())
                    try {
                        // get first id
                        String id = obj.getContentMap().isEmpty()? "":obj.getContentMap().firstKey();
                        openUriInBrowser("%s#%s".formatted(obj.url, id));
                    } catch (URISyntaxException | IOException uriSyntaxException) {
                        uriSyntaxException.printStackTrace();
                    }
        }
    }
    
    class OpenAllIdBtnEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            try {
                for (CheckboxAndData obj: checkbox_and_data)
                    if (obj.checkbox.isSelected()) {
                        for (String element_id : obj.content_by_id.keySet()) {
                            String url_with_id = "%s#%s".formatted(obj.url, element_id);
                            openUriInBrowser(url_with_id);
                        }
                        
                        if (obj.content_by_id.isEmpty()) openUriInBrowser(obj.url);
                    }
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
                        String id = obj.getContentMap().isEmpty()? "":obj.getContentMap().firstKey();
                        openUriInBrowser("%s#%s".formatted(obj.url, id));
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
                        String id = obj.getContentMap().isEmpty()? "":obj.getContentMap().firstKey();
                        openUriInBrowser("%s#%s".formatted(obj.url, id));
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
    
    static class HyperlinkLabelListener implements MouseListener {
        String hyperlink_target_url;
        
        public HyperlinkLabelListener(String url){
            super();
            hyperlink_target_url = url;
        }
    
        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                openUriInBrowser(hyperlink_target_url);
            } catch (URISyntaxException | IOException uriSyntaxException) {
                uriSyntaxException.printStackTrace();
            }
        }
    
        @Override
        public void mousePressed(MouseEvent e) {
        }
    
        @Override
        public void mouseReleased(MouseEvent e) {
        }
    
        @Override
        public void mouseEntered(MouseEvent e) {
        }
    
        @Override
        public void mouseExited(MouseEvent e) {
        }
    }


    public static void main(String[] args) {
        ArrayList<ScrapData> data;
        try {
            //noinspection unchecked
            data = (ArrayList<ScrapData>) Pickle.load(Main.difference_data_location);
            GuiCheckbox test_app = new GuiCheckbox(data);

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }


    }

}
