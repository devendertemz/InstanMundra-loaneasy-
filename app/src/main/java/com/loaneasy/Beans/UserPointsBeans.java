package com.loaneasy.Beans;

/**
 * Created by Ravindra on 22-Jun-18.
 */
public class UserPointsBeans {

   String info_id,category,sub_category,points;

    public UserPointsBeans(String sno, String category, String sub_category, String points) {
        this.category = category;
        this.sub_category = sub_category;
        this.points = points;
        this.info_id = sno;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getInfo_id() {
        return info_id;
    }

    public void setInfo_id(String info_id) {
        this.info_id = info_id;
    }

    @Override
    public String toString() {
        return sub_category;
    }
}
