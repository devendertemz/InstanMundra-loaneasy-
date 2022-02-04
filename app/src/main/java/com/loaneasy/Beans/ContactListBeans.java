package com.loaneasy.Beans;

/**
 * Created by Ravindra on 20-Apr-18.
 */

public class ContactListBeans  {

    String user_id, phone_no, contact_name;

    public ContactListBeans(String user_id, String phone_no, String contact_name) {
        this.user_id = user_id;
        this.phone_no = phone_no;
        this.contact_name = contact_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }
}
