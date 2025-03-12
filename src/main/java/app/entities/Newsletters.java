package app.entities;

import java.util.Date;

public class Newsletters {
    private int id;
    private String title;
    private String filename;
    private String teasertext;
    private String thumbnailName;
    private Date published;

    public Newsletters(int id, String title, String filename, String teasertext, String thumbnailName, Date published) {
        this.id = id;
        this.title = title;
        this.filename = filename;
        this.teasertext = teasertext;
        this.thumbnailName = thumbnailName;
        this.published = published;
    }

    public Newsletters(String title, String filename, String teasertext, String thumbnailName, Date published) {
        this.title = title;
        this.filename = filename;
        this.teasertext = teasertext;
        this.thumbnailName = thumbnailName;
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

    public String getThumbnailName() {
        return thumbnailName;
    }

    public void setThumbnailName(String thumbnailName) {
        this.thumbnailName = thumbnailName;
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
                ", thumbnailName='" + thumbnailName + '\'' +
                ", published=" + published +
                '}';
    }
}
