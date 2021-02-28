package com.Hzz;

import com.gargoylesoftware.htmlunit.Page;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;


public class WebScraper extends WebClient {
    /* -------------------------- Static Constants -------------------------- */

    static final int BFS = 0;  // breadth-first search
    static final int DFS = 1;  // depth-first search


    /*  ------------------------- Instance Variables -------------------------  */

    private boolean is_running = false;
    private boolean ready = false;
    private String URL_entry_point = null;
    private Deque<String> URL_to_be_scrapped;
    // private Queue<HtmlPage> results;
    private Set<String> added_or_scrapped_URLs;
    private Object tied_data = null;  // optional
    private int scrap_type = -1;  // 0 -> BFS, 1 -> DFS

    /**
     * Every url scrapped by WebScraper will be passed to this method, then the method will give a list of String,
     * containing all the new URL that should be added to the queue. If there is a URL has been crawled and/or added to
     * the queue, it will not be re-added to the url queue even if they are given by this method.
     * If you need to pass any information to this function, you don't have to use extra parameter. Just send
     * the information using .setTiedData(), and inside the function, retrieve it by using self.getTiedData()
     * where the self is the first parameter of the function. Multiple data? send linkedList or the like.
     * User should implements this method.
     * @param html_page
     * @return
     */
    private BiFunction<WebScraper, Page, Collection<String>> handler = null;  // implement this


    /*  ------------------------- Setters and getters -------------------------  */

    public boolean isRunning(){
        return this.is_running;
    }

    public void setUrlQueue(Deque<String> queue){ this.URL_to_be_scrapped = queue; }
    public Deque<String> getUrlQueue(){ return this.URL_to_be_scrapped; }

    public void setUrlSet(Set<String> added_or_scrapped_URLs){this.added_or_scrapped_URLs = added_or_scrapped_URLs;}


    public void setTiedData(Object anything){this.tied_data = anything;}
    public Object getTiedData(){return this.tied_data;}

    public void setScrapType(int type){
        if (type != BFS && type != DFS)
            throw new IllegalArgumentException("Type must be either .BFS or .DFS constant");
        this.scrap_type = type;
    }
    public int getScrapType(){ return this.scrap_type; }

    public void setHandlerFunc(BiFunction<WebScraper, Page, Collection<String>> handler){
        this.handler = handler;
    }


    /*  -------------------------  Methods -------------------------  */

    WebScraper(String URL_entry_point){
        super();
        this.URL_entry_point = URL_entry_point;
    }


    /**
     * Define a new object of WebScraper without having to implements all the required structure. Instantiating
     * a new object from this static method will make you sure you won't get any error from .check() method and
     * also help you reduce your code length.
     * @param URL_entry_point
     * @return
     */
    public static WebScraper defaultScrapper(String URL_entry_point){
        WebScraper ret = new WebScraper(URL_entry_point);
        ret.setUrlQueue(new LinkedList<>());
        ret.setUrlSet(new HashSet<>());
        ret.setScrapType(WebScraper.DFS);
        ret.setHandlerFunc(
                (WebScraper self, Page ReceivedPage) -> {return new LinkedList<>(); }
                );

        // these are my default settings for HtmlUnit
        ret.getOptions().setUseInsecureSSL(true);
        ret.getOptions().setCssEnabled(false);
        ret.getOptions().setJavaScriptEnabled(false);

        assert ret.isReady();
        return ret;
    }


    /**
     * Same as check(), but it will return a boolean instead of throwing errors.
     * It will also call check() in order to update the state (ready/not-ready state).
     * @return
     */
    public boolean isReady(){
        if (this.ready) return true;

        try{
            this.check();
            return true;
        }catch (Exception e){ return false; }
    }


    /**
     * check if this class is ready to be run (ready to be used). If not yet ready, it will throws exception.
     * if ready, it will be fine (nothing happens). This method will also update the not-ready state into
     * ready state if eligible.
     * @throws IllegalStateException
     */
    public void check() throws IllegalStateException{
        if (this.ready) return;

        if (this.URL_to_be_scrapped == null)
            throw new IllegalStateException("UrlQueue is not implemented. Please call `.setUrlQueue()`");

        if (this.added_or_scrapped_URLs == null)
            throw new IllegalStateException("Scrapped-Url set is not implemented. Please call `.setUrlSet()`");

        if (this.scrap_type == -1)
            throw new IllegalStateException("Scrap type is not defined. Please call `.setScrapType(.BFS)` or `.setScrapType(.DFS)`");

        if (this.handler == null)
            throw  new IllegalStateException("handler function  not defined. Please call `.setHandlerFunc()`");

        this.ready = true;  // the state is now ready
    }


    /**
     * An outside command to start the scrapping process. The `.isReady()` method should yields true to run this process
     * @return
     */
    public void startScrap() throws IOException {
        if (!this.isReady()) throw new IllegalStateException("The object is not ready yet. Please check the problem "
                + "with `.check()`");

        if (this.URL_entry_point != null)
            this.URL_to_be_scrapped.add(this.URL_entry_point);
        this.is_running = true;

        while (this.URL_to_be_scrapped.size() > 0)
            this.scrap();

        this.is_running = false;
    }

    private void scrap() throws IOException {
        String processed_url = this.URL_to_be_scrapped.remove();

        // we didn't assign .getPage() directly to HtmlPage type because if there is some error such as unclosed
        // tags. it will be error if we cast it into HtmlPage
        Page page = this.getPage(processed_url);


        Collection<String> returned = this.handler.apply(this, page);

        // out.printf("Returned : %s %n", returned);

        for (String i : returned) {
            if (!added_or_scrapped_URLs.contains(i)) {
                if (this.scrap_type == WebScraper.BFS) {
                    this.URL_to_be_scrapped.addLast(i);
                } else if (this.scrap_type == WebScraper.DFS) {
                    this.URL_to_be_scrapped.addFirst(i);
                } else {
                    // it should be impossible to happen. There's bug
                    throw new RuntimeException("Bug error: scrap type is neither BFS nor DFS.");
                }

                added_or_scrapped_URLs.add(i);
            }
        }


    }





}
