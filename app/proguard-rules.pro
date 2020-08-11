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



#-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-ignorewarnings
-dontpreverify
-verbose
#-applymapping qqlive_proguard_mapping.txt
-dontoptimize
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#-optimizations method/inlining/*

#-allowaccessmodification
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes *JavascriptInterface*

#-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
#-repackageclasses

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
#-keep public class com.android.vending.licensing.ILicensingService
#-dontnote com.android.vending.licensing.ILicensingService
#-dontwarn cn.com.iresearch.mapptracker.**

-keep class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static long serialVersionUID;
    static java.io.ObjectStreamField[] serialPersistentFields;
    void writeObject(java.io.ObjectOutputStream);
    void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class **.R$* {
	public static <fields>;
}

-keep class sun.misc.Unsafe {*;}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
	 *;
}

-keep class * implements android.support.v4.view.ViewPager.OnPageChangeListener {

}

-keepclassmembers class ** {
    public void on*Event(...);
}

-keepclassmembers class * {
    public boolean onShowFileChooser(*);
}

-dontwarn  org.eclipse.jdt.annotation.**

-dontnote ct.**


#======   以下为app中定义的类或者第三方库中用到的类 (ashercai 2014-8-8)  ============================
#         规则：
#         1) 第三方库，如果已经混淆过，建议全部保留
#         2) 第三方库，如果包含动态库，建议全部保留
#         3) App的类，如果用到动态库，建议保留包 (如果明确动态库中没有创建Java对象或访问Java类成员，可混淆)
#         4) App的类，如果用到了反射，需检查代码，将涉及的类和成员保留
#         5) App的类，定义为@JavascriptInterface的成员，需要保留
#=============================================================================================

-keepclassmembers class com.tencent.qqlive.ona.browser.WebAppInterface { public <methods>; }
-keepclassmembers class com.tencent.qqlive.ona.unicom.js.WebJsInterface { public <methods>; }

-keep class com.intertrust.wasabi.** {*;}

-keep class com.tencent.qqlive.ona.appconfig.AppConfig** {*; }
-keep class TencentVideo.Foo {*;}
-keep class com.tencent.update.IUpdateFacade {*;}
-keep class com.tencent.qqlive.jni.VerifyTS {*;}
-keep class com.tencent.dexloader.DexLoader {*;}
-keep class log.LogReport** {*;}
-keep class pi.** {*;}
-keep class oicq.** {*;}
-keep class com.tencent.feedback.** {*;}
-keep class com.tencent.stat.** {*;}
-keep class com.tencent.tad.** {*;}
-keep class com.tencent.nonp2pproxy.** {*;}
-keep class com.tencent.p2pproxy.** {*;}
-keep class com.tencent.httpproxy.** {*;}
-keep class cn.com.iresearch.** {*;}
-keep class com.tencent.smtt.** {*;}
-keep class com.tencent.mm.** {*;}
-keep class com.tenpay.** {*;}
-keep class tencent.com.cftutils.** {*;}
-keep class com.pay.** {*;}
-keep class com.tencent.midas.** {*;}
-keep class com.demon.** {*;}
-keep class vspi.** {*;}
-keep class com.paylogin.sdk.** {*;}
-keep class com.tencent.tmassistantsdk.** {*;}
-keep class com.tencent.tmassistantagentsdk.** {*;}
-keep class com.tencent.apkupdate.** {*;}
-keep class org.cybergarage.** {*;}
-keep class com.tencent.qqlive.ck.**{*;}
-keep class com.tencent.qqlivekid.search.smartbox.**{*;}

