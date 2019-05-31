# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/liaojinlong/AndroidStudio/android-studio/sdk/tools/proguard/proguard-android.txt
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


     # for google protobuf
        -keep public class * extends com.google.protobuf.GeneratedMessage { *; }
        -keep class com.google.protobuf.** { *; }
        -keep public class * extends com.google.protobuf.** { *; }

     #for okhttp
        -keep class okio.** {*;}
        -dontwarn okio.**
        -keep class com.squareup.okhttp.** {*;}
        -dontwarn com.squareup.okhttp.**

        -keep class com.comsince.github.** { *; }
        -dontwarn com.comsince.github.**


        # App compat
        -keep class android.support.** {*;}
        -dontwarn android.support.**
