pluginManagement {
    repositories {
        google()
        mavenCentral()
        jcenter()  // Incluir JCenter para resolver las dependencias
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()  // Asegúrate de incluir JCenter aquí también
    }
}

rootProject.name = "Git_Practica"
include(":app")
