package com.advantech.utk3000_glasshouse_checkout.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.advantech.utk3000_glasshouse_checkout.R;
import com.advantech.utk3000_glasshouse_checkout.adapter.SensorTypeAdapter;
import com.advantech.utk3000_glasshouse_checkout.entity.PrintData;
import com.advantech.utk3000_glasshouse_checkout.entity.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PayActivity extends AppCompatActivity {

    private static final String TAG = PayActivity.class.getSimpleName();

    private static final long DELAY_MILLIS = 500;

    private LinearLayout payDetailLayout;

    private static final int COUNT_LENGTH_PRINT = 22;
    private static final int PRINT_LENGTH = 40;

    private int count;
    private double price;

    private List<Product> products = new ArrayList<>();
    private List<PrintData> printDataList = new ArrayList<>();
    private StringBuilder strBuilder = new StringBuilder();

    private StringBuilder inputbuffer = new StringBuilder();

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable processInputRunnable = new Runnable() {
        @Override
        public void run() {
            // 处理输入数据
            handleInputBuffer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
    }

    private void initView() {
        count = getIntent().getExtras().getInt("count_num");
        price = getIntent().getExtras().getDouble("count_price");
        products = (List<Product>) getIntent().getSerializableExtra("productList");
        checkPrintData();
        Log.d(TAG, "products : " + products.toString() + ", count : " + count + ", price : " + price);
        TextView countTextview = findViewById(R.id.tv_product_count);
        countTextview.setText(String.valueOf(count));
        TextView priceTextview = findViewById(R.id.tv_checkout_num);
        priceTextview.setText(String.valueOf(price));
        TextView payFunction = findViewById(R.id.tv_checkout_way);
        payDetailLayout = findViewById(R.id.layout_pay_details);
    }

    private void checkPrintData() {
        for (Product product : products) {
            String data = product.getName()
                    + getSpace(COUNT_LENGTH_PRINT - product.getName().length()) + product.getCount()
                    + getSpace(PRINT_LENGTH - product.getName().length() - (COUNT_LENGTH_PRINT - product.getName().length()) - String.valueOf(product.getCount()).length())
                    + product.getCount() * product.getPrice();
            printDataList.add(new PrintData(data, "0", "1"));
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(PrintData.class, new SensorTypeAdapter()).create();
        for (PrintData data : printDataList) {
            strBuilder.append("{").append("\"txt-title\"").append(":").append(gson.toJson(data)).append("}").append(",");
        }
    }

    private String getSpace(int count) {
        StringBuilder rex = new StringBuilder();
        for (int index = 0; index < count; index++) {
            rex.append(" ");
        }
        return rex.toString();
    }

    private void handleInputBuffer() {
        Log.d(TAG, "inputbuffer : " + inputbuffer.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(inputbuffer)) {
                    payDetailLayout.setVisibility(View.VISIBLE);
                    startActivity();
                }
            }
        });
        inputbuffer.setLength(0);
    }

    private void startActivity() {
        Intent intent = new Intent(this, PrintActivity.class);
        intent.putExtra("printdata", strBuilder.toString());
        intent.putExtra("count", count);
        intent.putExtra("price", price);
        intent.putExtra("function", "weixin");
        intent.putExtra("card", inputbuffer.toString());
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() != KeyEvent.KEYCODE_BACK) {
            inputbuffer.append((char) event.getUnicodeChar());
            handler.removeCallbacks(processInputRunnable);
            handler.postDelayed(processInputRunnable, DELAY_MILLIS);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        payDetailLayout.setVisibility(TextUtils.isEmpty(inputbuffer) ? View.GONE : View.VISIBLE);
    }
}
