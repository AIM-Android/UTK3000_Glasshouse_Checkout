package com.advantech.utk3000_glasshouse_checkout.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.advantech.utk3000_glasshouse_checkout.MainApplication;
import com.advantech.utk3000_glasshouse_checkout.R;
import com.advantech.utk3000_glasshouse_checkout.adapter.ProductAdapter;
import com.advantech.utk3000_glasshouse_checkout.entity.Product;
import com.advantech.utk3000_glasshouse_checkout.utils.PermissionUtils;
import com.advantech.utk3000_glasshouse_checkout.utils.ToastUtil;
import com.nlscan.uhf.lib.HexUtil;
import com.nlscan.uhf.lib.TagInfo;
import com.nlscan.uhf.lib.UHFManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final static String ACTION_UHF_RESULT = "nlscan.intent.action.uhf.ACTION_RESULT_EX";
    private final static String EXTRA_TAG_INFO = "tag_info";

    private final static int REQUEST_RUNTIME_PERMISSION = 0x10;

    private static final int UHF_STATE_CONNECTED = 1;
    private static final int UHF_STATE_DISCONNECTED = 2;
    private static final int UHF_STATE_CONNECTING = 3;
    private static final int UHF_STATE_START_INVENTORY = 4;
    private static final int UHF_STATE_STOP_INVENTORY = 5;
    private static final int UHF_STATE_CONNECT_FAIL = 0x06;

    private static final String DEFAULT_DEV = "/dev/ttyACM0";

    private UHFManager mUHFManager;

    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;

    private List<Product> productList;

    private int count;
    private double price;

    private Set<String> epcBuffer = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestPermissions();

        initView();

        registerReceiver();
    }

    private void initView() {

        mRecyclerView = findViewById(R.id.rv_scan_checklist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ProductAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayout cancerButton = findViewById(R.id.btn_cancer_transaction);
        cancerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productList = new ArrayList<>();
//                productList = MainApplication.productList;
                Log.d(TAG, "set : " + epcBuffer.toString());
                for (String epc : epcBuffer) {
                    for (Product product : MainApplication.productList) {
                        if (product.getEpc().equals(epc)) {
                            productList.add(product);
                            price += product.getPrice();
                        }
                    }
                }
                count = productList.size();
                mAdapter.setDataList(productList);
                Log.d(TAG, "count : " + count + ", price : " + price);
                Log.d(TAG, "products : " + productList.toString());
                epcBuffer.clear();
            }
        });
        Button checkoutButton = findViewById(R.id.btn_checkout);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUHFManager.stopTagInventory();
                startActivity();
            }
        });
    }

    private void startActivity() {
        if (productList == null || productList.size() == 0) {
            ToastUtil.show(this, "Please scan the product first.", Gravity.CENTER, 0);
            return;
        }
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra("productList", (Serializable) productList);
        intent.putExtra("count_num", count);
        intent.putExtra("count_price", price);
        startActivity(intent);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UHFManager.ACTOIN_UHF_STATE_CHANGE);
        filter.addAction(ACTION_UHF_RESULT);
        registerReceiver(broadcastReceiver, filter);
    }

    private void requestPermissions() {
        if (PermissionUtils.checkRequestRuntimePermissions(this)) {
            PermissionUtils.requestAllRuntimePermission(this, REQUEST_RUNTIME_PERMISSION);
            Log.d(TAG, "requestPermissions.");
        } else {
            Log.d(TAG, "requestPermissions permissionGranted success.");
            doConnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_RUNTIME_PERMISSION == requestCode) {
            Log.d(TAG, "onRequestPermissionsResult : permissionGranted success.");
            doConnect();
        }
    }

    private void doConnect() {
        mUHFManager = UHFManager.getInstance(this);
        Log.d(TAG, "doConnect : "
                + "mUHFManager : " + mUHFManager
                + ", PLUGIN TYPE SERIEL : " + UHFManager.PLUGIN_TYPE_SERIEL
                + ", MODULE TYPE SERIAL : " + "MODULE_TYPE_SERIAL"
                + ", listener : " + "null");
        mUHFManager.asyncConnect(DEFAULT_DEV, UHFManager.PLUGIN_TYPE_SERIEL, "MODULE_TYPE_SERIAL", null);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context contxt, Intent intent) {
            if (intent == null) {
                return;
            }
            Log.d(TAG, "receive intent : " + intent.toString());
            if (UHFManager.ACTOIN_UHF_STATE_CHANGE.equals(intent.getAction())) {
                int state = intent.getIntExtra(UHFManager.EXTRA_UHF_STATE, -1);
                switch (state) {
                    case UHF_STATE_CONNECTING:
                        break;
                    case UHF_STATE_CONNECTED:
                        Log.d(TAG, "uhf module connect success.");
                        mUHFManager.startTagInventory();
                        break;
                    case UHF_STATE_DISCONNECTED:
                        Log.d(TAG, "uhf module disconnect.");
                        break;
                    case UHF_STATE_START_INVENTORY:
                        Log.d(TAG, "uhf module inventory started.");
                        break;
                    case UHF_STATE_STOP_INVENTORY:
                        Log.d(TAG, "uhf module inventory stoped.");
                        break;
                    case UHF_STATE_CONNECT_FAIL:
                        Log.d(TAG, "uhf module connect fail.");
                        break;
                }
            } else if (ACTION_UHF_RESULT.equals(intent.getAction())) {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(EXTRA_TAG_INFO);
                if (parcelables == null || parcelables.length == 0) {
                    return;
                }
                Log.d(TAG, "parcelables length : " + parcelables.length);
                for (Parcelable parcelable : parcelables) {
                    TagInfo tagInfo = (TagInfo) parcelable;
                    String epc = HexUtil.bytesToHexString(tagInfo.EpcId);
                    epcBuffer.add(epc);
                }
            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return super.dispatchKeyEvent(event);
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUHFManager.stopTagInventory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUHFManager.disconnect();
        unregisterReceiver(broadcastReceiver);
        Log.d(TAG, "unregisterReceiver");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onDestroy();
    }
}