# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Johnny\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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
-verbose
#忽略警告：
-ignorewarnings
#不预校验:
-dontpreverify
#不使用混合的类名:
-dontusemixedcaseclassnames

#不要跳过非公共类库:
-dontskipnonpubliclibraryclasses

-dontskipnonpubliclibraryclassmembers

#优化：
-optimizationpasses 5
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保留注解:
-keepattributes *Annotation*,EnclosingMethod,InnerClasses

#避免使用泛型的位置混淆后出现类型转换错误:
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

-keepattributes Signature
#保留本地方法：
-keepclasseswithmembers class * {
    native <methods>;
}

#保留枚举类型成员的方法：
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保留系统组件及其子类：
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}
#去除support-v4包的警告，保留相关API:
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.FragmentActivity
-keep public class android.support.v4.accessibilityservice.** { *; }
-keep public class android.support.v4.app.** { *; }
-keep public class android.support.v4.os.** { *; }
-keep public class android.support.v4.view.** { *; }
-keep public class android.support.v4.widget.** { *; }

#保留View子类读取XML的构造方法：
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
-keepclasseswithmembers class * {
  public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保留JSON、Parcelable、Serailizable相关API:
# Gson specific classes
-keep class com.google.gson.examples.android.model.** { *; }
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable { *; }

#去除调试日志，将所有Log.d()改为Log.i():
-assumenosideeffects class android.util.Log{
  public static *** d(...);
  public static *** i(...);
}

#保留资源文件
-keepclassmembers class **.R$* {
    public static <fields>;
}

################### 项目内混淆 start ###########################
#保留http下第一级的字段
-keepclassmembers class im.boss66.com.http.* { *; }

-keepclassmembers class im.boss66.com.entity.** { *; }

-keepclassmembers class im.boss66.com.http.request.** {*; }

-keepclassmembers class im.boss66.com.http.request.*$*{*; }

-keep public class im.boss66.com.db.*{ *; }
#-keep class com.atgc.mycs.activity.ConsultDetailActivity$InJavaScriptLocalObj {
#    public void showSource(java.lang.String);
#}
#-keep class com.atgc.mycs.activity.H5CourseDetailActivity$InJavaScriptLocalObj {
#    public void showSource(java.lang.String);
#}
################### 项目内混淆 end ###########################

################### 友盟混淆 start ###########################
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**

#-libraryjars libs/SocialSDK_QQZone_2.jar

-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**

-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}

-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

-keep public class [com.atgc.swwy].R$*{
    public static final int *;
}
################### 友盟混淆 end ###########################

################### pldroid-player start ###########################
-keep class com.pili.pldroid.player.** { *; }
-keep class tv.danmaku.ijk.media.player.** {*;}
################### pldroid-player end ###########################

################### 微信 start ###########################
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

################### 微信支付 ###########################
-dontwarn com.tencent.mm.**
-dontwarn com.tencent.wxop.stat.**
-keep class com.tencent.mm.** {*;}
-keep class com.tencent.wxop.stat.**{*;}
################### 微信 end ###########################

################### 支付宝 start###########################
#-libraryjars libs/alipaySdk-20160825.jar
#-keep class com.alipay.android.app.IAlixPay{*;}
#-keep class com.alipay.android.app.IAlixPay$Stub{*;}
#-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
#-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
#-keep class com.alipay.sdk.app.PayTask{ public *;}
#-keep class com.alipay.sdk.app.AuthTask{ public *;}
-dontwarn com.alipay.**
-dontwarn HttpUtils.HttpFetcher
-dontwarn com.ta.utdid2.**
-dontwarn com.ut.device.**
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.mobilesecuritysdk.*
-keep class com.ut.*
################### 支付宝 end###########################

#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#==================protobuf======================

################### okhttp start ###########################
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-dontwarn okio.**
################### okhttp end ###########################

################### apache 防混淆 start ###########################
-dontwarn org.apache.commons.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-dontnote org.apache.http.**
################### apache 防混淆 end ###########################

#-keep class com.atgc.mycs.webview.JavascriptInterFace{*;}

################### 高德地图 start ###########################
#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
#搜索
-keep   class com.amap.api.services.**{*;}
#2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}
#导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}
################### 高德地图 start ###########################

################### UIL相关 start ###########################
-keep class com.nostra13.universalimageloader.** { *; }
-dontwarn com.nostra13.universalimageloader.**
################### UIL相关 end ###########################

################### Glide start ###########################
#-keep class com.bumptech.glide.Glide { *; }
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser** {
#  **[]VALUES;
#    public *;
#}
#-dontwarn com.bumptech.glide.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
**[] $VALUES; public *;
}
################### Glide end ###########################

################### Picasso start ###########################
-keep class com.squareup.picasso.Picasso { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn com.squareup.picasso.**
################### Picasso end ###########################

################### xUtils-2.6.14 start ###########################
#-libraryjars libs/xUtils-2.6.14.jar
-keep class com.lidroid.xutils.** { *; }
-keep public class * extends com.lidroid.xutils.**
-keepattributes Signature
-keepattributes *Annotation*
-keep public interface com.lidroid.xutils.** {*;}
-dontwarn com.lidroid.xutils.**
-keepclasseswithmembers class com.jph.android.entity.** {
	<fields>;
	<methods>;
}
################### xUtils-2.6.14 end ###########################

####################zxing start#####################
#-libraryjars libs/zxing.jar
#-libraryjars libs/zxing_apply.jar
-keep class com.google.zxing.** {*;}
-dontwarn com.google.zxing.**
####################zxing end#####################

####################BASE64Decoder start##################
#-libraryjars libs/sun.misc.BASE64Decoder.jar
####################BASE64Decoder end##################

####################fastjson start##################
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
####################fastjson end##################

####################eventbus start##################
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
####################eventbus end##################


####################fresco start##################
-keep class com.facebook.fresco.** {*;}
-keep interface com.facebook.fresco.** {*;}
-keep enum com.facebook.fresco.** {*;}
####################fresco end##################

####################LRecyclerview end##################
#LRecyclerview_library
-dontwarn com.github.jdsjlzx.**
-keep class com.github.jdsjlzx.**{*;}
####################LRecyclerview end##################

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}
#-libraryjars libs/autobahn.jar
#-libraryjars libs/httpmime-4.1.3.jar
#-libraryjars libs/android-async-http-1.4.5.jar