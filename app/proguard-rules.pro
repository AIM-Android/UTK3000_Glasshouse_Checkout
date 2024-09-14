# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 优化时启用混淆
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 不混淆 R 类（保留资源）
-keep class **.R$* { *; }

# 保持应用程序入口点（Activity、Service、BroadcastReceiver、ContentProvider等）
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# 保持带有 Parcelable 接口的类（防止序列化问题）
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}

# 保留枚举类
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

# 保留 Gson 反序列化需要的类
-keep class * extends com.google.gson.TypeAdapter
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# OkHttp 保留类
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Retrofit 保留接口
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# 保留注解类
-keepattributes *Annotation*

# 保留类和方法的反射使用
-keepclassmembers class ** {
@android.webkit.JavascriptInterface <methods>;
}

# 保留所有实现 Serializable 的类及其字段
-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
private void readObjectNoData();
java.lang.Object writeReplace();
java.lang.Object readResolve();
!transient <fields>; # 保留非 transient 字段
}

-keep class com.esc.** { *; }
-keep class com.nlscan.uhf.lib.** { *; }
-keep class aidc.uhf.pda.args_comm.** { *; }
-keep class com.uhf.api.** { *; }
-keep class nls.** { *; }

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn android.os.SystemProperties
-dontwarn com.nlscan.android.uhf.TagInfo$SL_TagProtocol
-dontwarn com.nlscan.android.uhf.TagInfo
-dontwarn com.nlscan.android.uhf.UHFModuleInfo
-dontwarn com.pow.api.cls.RfidPower$PDATYPE
-dontwarn com.pow.api.cls.RfidPower
-dontwarn com.shockwave.pdfium.PdfDocument
-dontwarn com.shockwave.pdfium.PdfiumCore
-dontwarn com.uhf.api.cls.ErrInfo
-dontwarn com.uhf.api.cls.R2000Command
-dontwarn com.uhf.api.cls.ReadExceptionListener
-dontwarn com.uhf.api.cls.ReadListener
-dontwarn com.uhf.api.cls.Reader$AntPower
-dontwarn com.uhf.api.cls.Reader$AntPowerConf
-dontwarn com.uhf.api.cls.Reader$ConnAnts_ST
-dontwarn com.uhf.api.cls.Reader$CustomParam_ST
-dontwarn com.uhf.api.cls.Reader$EmbededData_ST
-dontwarn com.uhf.api.cls.Reader$HardwareDetails
-dontwarn com.uhf.api.cls.Reader$HoptableData_ST
-dontwarn com.uhf.api.cls.Reader$IT_MODE
-dontwarn com.uhf.api.cls.Reader$Inv_Potl
-dontwarn com.uhf.api.cls.Reader$Inv_Potls_ST
-dontwarn com.uhf.api.cls.Reader$Lock_Obj
-dontwarn com.uhf.api.cls.Reader$Lock_Type
-dontwarn com.uhf.api.cls.Reader$MaindBoard_Type
-dontwarn com.uhf.api.cls.Reader$Module_Type
-dontwarn com.uhf.api.cls.Reader$Mtr_Param
-dontwarn com.uhf.api.cls.Reader$READER_ERR
-dontwarn com.uhf.api.cls.Reader$ReaderVersion
-dontwarn com.uhf.api.cls.Reader$Reader_Ip
-dontwarn com.uhf.api.cls.Reader$Reader_Type
-dontwarn com.uhf.api.cls.Reader$Region_Conf
-dontwarn com.uhf.api.cls.Reader$SL_TagProtocol
-dontwarn com.uhf.api.cls.Reader$SpecObject
-dontwarn com.uhf.api.cls.Reader$TAGINFO
-dontwarn com.uhf.api.cls.Reader$TagFilter_ST
-dontwarn com.uhf.api.cls.Reader
-dontwarn nls.ble.uhf.args_comm.NLApisModule
-dontwarn nls.ble.uhf.args_comm.NLUrmFactoryInfoT