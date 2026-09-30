# kotlinx-serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class dk.nenoling.dapl.** {
    *** Companion;
}
-keepclasseswithmembers class dk.nenoling.dapl.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class dk.nenoling.dapl.**$$serializer { *; }
