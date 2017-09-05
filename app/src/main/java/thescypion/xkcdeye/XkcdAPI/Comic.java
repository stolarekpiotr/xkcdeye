package thescypion.xkcdeye.XkcdAPI;

public class Comic {
    private Integer num;
    private String title;
    private String img;
    private String transcript;
    private String alt;

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

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    @Override
    public String toString() {
        return String.format("Num: %4$d \n Title: %1$s \n Img: %2$s \n Transcript: %3$s", title, img, transcript, num);
    }

}
