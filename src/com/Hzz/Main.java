package com.Hzz;

import static java.lang.System.out;


import java.io.File;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.common.annotations.Beta;
import com.google.common.collect.Sets;
import org.w3c.dom.NamedNodeMap;


import net.sf.repr.Repr;
import org.w3c.dom.NodeList;

import javax.swing.*;
import java.io.IOException;
import java.util.function.BiFunction;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;


public class Main {
    private static WebScraper webClient;

    public static final String file_data_location = "scele data.pckl";
    public static final String difference_data_location = "scele data difference.pckl";
    private static HashMap<String, ScrapData> previous_data;
    private static HashMap<String, ScrapData> current_data;
    
    public static PrintStream console_out;

    //
    private static final String help_doc = """
            --load-previous
                to load previous session
            --update-scele
                to update the scele (default)
            --help
                to print this doc
                """;

    
    public static void main(String[] args) {
        console_out = System.out;
        CustomPrintStream out_stream = new CustomPrintStream(System.out);
        CustomPrintStream.setAsOutputStream(out_stream);
        
        if (args.length == 1){
            switch (args[0].toLowerCase()) {
                case "--update-scele" -> updateScele();
                case "--load-previous" -> loadPrevious();
                default -> out.println(help_doc);
            }
        }else if (args.length > 1){
            out.println(help_doc);
        }else{
            out.println(help_doc);
            updateScele();
        }
        
    }
    
    
    public static void updateScele(){
    
        /// set up console window
        GuiConsole console = new GuiConsole();
        console.activate();
        console.getMainFrame().setVisible(true);
        console.getMainFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        out.printf("Default directory: %s %n%n%n", System.getProperty("user.dir"));
    
        if (!load_previous_data()){
            out.println("load previous data failed");
            return;
        }
    
        current_data = new HashMap<>(100);
    
        webClient = WebScraper.defaultScrapper(null);
        webClient.setUrlQueue(UrlEntryPoint.getList());  // set the queue with a pre-filled url list
        login_scele();
    
        webClient.setHandlerFunc(new ScrapProcedure());
    
        try {
            webClient.startScrap();
        } catch (IOException e) {
            out.println("Error");
            e.printStackTrace();
        }
    
        // get difference data between old link and new link
        ArrayList<ScrapData> differences =  getDifference();
        try {
            Pickle.dump(Main.difference_data_location, differences);
            Pickle.dump(Main.file_data_location, current_data);  // update the data
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        out.println("logging out...");
        if (logout()) {
            out.println("logged out.");
        }else{
            out.println("log out failed");
        }
    
        out.println("Done");
    
        if (differences.size() > 0) {
            console.getMainFrame().setVisible(false);
            new GuiCheckbox(differences).getMainFrame()
                    .setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }else{
            JOptionPane.showMessageDialog(null, "Scele has no update",
                                          "No Update", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    
    
    @SuppressWarnings("unchecked")
    private static void loadPrevious(){
        GuiConsole console = new GuiConsole();
        console.activate();
        console.getMainFrame().setVisible(true);
        console.getMainFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        // load previous difference data between old link and new link
        ArrayList<ScrapData> differences;
        try {
            differences = (ArrayList<ScrapData>) Pickle.load(difference_data_location);
    
            console.getMainFrame().setVisible(false);
            new GuiCheckbox(differences).getMainFrame()
                    .setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (java.io.FileNotFoundException e){
            System.out.println("Previous data not found! ");
        }catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
    
    
    

    // returns true on successful operation, and false on error
    @SuppressWarnings("unchecked")
    private static boolean load_previous_data(){
        File f = new File(Main.file_data_location);
        if (! f.isFile()){  // file is either not exists or a directory
            HashMap<String, GroupedData> new_instance = new HashMap<>(100);
            try {
                Pickle.dump(Main.file_data_location, new_instance);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Main.previous_data = (HashMap<String, ScrapData>) Pickle.load(file_data_location);
            return true;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void login_scele(){
        String login_form_url = "https://scele.cs.ui.ac.id/login/index.php?authldap_skipntlmsso=1";
        String username, pss;  // needed to get access to scele and emas2
        username = "<username>"; pss = "<password>";
        
        

        String tmp1, tmp2;
        tmp1 = encodeValue(username);
        tmp2 = encodeValue(pss);
        String request_query = ("username=%s&password=%s").formatted(tmp1, tmp2);

        assert false;
        try {
            out.println("Logging in...");

            Page redirect_page = Requests.post(webClient, login_form_url, request_query);
            if (redirect_page.getWebResponse().getStatusCode() != 200)
                throw new RuntimeException("error status code when logging in");
            HtmlPage htmlPage = (HtmlPage) redirect_page;
            DomNodeList<DomNode> temp = htmlPage.querySelectorAll("div.profileblock > div#loggedin-user");
            if (temp.getLength() == 0)
                throw new RuntimeException("login failed, profile box not shown in html");

            out.println("Logged in.");
        } catch (Exception e) {
            out.println("login error");
            JOptionPane.showMessageDialog(null, "Couldn't login to scele.cs.ui.ac.id",
                                          "Login Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(130); // random value, idk what error code to assign
        }


        // under development
        // login_emas2(username, pss);
    }


    // returns true on success, false otherwise
    @Beta
    private static boolean login_emas2(String username, String pss){
        String login_form_url = "https://emas2.ui.ac.id/login/index.php";

        HtmlPage login_page;
        try {
            login_page = webClient.getPage(login_form_url);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        HtmlForm form = login_page.getHtmlElementById("login");
        if (form == null)  return false;
        HtmlTextInput username_field = form.getInputByName("username");
        HtmlTextInput pss_field = form.getInputByName("password");
        HtmlSubmitInput submit_btn = login_page.getHtmlElementById("loginbtn");

        try {
            username_field.type(username);
            pss_field.type(pss);
            submit_btn.click();
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
        
        return true;
    }


    // Method to encode a string value using `UTF-8` encoding scheme
    public static String encodeValue(String value) {
        // credits: https://www.urlencoder.io/java/
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
    
    
    private static boolean logout(){
        HtmlPage home_page;
        //noinspection TryWithIdenticalCatches
        try {
            home_page = webClient.getPage("https://scele.cs.ui.ac.id");
            HtmlElement temp = home_page.querySelector("a[data-title='logout,moodle']");
            String url = temp.getAttribute("href");

            //HtmlPage logout = webClient.getPage(url);
            Requests.post(webClient, url, "");
            // assert logout.getWebResponse().getStatusCode() == 200;

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        JOptionPane.showMessageDialog(null, "Couldn't logout from scele.cs.ui.ac.id",
                                      "Logout Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private static class ScrapProcedure implements BiFunction<WebScraper, Page, Collection<String>> {
        private final ScrapPattern regexp_url_list = new ScrapPattern();

        public static String getAttribute(DomNode node, String attributeName){
            NamedNodeMap temp1 = node.getAttributes();
            DomAttr temp2 = (DomAttr) temp1.getNamedItem(attributeName);
            return temp2.getValue();
        }

        public static boolean isStringMatches(String theInputString, Pattern theRegexPattern){
            Matcher matcher = theRegexPattern.matcher(theInputString);
            return matcher.matches();
        }


        @Override
        public Collection<String> apply(WebScraper self, Page result_) {

            String temp = result_.getWebResponse().getContentType();
            if (!temp.equals("text/html")) {
                out.println(Repr.repr(temp));
                return new ArrayList<>();
            }


            HtmlPage result = null;

            try{
                result = (HtmlPage) result_;
            }catch(ClassCastException e){
                e.printStackTrace();
                System.exit(130);  // its a random int error code, idk what code I should assign
            }


            Collection<String> ret = processNextUrlToBeScrapped(result);
            processCurrentPage(result);
            return ret;
        }



        public Collection<String> processNextUrlToBeScrapped(HtmlPage result) {
            out.printf("scrap %s \n", result.getUrl());

            ArrayList<String> urls = new ArrayList<>(250); // name meaning: multiple URL

            // note that this querySelectorAll is case-sensitive
            DomNodeList<DomNode> elements_with_href =
                    result.querySelectorAll("section#region-main *[href], div[role=main] *[href]"
                                                    + ", ul.dropdown-menu *[href]");

            for (DomNode obj: elements_with_href){
                String url = getAttribute(obj, "href");

                // if url matches any compiled_pattern, add it to urls
                for (var compiled_pattern : regexp_url_list.compiled) {
                    if (isStringMatches(url, compiled_pattern)){
                        urls.add(url);
                        break;
                    }
                }
            }
            

            return urls;
        }

        public void processCurrentPage(HtmlPage result){
            String page_title = result.getTitleText();
            String url = result.getUrl().toString();

            DomNode main_content_element = result.querySelector("section#region-main");
            String main_content = main_content_element.getTextContent();
            String hashed_content = Hash.sha512(main_content);

            ScrapData new_data = new ScrapData();
            new_data.page_title = page_title;
            new_data.url = url;
            new_data.page_content_hash = hashed_content;
            Map<String, String> content_map = new_data.getContentMap();
    
            // store content by id
            String selector = "li[id^='section']";
            if (main_content_element.querySelector(selector) != null) {  // element exist/matched
                DomNodeList<DomNode> node_list = main_content_element.querySelectorAll(selector);
                for (Object obj_: node_list){
                    assert obj_ instanceof HtmlElement;
                    HtmlElement obj = (HtmlElement) obj_;
                    content_map.put(obj.getAttribute("id"), obj.getTextContent());
                }
            }
            
            current_data.put(url, new_data);
        }

    }

    private static ArrayList<ScrapData> getDifference(){
        ArrayList<ScrapData> differences = new ArrayList<>(100);

        Set<String> keys_previous_data = previous_data.keySet();
        Set<String> keys_current_data = current_data.keySet();

        Set<String> new_URLs = Sets.difference(keys_current_data, keys_previous_data);
        Set<String> deleted_URLs = Sets.difference(keys_previous_data, keys_current_data);
        Set<String> still_exist_URLs = Sets.intersection(keys_previous_data, keys_current_data);
    
        // https://scele.cs.ui.ac.id/course/view.php?id=3066
        
        for (String key: still_exist_URLs){
            ScrapData prev = previous_data.get(key);
            String prev_hash = prev.page_content_hash;
            ScrapData curr = current_data.get(key);
            String curr_hash = curr.page_content_hash;
    
            // compare in-depth information: element-id level precision
            Map<String, Integer> map_comparation = MapComparator.compareMap(prev.getContentMap(),
                                                                            curr.getContentMap());
            
            if (! prev_hash.equals(curr_hash)){
                ScrapData tmp = new ScrapData(prev, curr, "mod");  // mod = modified
                Map<String, String> tmp_map = tmp.getContentMap();
                
                for (var element_id: map_comparation.keySet())
                    if (map_comparation.get(element_id) == MapComparator.DIFFERENT
                            || map_comparation.get(element_id) == MapComparator.ONLY_RIGHT_MAP)
                        tmp_map.put(element_id, "new/modified");
                    
                differences.add(tmp);
            }
        }

        
        for (String key: new_URLs){
            ScrapData prev = new ScrapData();
            ScrapData curr = current_data.get(key);
            ScrapData difference = new ScrapData(prev, curr, "new");
            differences.add(difference);
    
            Map<String, String> tmp_map = difference.getContentMap();
            for (var element_id: curr.getContentMap().keySet())
                tmp_map.put(element_id, "new/modified");
        }

        for (String key: deleted_URLs){
            ScrapData prev = previous_data.get(key);
            ScrapData curr = new ScrapData();
            ScrapData difference = new ScrapData(prev, curr, "del");
            differences.add(difference);
        }

        return differences;
    }
    
    
}
