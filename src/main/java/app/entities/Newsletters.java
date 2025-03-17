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

    public String getTitle() {
        return title;
    }

    public String getFilename() {
        return filename;
    }

    public String getTeasertext() {
        return teasertext;
    }

    public String getThumbnail_name() {
        return thumbnail_name;
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
                ", thumbnailName='" + thumbnail_name + '\'' +
                ", published=" + published +
                '}';
    }
}
