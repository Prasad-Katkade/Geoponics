package com.example.geoponics;

public class ProductModel {
    public String product_name,product_desc,product_count,product_price,seller_name,seller_addr,seller_phno,seller_farmer,seller_ID,product_url,product_type;

    public ProductModel() {
    }

    public ProductModel(String product_name, String product_desc, String product_count, String product_price, String seller_name, String seller_addr, String seller_phno, String seller_farmer, String seller_ID,String product_url,String product_type) {
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.product_count = product_count;
        this.product_price = product_price;
        this.seller_name = seller_name;
        this.seller_addr = seller_addr;
        this.seller_phno = seller_phno;
        this.seller_farmer = seller_farmer;
        this.seller_ID = seller_ID;
        this.product_url=product_url;
        this.product_type=product_type;
    }


    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public String getProduct_count() {
        return product_count;
    }

    public void setProduct_count(String product_count) {
        this.product_count = product_count;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getSeller_addr() {
        return seller_addr;
    }

    public void setSeller_addr(String seller_addr) {
        this.seller_addr = seller_addr;
    }

    public String getSeller_phno() {
        return seller_phno;
    }

    public void setSeller_phno(String seller_phno) {
        this.seller_phno = seller_phno;
    }

    public String getSeller_farmer() {
        return seller_farmer;
    }

    public void setSeller_farmer(String seller_farmer) {
        this.seller_farmer = seller_farmer;
    }

    public String getSeller_ID() {
        return seller_ID;
    }

    public void setSeller_ID(String seller_ID) {
        this.seller_ID = seller_ID;
    }

    public String getProduct_url() {
        return product_url;
    }

    public void setProduct_url(String product_url) {
        this.product_url = product_url;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }
}