#tvk播放器组件相关的接口不能混淆
-keep class com.tencent.qqlive.downloadproxy.tvkp2pproxy.** {*;}
-keep class com.tencent.qqlive.downloadproxy.tvkhttpproxy.** {*;}
-keep class com.tencent.qqlive.ck.**{*;}
-keep interface com.tencent.qqlive.multimedia.tvkplayer.api.** { *; }
-keep interface com.tencent.qqlive.multimedia.tvkcommon.api.** { *; }
-keep class com.tencent.qqlive.multimedia.tvkcommon.baseinfo.** { *; }
-keep class com.tencent.qqlive.multimedia.tvkcommon.utils.** {
    public *;
}
-keep interface com.tencent.qqlive.multimedia.tvkeditor.record.api.** { *; }
-keep class com.tencent.qqlive.multimedia.TVKSDKMgr {*;}
-keep class com.tencent.qqlive.multimedia.tvkplayer.api.** {*;}
-keep class com.tencent.qqlive.multimedia.tvkplayer.player.self.playernative.** {*;}
-keep class com.tencent.qqlive.multimedia.tvkplayer.player.self.tvsubtitlenative** {*; }
-keep class com.tencent.qqlive.multimedia.tvkplayer.proxy.proxynative.** {*;}
-keep class com.tencent.qqlive.multimedia.tvkeditor.record.** {*;}
-keep class com.tencent.qqlive.multimedia.tvkeditor.composition.** {*;}
-keep class com.tencent.qqlive.multimedia.tvkeditor.mediaedit.encodernative.** {*;}
-keep class com.tencent.qqlive.multimedia.tvkeditor.mediaedit.transcodernative.** {*;}
-keep class com.tencent.qqlive.multimedia.tvkeditor.mediaedit.combinernative.**{*;}
-keep class com.tencent.qqlive.multimedia.tvkeditor.mediaedit.helpernative.** {*;}
-keep class com.tencent.qqlive.multimedia.tvkeditor.record.encode.TextureRenderNative {*;}
-keep class com.tencent.qqlive.multimedia.tvkmonet.api.** {*;}
-keep class com.tencent.qqlive.multimedia.tvkmonet.monetprocess.processnative.** {*;}
-keep class com.tencent.qqlive.multimedia.tvkcommon.wrapper.TVKMediaPlayerFactory {*;}
-keep class com.tencent.qqlive.multimedia.tvkcommon.wrapper.TVKSDKMgrFactory {*;}
-keep class com.tencent.qqlive.multimedia.tvkcommon.config.TVKMediaPlayerConfig$PlayerConfig {*;}
-keep class com.tencent.qqlive.multimedia.tvkcommon.config.TVKMediaPlayerConfig$AdConfig {*;}
-keep class com.tencent.qqlive.multimedia.tvkcommon.config.TVKConfigField {*;}
-keep class com.tencent.qqlive.multimedia.tvkcommon.config.TVKCommParams { public <methods>; }
-keep class com.tencent.qqlive.multimedia.tvkcommon.sdkupdate.TVKSDKLocalConfig {*;}
-keep class com.tencent.qqlive.multimedia.tvkcommon.dex.**{
    public ** get*();
}
-keep class com.tencent.qqlive.multimedia.tvkcommon.thirdparties.** { public *; }
-keep class com.tencent.qqlive.multimedia.tvkplayer.logic.** { public *; }
-keep class com.tencent.qqlive.multimedia.tvkplayer.vodcgi.** { public *; }
-keep class com.tencent.qqlive.multimedia.tvkplayer.cgilogic.** { public *; }


