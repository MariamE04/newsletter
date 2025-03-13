package app.entities;

import java.util.Date;

public class Newsletters {
    private int id;
    private String title;
    private String filename;
    private String teasertext;
    private String thumbnail_name;
    private Date published;

    public Newsletters(int id, String title, String filename, String teasertext, String thumbnail_name, Date published) {
        this.id = id;
        this.title = title;
        this.filename = filename;
        this.teasertext = teasertext;
        this.thumbnail_name = thumbnail_name;
        this.published = published;
    }

    public Newsletters(String title, String filename, String teasertext, String thumbnail_name, Date published) {
        this.title = title;
        this.filename = filename;
        this.teasertext = teasertext;
        this.thumbnail_name = thumbnail_name;
        this.published = published;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTeasertext() {
        return teasertext;
    }

    public void setTeasertext(String teasertext) {
        this.teasertext = teasertext;
    }

    public String getThumbnail_name() {
        return thumbnail_name;
    }

    public void setThumbnail_name(String thumbnail_name) {
        this.thumbnail_name = thumbnail_name;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    @Override
    public String toString() {
        return "newsletters{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", filename='" + filename + '\'' +
                ", teasertext='" + teasertext + '\'' +
                ", thumbnailName='" + thumbnail_name + '\'' +
                ", published=" + published +
                '}';
    }
}
