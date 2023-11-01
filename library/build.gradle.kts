plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
}

android {
    namespace = "ir.afraapps.form"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    lint {
        // abortOnError false
    }

    compileOptions {
        // coreLibraryDesugaringEnabled true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "ir.afraapps"
                artifactId = "form-builder"
                version = rootProject.extra.get("appVersionName") as String

                pom {
                    name.set(project.name)
                    description.set("A form builder for android")
                    url.set("https://github.com/sobhan-jabbari/${project.name}")
                    /*properties = [
                            myProp: "value",
                            "prop.with.dots": "anotherValue"
                    ]*/
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("sobhan-jabbari")
                            name.set("Ali Jabbari")
                            email.set("sobhan.jabbari@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:github.com/sobhan-jabbari/${project.name}.git")
                        // developerConnection.set("scm:git:ssh://example.com/my-library.git")
                        url.set("https://github.com/sobhan-jabbari/${project.name}")
                    }
                }
            }

        }

        repositories {
            maven {
                name = "afraapps"
                url = uri("${project.layout.buildDirectory}/afraapps")
            }
        }
    }
}


dependencies {

    implementation("androidx.core:core-ktx:1.12.0")

    val appcompat_version = "1.6.1"
    implementation("androidx.appcompat:appcompat:$appcompat_version")
    implementation("androidx.appcompat:appcompat-resources:$appcompat_version")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.10.0")

    implementation("com.github.sobhan-jabbari:jcalendar:1.2.7")
    implementation("com.github.sobhan-jabbari:gviews:1.1.26")
    implementation("com.github.sobhan-jabbari:number-picker:1.1.26")
}
