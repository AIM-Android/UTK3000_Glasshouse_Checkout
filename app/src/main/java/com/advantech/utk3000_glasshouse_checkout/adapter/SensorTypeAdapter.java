package com.advantech.utk3000_glasshouse_checkout.adapter;

import com.advantech.utk3000_glasshouse_checkout.entity.PrintData;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class SensorTypeAdapter extends TypeAdapter<PrintData> {

    @Override
    public void write(JsonWriter out, PrintData value) throws IOException {
        out.beginObject();
        //按自定义顺序输出字段信息
        out.name("data").value(value.getData());
        out.name("align").value(value.getAlign());
        out.name("font").value(value.getFont());
        out.endObject();
    }

    @Override
    public PrintData read(JsonReader in) throws IOException {
        return null;
    }
}