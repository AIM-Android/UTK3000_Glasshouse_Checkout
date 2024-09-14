package com.advantech.utk3000_glasshouse_checkout.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.esc.PrinterHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import print.Print;
import print.PublicFunction;

public class YhPrintMain {
    private String mAddress = "-1";

    public boolean connectBluetoothOnAppStart(Activity activity) {
        try {
            Print.LanguageEncode = "gb2312";
        } catch (Exception e) {
            Log.e("Print", (new StringBuilder("PublicAction --> BeforePrintAction ")).append(e.getMessage()).toString());
        }
        try {
            Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            if (bondedDevices != null && bondedDevices.size() > 0) {
                String[] addrs = new String[bondedDevices.size()];
                int index = 0;
                for (BluetoothDevice bondedDevice : bondedDevices) {
                    addrs[index] = bondedDevice.getAddress();
                    index++;
                }
                if (addrs.length > 0) {
                    mAddress = addrs[0];
                    int result = Print.PortOpen(activity, "Bluetooth," + mAddress);
                    if (result == 0) {
                        ToastUtil.show(activity, "Bluetooth connection successful,result=", Gravity.CENTER, 0);
                        PrinterHelper.isWriteLog=true;
                        PrinterHelper.isHex=true;
                        Print.BeepBuzzer((byte)1,(byte)5,(byte)1);
                        return true;
                    } else {
                        ToastUtil.show(activity, "Bluetooth connection failed, please check；result=", Gravity.CENTER, 0);
                    }
                } else {
                    ToastUtil.show(activity, "No available Bluetooth found nearby address，check on", Gravity.CENTER, 0);
                }
            } else {
                ToastUtil.show(activity, "No Bluetooth device hardware found nearby, please check (pairing)", Gravity.CENTER, 0);
            }
        } catch (Exception e) {
            ToastUtil.show(activity,"Failed to obtain Bluetooth device, please check"+e.getMessage(), Gravity.CENTER, 0);
            e.printStackTrace();
        }
        return false;
    }


