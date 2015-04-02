package model;

import java.util.Date;

/**
 * Created by Bin on 2015/3/11.
 */
public class CrawlerData {
    private long id;
    private String title;
    private String extra_imfo;
    private String content_html;
    private String content_text;
    private String comment;
    private String memo;
    private Date oper_time;
    private String source_url;
    private String keywords;

    public CrawlerData(){
        id = 0;
        title = "";
        extra_imfo = "";
        content_html = "";
        content_text = "";
        comment = "";
        memo = "";
        oper_time = new Date();
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getExtra_imfo() {
        return extra_imfo;
    }
    public void setExtra_imfo(String extra_imfo) {
        this.extra_imfo = extra_imfo;
    }
    public String getContent_html() {
        return content_html;
    }
    public void setContent_html(String content_html) {
        this.content_html = content_html;
    }
    public String getContent_text() {
        return content_text;
    }
    public void setContent_text(String content_text) {
        this.content_text = content_text;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getMemo() {
        return memo;
    }
    public void setMemo(String memo) {
        this.memo = memo;
    }
    public Date getOper_time() {
        return oper_time;
    }
    public void setOper_time(Date oper_time) {
        this.oper_time = oper_time;
    }
    public String getSource_url() {
        return source_url;
    }
    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }
    public String getKeywords() {
        return keywords;
    }
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