-keep class com.tencent.qqlive.ona.utils.AppUtils { public <methods>; }
-keep class com.tencent.qqlivekid.view.onaview.** {*;}
-keep class com.tencent.qqlivekid.protocol.jce.** {*;}
-keep class com.tencent.qqlive.ona.protocol.jce.** {*;}
-keep class com.tencent.qqlive.projection.sdk.jce.** {*;}
-keep class com.tencent.qqlivekid.protocol.ProtocolPackage {*;}
-keep class com.tencent.qqlivekid.player.view.** {*;}
-keep class com.tencent.qqlivekid.player.view.PlayerAnimationView** {*;}
-keep class com.tencent.qqlivekid.player.view.controller.PlayerAnimationController** {*;}
-keep class com.tencent.qqlive.ona.logreport.** {*;}
-keep class com.tencent.qqlivekid.base.log.AppLaunchReporter** {*;}
-keep class com.tencent.tad.download.TadDownloadManager {public <methods>;}
-keep class com.tencent.ads.download.** {*;}
-keep class com.tencent.ads.mraid.** {*;}
-keep class com.tencent.ads.view.AdPage$* {*;}
-keep class com.tencent.qqlive.ona.player.apollo.** {*;}
-keep class com.tencent.apollo.** {*;}
-keepclassmembers class * extends android.webkit.WebChromeClient {public void openFileChooser(...);}
-keepclassmembers class com.tencent.ads.js.AdJsBridge { public <methods>; }
-keep class com.tencent.qqlive.jsapi.api.** {*;}
-keep class com.tencent.qqlive.jsapi.webview.** {*;}
-keep class com.tencent.qqlive.jsapi.webclient.** {*;}
-keep class com.tencent.qqlive.webapp.hollywood.HollywoodInteractJSApi** {*;}
-keep class com.tencent.qqlive.qqvideocmd.** {*;}
#小米推送服务的receive
-keep class com.tencent.qqlive.services.push.XiaoMiPushMessageReceiver {*;}
-keep class com.facebook.** {*;}
-keep class com.squareup.** {*;}
-keep class com.tencent.mobileqq.memoryleak.LeakInspector {*;}
-keep class com.tencent.h.HManager {*;}
-keep class com.huawei.**{*;}
-keep class com.baidu.mapapi.**{*;}
-keep class com.hianalytics.android.**{*;}
-keep class android.support.v7.widget.**{*;}

#Analytic的协议包,用于上报的数据结构
-keep public class * extends com.qq.taf.jce.JceStruct{*;}

#wup的协议包：
-keep public class com.qq.jce.*{
public * ;
protected * ;
}

#native 回调接口
-keep public interface com.tencent.feedback.eup.jni.NativeExceptionHandler{
*;
}
-keep public class com.tencent.feedback.eup.jni.NativeExceptionUpload{
*;
}

#bugly RDM热更新
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-keep class com.tencent.qqlivekid.base.QQLiveKidApplication {
*;
}

#小手指
-keep public class com.tencent.qqlivekid.videodetail.FingerVideoDetailActivity{*;}
-keep public class com.tencent.qqlivekid.utils.manager.FingerGameConfigModel{*;}
-keep class com.tencent.qqlivekid.player.Player {*;}
-keep class com.tencent.qqlivekid.model.finger.**{*;}
-keep class com.tencent.qqlivekid.model.**{*;}
-keep class org.cocos2dx.lua.**{*;}
-keep class com.chukong.cocosplay.client.CocosPlayClient {*;}
-keep class org.cocos2dx.lib.**{*;}
-keep class org.cocos2dx.utils.**{*;}

-keep class com.tencent.qqlivekid.theme.**{*;}

-keep class com.tencent.qqlivekid.finger.work.**{*;}
-keep class com.tencent.qqlivekid.finger.game.**{*;}
-keep class com.tencent.qqlivekid.finger.join.**{*;}
-keep class com.tencent.qqlivekid.finger.share.**{*;}
-keep class com.tencent.qqlivekid.finger.sound.**{*;}
-keep class com.tencent.qqlivekid.finger.gamework.**{*;}

-keep class android.support.**{*;}

-keep class  com.ktcp.transmissionsdk.internal.InternalCmd**{*;}
-keep class  com.ktcp.transmissionsdk.utils.Constants**{*;}
-keep class  com.ktcp.remotedevicehelp.sdk.enternal.** {*;}
-keep class  com.ktcp.transmissionsdk.api.** {*;}


-keep class com.ktcp.interactcomp.** {*;}
-keep class com.ktcp.** {*;}



-keep public class com.tencent.qqlivekid.activity.HomeInfo {
*;
}

-keep class com.tencent.qqlive.dlna.data.** {*;}

-keep class com.tencent.qqlivekid.finger.share.WXShareQRCodeData {*;}

-keep class * extends com.tencent.qqlivekid.theme.DataBase {
*;
}

