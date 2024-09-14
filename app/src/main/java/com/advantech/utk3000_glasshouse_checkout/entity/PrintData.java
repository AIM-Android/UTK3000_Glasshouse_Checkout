package com.advantech.utk3000_glasshouse_checkout.entity;

public class PrintData {
    private String data;
    private String align;
    private String font;

    public PrintData(String data, String align, String font) {
        this.data = data;
        this.align = align;
        this.font = font;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}