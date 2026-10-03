# retain these to support class references for the bundling and translation systems
-keepnames class pd.** { *; }
-keepnames class render.** { *; }

# keep classes that are instantiated via reflection
-keep class * extends render.noosa.Gizmo { *; }
-keep class * extends render.glscripts.Script { *; }
# 注意 Bundlable 的实际包名是 render.utils.serialize（不是 render.utils）。
# 之前这里写漏了 .serialize，导致这条规则从未匹配到任何类 ——
# 而 Actor、Room、Item、Level 等全都 implements Bundlable，
# 于是它们的子类被 R8 删掉了反射构造所需的成员，
# 进入关卡时 Buff.affect()/房间创建 全部 NPE 闪退（release 包专属，debug 不混淆所以正常）。
-keep class * implements render.utils.serialize.Bundlable { *; }

# retained to support meaningful stack traces
# note that the mapping file must be referenced in order to make sense of line numbers
# mapping file can be found in core/build/outputs/mapping after running a release build
-keepattributes SourceFile,LineNumberTable

# libGDX stuff
-dontwarn android.support.**
-dontwarn com.badlogic.gdx.backends.android.AndroidFragmentApplication
-dontwarn com.badlogic.gdx.utils.GdxBuild
-dontwarn com.badlogic.gdx.physics.box2d.utils.Box2DBuild
-dontwarn com.badlogic.gdx.jnigen.BuildTarget*

# needed for libGDX skin reflection used in text fields. Perhaps just don't use skin?
-keep class com.badlogic.gdx.graphics.Color { *; }
-keep class com.badlogic.gdx.scenes.scene2d.ui.TextField$TextFieldStyle { *; }
-keepnames class com.badlogic.gdx.scenes.scene2d.ui.TextField { *; }

# needed for libGDX controllers
-keep class com.badlogic.gdx.controllers.android.AndroidControllers { *; }

-keepclassmembers class com.badlogic.gdx.backends.android.AndroidInput* {
    <init>(com.badlogic.gdx.Application, android.content.Context, java.lang.Object, com.badlogic.gdx.backends.android.AndroidApplicationConfiguration);
}