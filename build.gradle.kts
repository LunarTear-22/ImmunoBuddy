// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Your existing Android application plugin (assuming it's for the app module)
    alias(libs.plugins.android.application) apply false

    // Add the Google Services plugin here
    alias(libs.plugins.googleServices) apply false // Or use the string ID if not in version catalog
    // For example: id("com.google.gms.google-services") version "4.4.1" apply false
}