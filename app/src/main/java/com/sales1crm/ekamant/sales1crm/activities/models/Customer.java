package com.sales1crm.ekamant.sales1crm.activities.models;

/**
 * Created by MatthewLim on 2/13/17.
 */

public class Customer {

    private int customer_id;
    private String customer_name;
    private String customer_address;
    private String customer_tipe;
    private int customer_type;
    private String customer_code;

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public int getCustomer_type() {
        return customer_type;
    }

    public void setCustomer_type(int customer_type) {
        this.customer_type = customer_type;
    }

    public String getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
    }

    public String getCustomer_tipe() {
        return customer_tipe;
    }

    public void setCustomer_tipe(String customer_tipe) {
        this.customer_tipe = customer_tipe;
    }
}
