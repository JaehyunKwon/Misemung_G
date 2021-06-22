# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\user\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

# 광고 라이브러리 프로가드 제외
-keep public class com.fsn.cauly.** {
	 	   public protected *;
	}
-keep public class com.trid.tridad.** {
	  	  public protected *;
	}
-dontwarn android.webkit.**

-keepattributes *Annotation*

-libraryjars libs/adlibr.4.0.0.0.jar

-keepattributes EnclosingMethod

-keep class com.crashlytics.** { *; }
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

# GSon
-keep class com.google.gson.examples.android.model.** { *; }
# Glide
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#    **[] $VALUES;
#    public *;
#}

### OKHTTP3
-keep class okhttp3.** { *; }

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote okhttp3.internal.Platform

-dontwarn org.apache.http.**
-dontwarn udk.android.**
-dontwarn org.junit.**
-dontwarn android.test.**
-dontwarn com.squareup.okhttp.**
-dontwarn android.support.v4.app.**
-dontwarn net.lucode.hackware.**
