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

    public String getTitle() {
        return title;
    }

    public String getFilename() {
        return filename;
    }

    public String getTeasertext() {
        return teasertext;
    }

    public String getThumbnailName() {
        return thumbnailName;
    }

    public Date getPublished() {
        return published;
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
