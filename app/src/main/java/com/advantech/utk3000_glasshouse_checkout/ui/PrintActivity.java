package com.advantech.utk3000_glasshouse_checkout.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.advantech.utk3000_glasshouse_checkout.R;
import com.advantech.utk3000_glasshouse_checkout.utils.PublicAction;
import com.advantech.utk3000_glasshouse_checkout.utils.YhInvoke;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import print.Print;

public class PrintActivity extends AppCompatActivity implements YhInvoke.BTCallback {

    private static final String TAG = PrintActivity.class.getSimpleName();

    private static final String ACTION_USB_PERMISSION = "com.advantech.utk_3000_quickcheckout";
    private static final int PRINT_INTERFACECLASS = 7;
    private static final int PRINT_LENGTH = 40;

    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private String mConnectType;

    private PendingIntent mPermissionIntent;

    private ExecutorService mExecutorService;
    private PublicAction mPublicAction;

    private Handler mHandler = new WeakReferenceHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

        registerReceiver();
        connectUSB();
    }

    private void registerReceiver() {
        Intent intent = new Intent();
        intent.setAction(ACTION_USB_PERMISSION);
        intent.setPackage(getPackageName());
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
    }

    private void initView() {
        Button button = findViewById(R.id.btn_restart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                PrintSampleReceipt();
                printUSB(getIntent().getStringExtra("printdata"));
            }
        });
        YhInvoke.setBtCallback(this);
    }

    private void printUSB(String data) {
        String total = String.valueOf(getIntent().getIntExtra("price", 0));
        String count = String.valueOf(getIntent().getIntExtra("count", 0));
        String residue = String.valueOf(1000 - getIntent().getIntExtra("price", 0));
        String printdata = "[" +
                "{\"img\":{\"src\":\"CRLandImposRes/printLogo/2400034001.bmp\",\"offset\":\"0\",\"width\":\"384\",\"height\":\"98\"}}," +
                "{\"txt-title\":{\"data\":\"                    sales slip\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"              [Customer Connection]\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-splitLine\":{\"data\":\"·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-\",\"align\":\"1\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Merchant Name:KELME\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Merchant ID:L0406N01\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Mall name:Advantech\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Shopping mall number:2YRJ00201\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Cash register number:01\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"cashier:2yrj00201l0406n0101\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Serial number:22010001\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Transaction Date:2023/12/22 12:00:13 \",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Print date:2023/12/22 12:00:17\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"barCode\":{\"data\":\"0124000340231222010001\",\"align\":\"1\",\"width\":\"280\",\"height\":\"60\"}}," +
                "{\"txt-splitLine\":{\"data\":\"·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-\",\"align\":\"1\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Product          quantity     amount of money(RMB)\",\"align\":\"0\",\"font\":\"1\"}}," +
                data +
                "{\"txt-title\":{\"data\":\"Total number" + getSpace(PRINT_LENGTH - "Total number".length()) + count + "\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Total amount" + getSpace(PRINT_LENGTH - "Total amount".length()) + total + "\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-splitLine\":{\"data\":\"·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-\",\"align\":\"1\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Payment records             amount of money(RMB)\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Alipay" + getSpace(PRINT_LENGTH - "Alipay".length()) + total + "\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-splitLine\":{\"data\":\"·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-\",\"align\":\"1\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"VIP card number:138****4089\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"This gathering of stars:" + total + "\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Account Advantech Star balance:" + residue + "\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-splitLine\":{\"data\":\"·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-\",\"align\":\"1\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"                 Alipay sales slip\",\"align\":\"1\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Card number:289339*** **8678\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Authorization code:\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Reference number:903562774242\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"Voucher number:000515\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"External order number:0054531141890160Ymc0s\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-title\":{\"data\":\"amount of money:277\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-splitLine\":{\"data\":\"·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-\",\"align\":\"1\",\"font\":\"1\"}}," +
                "{\"qrCode\":{\"data\":\"https://www.advantech.com\",\"offset\":\"20\",\"length\":\"350\"}}," +
                "{\"txt-title\":{\"data\":\" \",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"txt-splitLine\":{\"data\":\"·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-·-\",\"align\":\"1\",\"font\":\"1\"}}," +
//                "{\"txt-title\":{\"data\":\"reminder：\\nThis QR code can be used for self-service star collection on the Advantech app/Wanxiang mini program, or for collecting stars at the mall service desk with receipts (valid on the same day)\\nPlease keep this ticket as the only purchase voucher\",\"align\":\"0\",\"font\":\"1\"}}," +
                "{\"cut\":{\"data\":\"\"}}]";
        try {
            Map<String,String> map=new HashMap<>();
            map.put("method", "print");
            map.put("params", printdata);
            YhInvoke.execute(this,"", map);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private String getSpace(int count) {
        StringBuilder rex = new StringBuilder();
        for (int i = 0; i < count; i++) {
            rex.append(" ");
        }
        return rex.toString();
    }

    private void startActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void connectUSB() {
        mConnectType = "USB";
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            mUsbDevice = deviceIterator.next();
            if (isPrintExist() && mPermissionIntent != null) {
                Log.d("PRINT_TAG", "vendorID--" + mUsbDevice.getVendorId() + "ProductId--" + mUsbDevice.getProductId());
                mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
            }
        }
    }

    @Override
    public void printSuccess() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity();
            }
        }, 2000);
    }

    @Override
    public void printFailed() {

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                Log.d(TAG, "receive intent is null.");
                return;
            }
            Log.d(TAG, "action : " + intent.toString());

            try {
                mUsbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (mUsbDevice == null) {
                    Log.d(TAG, "mUsbDevice is null.");
                    return;
                }
                if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
                    synchronized (context) {
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (Print.PortOpen(context, mUsbDevice) != 0) {
                                Log.d(TAG, "printer is not connected.");
                            } else {
                                Log.d(TAG, "printer is connected.");
//                                PrintSampleReceipt();
                            }
                        }
                    }
                } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
                    if (isPrintExist()) {
                        Print.PortClose();
                        Log.d(TAG, "printer is closed.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "usbReceiver" + e.getMessage());
            }
        }
    };

    private void PrintSampleReceipt() {
        mPublicAction = new PublicAction(this);
        mExecutorService = Executors.newSingleThreadExecutor();
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Print.Initialize();
                    mPublicAction.BeforePrintAction();
                    String[] ReceiptLines = getResources().getStringArray(R.array.activity_main_sample_2inch_receipt);
                    String str = "";
                    for (int i = 0; i < ReceiptLines.length; i++) {
                        str += ReceiptLines[i];
                    }
                    int result = Print.PrintText(str);
                    mPublicAction.AfterPrintAction();
                } catch (Exception e) {
                    Log.e("Print", (new StringBuilder("PrintFragment --> PrintSampleReceipt ")).append(e.getMessage()).toString());
                }
            }
        });
    }

    private boolean isPrintExist() {
        for (int index = 0; index < mUsbDevice.getInterfaceCount(); index++) {
            UsbInterface usbInterface = mUsbDevice.getInterface(index);
            if (PRINT_INTERFACECLASS == usbInterface.getInterfaceClass()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, event.toString());
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return super.dispatchKeyEvent(event);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private static class WeakReferenceHandler extends Handler {
        private final WeakReference<Activity> mActivity;
        public WeakReferenceHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);

        }
        @Override
        public void handleMessage(Message msg) {
            if(mActivity.get() == null) {
                return;
            }
        }
    }
}