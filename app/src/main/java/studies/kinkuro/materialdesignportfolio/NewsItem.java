package studies.kinkuro.materialdesignportfolio;

/**
 * Created by alfo6-2 on 2018-03-20.
 */

public class NewsItem {

    private String title, category, author, desc, pubDate, link;
    int imgNews;

    public NewsItem() {

    }

    public NewsItem(String title, String category, String author, String desc, String pubDate, String link, int imgNews) {
        this.title = title;
        this.category = category;
        this.author = author;
        this.desc = desc;
        this.pubDate = pubDate;
        this.link = link;
        this.imgNews = imgNews;
    }

    public String getTitle() {        return title;    }
    public void setTitle(String title) {        this.title = title;    }

    public String getCategory() {        return category;    }
    public void setCategory(String category) {        this.category = category;    }

    public String getAuthor() {        return author;    }
    public void setAuthor(String author) {        this.author = author;    }

    public String getDesc() {        return desc;    }
    public void setDesc(String desc) {        this.desc = desc;    }

    public String getPubDate() {        return pubDate;    }
    public void setPubDate(String pubDate) {        this.pubDate = pubDate;    }

    public String getLink() {        return link;    }
    public void setLink(String link) {        this.link = link;    }

    public int getImgNews() {        return imgNews;    }
    public void setImgNews(int imgNews) {        this.imgNews = imgNews;    }

}
