# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\ADT\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#yx_sdk_core_m混淆配置

#37sy_sdk_core_m混淆配置

-ignorewarnings

# 代码混淆压缩比，在0~7之间，默认为5,一般不下需要修改
-optimizationpasses 5

# 混淆时不使用大小写混合，混淆后的类名为小写
# windows下的同学还是加入这个选项吧(windows大小写不敏感)
-dontusemixedcaseclassnames

# 指定不去忽略非公共的库的类
# 默认跳过，有些情况下编写的代码与类库中的类在同一个包下，并且持有包中内容的引用，此时就需要加入此条声明
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 不做预检验，preverify是proguard的四个步骤之一
# Android不需要preverify，去掉这一步可以加快混淆速度
-dontpreverify

# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
-verbose
-printmapping priguardMapping.txt

# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/artithmetic,!field/*,!class/merging/*

# 保护代码中的Annotation不被混淆
# 这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*

# 避免混淆泛型
# 这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留了继承自Activity、Application这些类的子类
# 因为这些子类有可能被外部调用
# 比如第一行就保证了所有Activity的子类不要被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# 如果有引用android-support-v4.jar包，可以添加下面这行
-keep public class com.null.test.ui.fragment.** {*;}



-keepclasseswithmembers,allowshrinking class * {
    public <init>(android.content.Context,android.util.AttributeSet);
}

-keepclasseswithmembers,allowshrinking class * {
    public <init>(android.content.Context,android.util.AttributeSet,int);
}

# 保留Parcelable序列化的类不能被混淆
-keep class * implements android.os.Parcelable{
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable 序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   !static !transient <fields>;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}

# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 对R文件下的所有类及其方法，都不能被混淆
-keepclassmembers class **.R$* {
    *;
}

# 对于带有回调函数onXXEvent的，不能混淆
-keepclassmembers class * {
    void *(**On*Event);
}

# for ali pay start---------------
-keep public class com.alipay.android.app.**{*;}
-keep public interface com.alipay.android.app.**{*;}
-keep public class com.alipay.android.app.IRemoteServiceCallback$*{*;}
-keep public class com.alipay.android.app.IRemoteServiceCallback$Stub$*{*;}
-keep public class com.alipay.android.app.IAlixPay$*{*;}
-keep public class com.alipay.android.app.IAlixPay$Stub$*{*;}
-keep public class com.yxyige.sdk.alipay.MobileSecurePayer{*;}
-keep public class com.yxyige.sdk.alipay.MobileSecurePayHelper{*;}

# for ali pay end-----------------

# for android-support-v4 start

-keep class android.support.** { *; }
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
#-keep public class com.fgw.kefu.widget.**{*;}

# for android-support-v4 end

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

##---------------End: proguard configuration for Gson  ----------

# 第三方libs库
# alipaySdk
-keep class com.alipay.** { *; }
-keep class com.ta.utdid2.** { *; }
-keep class com.ut.device.** { *; }
-keep class org.json.alipay.** { *; }

# android-support-v4
-keep class com.android.support.v4.** { *; }

# gson
-keep class com.google.gson.** { *; }

# UPPayAssistEx
-keep class com.unionpay.** { *; }

# wftsdk
-keep class com.switfpass.pay.** { *; }

# downwarn
-dontwarn sun.misc.Unsafe
-dontwarn com.google.common.collect.MinMaxPriorityQueue
-dontwarn android.support.v4.**

# 继承JavascriptInterface的类
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# yx_sdk_core start

# m
-keep public class com.yxyige.msdk.BaseYXMCore{*;}
-keep public class com.yxyige.msdk.YXMCore{*;}
-keep public class com.yxyige.afinal.**{*;}
-keep public class com.yxyige.msdk.ZipString{*;}
-keep public class com.yxyige.msdk.api.YXAppConfig{*;}
-keep public class com.yxyige.msdk.api.YXMResultListener{*;}

# s
-keep public class com.yxyige.sdk.core.YXSDK{*;}
-keep class com.yxyige.sdk.views.webview.**{*;}
-keep public class com.yxyige.sdk.view.PayWebDialog$* {*;}
-keep public interface com.yxyige.sdk.core.IError{*;}
-keep public class com.yxyige.sdk.http{*;}
-keep public class com.yxyige.sdk.bean.**{*;}

# yx_sdk_core_end end





