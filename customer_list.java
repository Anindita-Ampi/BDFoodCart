package com.example.lenovo.bdfoodcart;

/**
 * Created by Lenovo on 19/09/2017.
 */

public class customer_list {

    String cust_name;
    String cust_phone;

    public customer_list() {
    }

    public customer_list(String cust_name, String cust_phone) {
        this.cust_name = cust_name;
        this.cust_phone = cust_phone;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getCust_phone() {
        return cust_phone;
    }

    public void setCust_phone(String cust_phone) {
        this.cust_phone = cust_phone;
    }
}
