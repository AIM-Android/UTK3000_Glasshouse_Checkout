package com.advantech.utk3000_glasshouse_checkout.entity;

import java.io.Serializable;

public class Product implements Serializable {

    private String epc;

    private String imgPath;

    private String name;

    private double price;

    private int count;

    public Product(String epc, String imgPath, String name, double price, int count) {
        this.epc = epc;
        this.imgPath = imgPath;
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Product{" +
                "epc='" + epc + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", count=" + count +
                '}';
    }
}
