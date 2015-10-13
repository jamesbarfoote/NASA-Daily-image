package nz.ac.vuw.ecs.nasadailyimage;

/**
 * The class is created to represent each image object
 * <p/>
 * @author Aaron Chen
 * @version 1.0
 * @since 2015-08-31
 */
public class Image {
    private String title = null;
    private String description = null;
    private String date = null;
    private String url = null;

    public Image() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