-keep class com.tencent.mm.sdk.** { *; }
-keep class com.tencent.mm.opensdk.** { *; }
-keep class com.alipay.sdk.** { *; }
-keep class com.tencent.mobileqq.openpay.** { *; }
-keep class com.tencent.smtt.** { *; }
-keep class com.migu.sdk.** { *; }
-keep class com.tencent.tauth.** { *; }
-keep class com.tencent.midas.** { *; }
-keep class com.pay.** { *; }
-keep class * extends android.app.Dialog { *; }
-keep class com.tencent.qqgame.client.scene.model.** { *; }
-keep class com.tencent.qqlivekid.finger.gameloading.GameLoadingData.**{*;}
-keep class org.cocos2dx.javascript.**{*;}
#-keep class com.tencent.qqlivekid.jsgame.**{*;}


#手管王卡SDK
-keep class tmsdk.common.** {
    *;
}
-keep class kingcardsdk.common.** {
    *;
}
-keep public class * extends kingcardsdk.common.gourd.ActionI {
    public <fields>;
    public <methods>;
}
-keep class com.qq.taf.jce.** {
    *;
}
-keep public class com.tencent.tmsdual.l.Tlm {
   private static long de(java.lang.String, java.security.interfaces.RSAPublicKey);
   private static byte[] dd(byte[], byte[]);
}

-dontwarn android.support.**
-dontwarn kcsdkint.**
-dontwarn tmsdk.common.**
-dontwarn dualsim.**
-dontwarn android.content.**
-dontwarn com.android.**







-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
    public static int i(...);
}

-keep class com.googlecode.** { *; }

# Gson库需要的混淆配置
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard

# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

##---------------End: proguard configuration for Gson  ----------

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

##---------------End: proguard configuration for Gson  ----------


# 消除语音SDK的警告
-dontwarn com.tvnetcomm.**
-dontwarn com.tencent.qqlive.**
-dontwarn com.ktcp.tencent.**
-dontwarn com.ktcp.tvagent.**
-dontwarn com.ktcp.aiagent.**
-dontwarn okio.**

# 保持语音SDK的必要类不被混淆
-keep class com.tencent.ai.speech.** { *; }
-keep class com.ktcp.tvagent.voice.recognizer.AsrSpeechRecognizer { *; }
-keep class com.ktcp.tvagent.voice.recognizer.AilabRecognizerError { *; }
# 保持灯塔SDK
-keep class com.tencent.beacon.** { *; }

-keep class com.tencent.qqlivekid.finger.model.** {*;}

#if条件会反射调用MTAReport类里的数据
-keep class com.tencent.qqlivekid.base.log.MTAReport {*;}

-keep class com.tencent.qqlivekid.utils.bean.** {*;}

#if条件反射调用XQEData里的数据
-keep class com.tencent.qqlivekid.config.model.xqe.XQEData {*;}
-keep class com.tencent.qqlivekid.config.model.xqe.BR {*;}
-keep class com.tencent.qqlivekid.home.daily.DailyRecommend {*;}

#qapm
-keep class com.tencent.qapmsdk.**{*;}


-keepclassmembers class ** {
    public void on*Event(...);
}
-keep class c.t.**{*;}
-keep class com.tencent.map.geolocation.**{*;}


-dontwarn  org.eclipse.jdt.annotation.**
-dontwarn  c.t.**

-keep class com.tencent.qqlivekid.topic.protocol.**

#vip会员信息
-keep class trpc.kidsvip.getvipinfo.** { *; }
-keep class trpc.kidsvip.getvipinfo.**

-keep class com.tencent.qqlive.route.** {*;}

-keep class com.tencent.qqlive.protocol.pb.** {*;}

-keep class com.tencent.qqlive.qqlivelog.**{*;}
-keep class com.tencent.qqlive.action.**{*;}
-keep class com.tencent.qqlive.modules.login.**{*;}
-keep class com.tencent.qqlive.log.**{*;}
-keep class com.tencent.mars.xlog.**{*;}
-keep class com.tencent.qqlive.report.**{*;}


#ivsdk
-keep class com.google.**{*;}
-keep class com.ctrlvideo.**{*;}

