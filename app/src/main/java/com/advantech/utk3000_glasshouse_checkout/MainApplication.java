package com.advantech.utk3000_glasshouse_checkout;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.advantech.utk3000_glasshouse_checkout.entity.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getSimpleName();

    public static List<Product> productList;

    @Override
    public void onCreate() {
        super.onCreate();
        productList = getConfigData();
        Log.d(TAG, "product : " + productList);
    }

    private List<Product> getConfigData() {
        List<Product> products = new ArrayList<>();
        AssetManager assetManager = getAssets();
        try (InputStream inputStream = assetManager.open("initial.json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String epc = jsonObject.getString("epc");
                String imagePath = jsonObject.getString("image");
                String name = jsonObject.getString("name");
                double price = jsonObject.getDouble("price");
                int count = jsonObject.getInt("count");

                products.add(new Product(epc, imagePath, name, price, count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}
