pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/central/")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://maven.aliyun.com/repository/central/")
        maven("https://maven.aliyun.com/repository/jcenter/")
        maven("https://maven.aliyun.com/repository/google/")
        google()
        mavenCentral()
    }
}

rootProject.name = "OddSliderAndroid"
include(":app")
