# UTK3000_Glasshouse_Checkout

#### 使用说明

- 安装 UTK3000_Glasshouse_Checkout_v1.0.1_release.apk
- 确认安卓设备与 UHF 模块连接的串口节点是否有读写权限，如果没有则需要手动添加权限
```bash
adb shell
su
chmod +rwx /dev/ttyACM0
```
- 打开 UTK3000_Glasshouse_Checkout APP，点击”扫描商品“按钮，----> 点击开始结账----->扫描付款卡------>点击打印

- APP 截图

说明：由于截图是构造数据，上哦商品价格没有进行填充，看到的数字仅为 TextView 的 hint，

![](https://github.com/AIM-Android/UTK3000_Glasshouse_Checkout/tree/main/screenshot/scan.png)
![](https://github.com/AIM-Android/UTK3000_Glasshouse_Checkout/tree/main/screenshot/pay.png)
![](https://github.com/AIM-Android/UTK3000_Glasshouse_Checkout/tree/main/screenshot/print.png)
![](https://github.com/AIM-Android/UTK3000_Glasshouse_Checkout/tree/main/screenshot/billes.png)