    public boolean connectBluetooth(Activity context) {
        try {
            Print.LanguageEncode = "gb2312";
        } catch (Exception e) {
            Log.e("Print", (new StringBuilder("PublicAction --> BeforePrintAction ")).append(e.getMessage()).toString());
        }
        try {
            Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            if (bondedDevices != null && bondedDevices.size() > 0) {
                String[] addrs = new String[bondedDevices.size()];
                int index = 0;
                for (BluetoothDevice bondedDevice : bondedDevices) {
                    addrs[index] = bondedDevice.getAddress();
                    index++;
                }
                if (addrs.length > 0) {
                    mAddress = addrs[0];
                    int result = Print.PortOpen(context, "Bluetooth," + mAddress);
                    if (result == 0) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean connectUSB(Context context) {
        try {
            UsbDevice usbDevice = null;
            UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> deviceHashMap = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceHashMap.values().iterator();
            while (deviceIterator.hasNext()) {
                usbDevice = deviceIterator.next();
                if (isPrint(usbDevice)) {
                    Log.d("PRINT_TAG", "vendorID--" + usbDevice.getVendorId() + "ProductId--" + usbDevice.getProductId());
                    break;
                }
            }
            if (usbDevice != null) {
                int result = Print.PortOpen(context, usbDevice);
                if (result == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isPrint(UsbDevice usbDevice) {
        for (int index = 0; index < usbDevice.getInterfaceCount(); index++) {
            UsbInterface usbInterface = usbDevice.getInterface(index);
            if (7 == usbInterface.getInterfaceClass()) {
                return true;
            }
        }
        return false;
    }

    public boolean isPrintConnected() {
        try {
            boolean isOpened = Print.IsOpened();
            if (!isOpened) {
                return false;
            }
            /**
             * 尝试发送数据
             * 不等于-1：发送给打印机成功。 -1：发送失败
             */
            return Print.SetCharacterSet((byte) 0) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private final int BITMAP_BLACKW = 0;
    private final int BITMAP_SHAKE = 1;
    private final int BITMAP_GATHER = 2;

    public boolean printBill(Activity activity, JSONArray params) {
        try {
            Print.LanguageEncode = "gb2312";
            PublicFunction PFun = new PublicFunction(activity);
            if (!TextUtils.isEmpty(PFun.ReadSharedPreferencesData("Codepage"))) {
                String codepage = PFun.ReadSharedPreferencesData("Codepage").split(",")[1];
                String sLEncode = PFun.getLanguageEncode(codepage);
                //设置Codepage
                Print.LanguageEncode = sLEncode;
            }
            JSONObject obj = null;
            for (int i = 0; i < params.length(); i++) {
                obj = params.getJSONObject(i);
                Iterator iterator = obj.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    if (key.indexOf("img") == 0) {
                        String imgSrc = "";
                        int imgOffset = 0;

                        JSONObject imgObj = obj.getJSONObject(key);
                        Iterator imgObjIterator = imgObj.keys();

                        while (imgObjIterator.hasNext()) {
                            String titleKey = (String) imgObjIterator.next();
                            if (titleKey.indexOf("src") == 0) {
                                imgSrc = imgObj.optString(titleKey);
                            }
                            if (titleKey.indexOf("offset") == 0) {
                                imgOffset = imgObj.optInt(titleKey);
                            }
                        }

                        if (!imgSrc.equals("")) {
                            try {
                                Print.SetJustification(1);
                                Print.PrintBitmap(getBitMap(imgSrc), BITMAP_BLACKW, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // If there are headings in the parameter, add print heading steps.
                    if (key.indexOf("txt") == 0) {
                        String txt = "";
                        String titleAlign = "";
                        JSONObject spaceXY = null;
                        JSONObject titleFontFormat = null;

                        JSONObject titleObj = obj.getJSONObject(key);
                        Iterator titleObjIterator = titleObj.keys();

                        while (titleObjIterator.hasNext()) {
                            String titleKey = (String) titleObjIterator.next();
                            if (titleKey.indexOf("data") == 0) {
                                txt = titleObj.optString(titleKey);
                            }
                            if (titleKey.indexOf("align") == 0) {
                                titleAlign = titleObj.optString(titleKey);
                            }

                            if (titleKey.indexOf("font") == 0) {
                            }
                        }
                        int alignment = 0;//0:左对齐，1：居中，2：右对齐
                        int attribute = 0;
                        int textSize = 0;
//                        Print.PrintText(txt, alignment, attribute, textSize);
                        Print.SetJustification(0);
                        Print.PrintText(txt + "\n");
                    }
                    //barCode
                    if (key.indexOf("barCode") == 0) {
                        //研华的sdk 关于barcode的实现与impos的生成规则有冲突，需要手动生成条形码，
                        String barCode = "";
                        JSONObject barCodeObj = obj.getJSONObject(key);
                        Iterator barCodeIterator = barCodeObj.keys();

                        while (barCodeIterator.hasNext()) {
                            String qrCodeKey = (String) barCodeIterator.next();
                            if (qrCodeKey.indexOf("data") == 0) {
                                barCode = barCodeObj.optString(qrCodeKey);
                            }
                        }
                        if (!barCode.equals("")) {
                            Print.SetJustification(1);
                            Print.PrintBitmap(createBarcode(barCode), BITMAP_BLACKW, 0);
                            Print.PrintAndLineFeed();
                            Print.PrintText(barCode,1,0,0);
                        }
                    }

                    // qrcode
                    if (key.indexOf("qrCode") == 0) {
                        JSONObject qrCodeObj = obj.getJSONObject(key);
//                System.out.println("qrcode" + qrCodeObj);
                        String qrCode = "";
                        int qrCodeOffset = 55;
                        int qrCodeImgHeight = 350;

                        Iterator qrCodeiterator = qrCodeObj.keys();

                        while (qrCodeiterator.hasNext()) {

                            String qrCodeKey = (String) qrCodeiterator.next();

                            if (qrCodeKey.indexOf("data") == 0) {
                                qrCode = qrCodeObj.optString(qrCodeKey);
                            }
                            if (qrCodeKey.indexOf("offset") == 0) {
                                qrCodeOffset = qrCodeObj.optInt(qrCodeKey);
                            }
                            if (qrCodeKey.indexOf("qrCodeImgHeight") == 0) {
                                qrCodeImgHeight = qrCodeObj.optInt(qrCodeKey);
                            }
                        }

                        if (!qrCode.equals("")) {
                            int size = 7;//1~16
                            int justification = 1;//0:左对齐，1：居中，2：右对齐
                            int QRCodeLevel = 4;//1:L，2:M，3:Q，4:H
                            Print.PrintQRCode(qrCode, size, (QRCodeLevel + 0x30), justification);
                        }
                    }
                    //barCode
                    if (key.indexOf("cut") == 0) {
                        Print.PrintAndFeed(10);//走纸距离（单位：distance*y轴的移动单元 mm）
                        int distance = 10;//走纸距离（单位：distance*y轴的移动单元 mm）
                        Print.CutPaper(1, distance);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Bitmap getBitMap(String imgSrc) throws Exception {
        InputStream in = null;
        File file = new File(Environment.getExternalStorageDirectory(), imgSrc);
        in = new FileInputStream(file);
        return BitmapFactory.decodeStream(in);
    }
    public static Bitmap createBarcode(String content) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, 400, 70);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? 0xff000000 : 0xFFFFFFFF;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}