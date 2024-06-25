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

-dontwarn com.facebook.infer.annotation.Nullsafe$Mode
-dontwarn com.facebook.infer.annotation.Nullsafe

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Specifies the compression level of the code
-optimizationpasses 5

# Include unmixed case
-dontusemixedcaseclassnames

# Do not ignore non-public library classes
-dontskipnonpubliclibraryclasses

# Whether to log when confusing
-verbose

# Algorithms used for obfuscation
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Specifies to print any warnings about unresolved references and other important problems,
# but to continue processing in any case.
-ignorewarnings

# Keep native methods from being obfuscated
-keepclasseswithmembernames class * {
    native <methods>;
}

####  <START> Keep custom control classes from being obfuscated   ####
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
####  <END>   ####

# Keep enum classes from being obfuscated
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

# Achartengine
-dontwarn org.achartengine.**
-keep class org.achartengine.** { *;}

# Keep Serializable from being confused
-keep class * implements java.io.Serializable { *; }
-keepnames class * implements java.io.Serializable

####  <START> HTTP & Retrofit   ####
#async http
-keep class cz.msebera.android.httpclient.** { *; }
-keep class com.loopj.android.http.** { *; }

#com.squareup.retrofit2:retrofit:2.3.0
-keepattributes Signature
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# OkHttp
-keepattributes Signature
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

#okio
-dontwarn okio.**

-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
####  <END>   ####

# For in-app purchase lib
-keep class com.android.vending.billing.**

####    <START> Ads Related   ####
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# For Google Ads
-keep public class com.google.android.gms.ads.** {
   public *;
}

##    <START> Iron Source Ads Related   ##
# unity
-keep class com.ironsource.unity.androidbridge.** { *;}
-keep class com.google.android.gms.ads.** {public *;}
-keep class com.google.android.gms.appset.** { *; }
-keep class com.google.android.gms.tasks.** { *; }

# adapters
-keep class com.ironsource.adapters.** { *; }

# sdk
-dontwarn com.ironsource.**
-dontwarn com.ironsource.adapters.**
-keepclassmembers class com.ironsource.** { public *; }
-keep public class com.ironsource.**
-keep class com.ironsource.adapters.** { *; }

# omid
-dontwarn com.iab.omid.**
-keep class com.iab.omid.** {*;}

# javascript
-keepattributes JavascriptInterface
-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }
## <END>   ##
#### <END>   ####

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Gson TypeToken
# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default, keep it.
# This is also needed for R8 in compat mode since multiple
-keepattributes Signature
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
# Optional. For using GSON @Expose annotation
-keepattributes AnnotationDefault,RuntimeVisibleAnnotations




# fOR Agora SDK
-keep class io.agora.**{*;}

################################################
######    End Part Of All Progard Rules   ######
################################################

####    <START> Remove Log   ####
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
}
#### <END>   ####

####  <START> Record the generated log data, which is output in the root directory of this project when gradle builds ####
# The internal structure of all classes in the apk package
###     -dump proguard/class_files.txt
# Unobfuscated classes and members
###     -printseeds proguard/seeds.txt
# List code removed from apk
###     -printusage proguard/unused.txt
# Mapping before and after obfuscation
###     -printmapping proguard/mapping.txt
#### <END>   ####


# Cpoy From WordConnect
-verbose

-dontwarn android.support.**
-dontwarn com.badlogic.gdx.backends.android.AndroidFragmentApplication
-dontwarn com.badlogic.gdx.utils.GdxBuild
-dontwarn com.badlogic.gdx.physics.box2d.utils.Box2DBuild
-dontwarn com.badlogic.gdx.jnigen.BuildTarget*
-dontwarn com.badlogic.gdx.graphics.g2d.freetype.FreetypeBuild

-keep class com.badlogic.gdx.controllers.android.AndroidControllers

-keepclassmembers class com.badlogic.gdx.backends.android.AndroidInput* {
   <init>(com.badlogic.gdx.Application, android.content.Context, java.lang.Object, com.badlogic.gdx.backends.android.AndroidApplicationConfiguration);
}

-keepclassmembers class com.badlogic.gdx.physics.box2d.World {
   boolean contactFilter(long, long);
   void    beginContact(long);
   void    endContact(long);
   void    preSolve(long, long);
   void    postSolve(long, long);
   boolean reportFixture(long);
   float   reportRayFixture(long, float, float, float, float, float);
}




###########################################################################################################

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

-dontwarn com.facebook.infer.annotation.Nullsafe$Mode
-dontwarn com.facebook.infer.annotation.Nullsafe


-renamesourcefileattribute SourceFile

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-ignorewarnings

#async http
-keep class cz.msebera.android.httpclient.** { *; }
-keep class com.loopj.android.http.** { *; }


#com.squareup.retrofit2:retrofit:2.3.0
-keepattributes Signature
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# OkHttp
-keepattributes Signature
#-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

#okio
-dontwarn okio.**

-dontwarn retrofit2.**

-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }

#For in-app purchase lib
-keep class com.android.vending.billing.**

#For Ads
#-keepclassmembers class * implements android.os.Parcelable {
#    public static final android.os.Parcelable$Creator *;
#}
-keep public class com.google.android.gms.ads.** {
   public *;
}

#Keep native methods from being obfuscated
-keepclasseswithmembernames class * {
    native <methods>;
}

#Keep custom control classes from being obfuscated
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#Keep custom control classes from being obfuscated
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#Keep enum classes from being obfuscated
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

#Remove Log
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
}

#Achartengine
-dontwarn org.achartengine.**
-keep class org.achartengine.** { *;}

-keep class * implements java.io.Serializable { *; }
-keepnames class * implements java.io.Serializable

#-------------------------------------------------------------------------------

-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
#unity
-keep class com.ironsource.unity.androidbridge.** { *;}
-keep class com.google.android.gms.ads.** {public *;}
-keep class com.google.android.gms.appset.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
#adapters
-keep class com.ironsource.adapters.** { *; }
#sdk
-dontwarn com.ironsource.**
-dontwarn com.ironsource.adapters.**
-keepclassmembers class com.ironsource.** { public *; }
-keep public class com.ironsource.**
-keep class com.ironsource.adapters.** { *;
}
#omid
-dontwarn com.iab.omid.**
-keep class com.iab.omid.** {*;}
#javascript
-keepattributes JavascriptInterface
-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }

# Gson TypeToken
# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default, keep it.
# This is also needed for R8 in compat mode since multiple
-keepattributes Signature
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
# Optional. For using GSON @Expose annotation
-keepattributes AnnotationDefault,RuntimeVisibleAnnotations


####    //<editor-fold desc="For TopOn SDK">
-keep public class com.anythink.**
-keepclassmembers class com.anythink.** {
   *;
}

-keep public class com.anythink.network.**
-keepclassmembers class com.anythink.network.** {
   public *;
}

-dontwarn com.anythink.hb.**
-keep class com.anythink.hb.**{ *;}

-dontwarn com.anythink.china.api.**
-keep class com.anythink.china.api.**{ *;}

# new in v5.6.6
-keep class com.anythink.myoffer.ui.**{ *;}
-keepclassmembers public class com.anythink.myoffer.ui.** {
   public *;
}
####    //</editor-fold>

-keepattributes SourceFile,LineNumberTable
 -keepattributes JavascriptInterface
 -keep class android.webkit.JavascriptInterface {
     *;
 }
 -keep class com.unity3d.** {
     *;
 }