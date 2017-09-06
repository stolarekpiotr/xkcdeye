package thescypion.xkcdeye.XkcdAPI;

public class Comic {
    private Integer num;
    private String title;
    private String img;
    private String alt;
    private Integer day;
    private Integer month;
    private Integer year;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    @Override
    public String toString() {
        return String.format("Num: %4$d \n Title: %1$s \n Img: %2$s", title, img, num);
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDateString() {
        return String.format("%1$d.%2$d.%3$d", day, month, year);
    }
}
