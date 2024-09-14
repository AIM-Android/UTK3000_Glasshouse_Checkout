package com.advantech.utk3000_glasshouse_checkout.utils;

import android.app.Activity;
import android.view.Gravity;

import org.json.JSONArray;

import java.util.Map;

import print.Print;


public class YhInvoke {
    public static final String TAG = "YhInvoke";
    private Activity activity = null;
    private static YhPrintMain print;
    private static BTCallback btCallback;

    public static boolean isPrintConnected() {
        return print != null && print.isPrintConnected();
    }

    public static void setBtCallback(BTCallback btCallback) {
        YhInvoke.btCallback = btCallback;
    }

    public static boolean execute(Activity activity, String action, Map<String,String> map) {
        if (print == null) {
            print = new YhPrintMain();
        }

        try {
            String method = map.get("method");
            switch (method) {
                case "testPrint":
                    if (!print.isPrintConnected()) {
                        print.connectBluetoothOnAppStart(activity);
                    }else{
                        Print.BeepBuzzer((byte)1,(byte)10,(byte)1);
                        ToastUtil.show(activity, "Bluetooth connected.", Gravity.CENTER, 0);
                    }
                    break;
                case "print":
                    JSONArray printDataArray = new JSONArray(map.get("params"));
                    if (!print.isPrintConnected()) {
                        print.connectUSB(activity);
                    }
                    if (print.isPrintConnected()) {
                        boolean printResult = print.printBill(activity, printDataArray);
                        if (printResult) {
                            if (btCallback != null) {
                                btCallback.printSuccess();
                            }
                            ToastUtil.show(activity, "Printing successful.", Gravity.CENTER, 0);
                        } else {
                            ToastUtil.show(activity, "Printing failed.", Gravity.CENTER, 0);
                        }
                    } else {
                        ToastUtil.show(activity, "Printer connection failed.", Gravity.CENTER, 0);
                    }
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public interface BTCallback {
        void printSuccess();

        void printFailed();
    }
}