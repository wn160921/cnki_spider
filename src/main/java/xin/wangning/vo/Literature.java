package xin.wangning.vo;

import java.util.Date;
import java.util.List;

public class Literature {
    String name;
    String url;
    String classify;
    Integer referNum;
    String belong;
    Integer year;
    Integer phase;
    List<Refer> referList;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Refer> getReferList() {
        return referList;
    }

    public void setReferList(List<Refer> referList) {
        this.referList = referList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
    }

    public Integer getReferNum() {
        return referNum;
    }

    public void setReferNum(Integer referNum) {
        this.referNum = referNum;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getPhase() {
        return phase;
    }

    public void setPhase(Integer phase) {
        this.phase = phase;
    }
}
