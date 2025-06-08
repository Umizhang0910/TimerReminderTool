plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.16.0"
}

group = "com.umizhang"
version = "1.0.0"

repositories {
    mavenCentral()
}

// 配置IntelliJ插件
intellij {
    version.set("2023.3.4")  // 与您的IDEA版本一致
    type.set("IC")           // IC:社区版, IU:旗舰版

    // 添加插件依赖（可选）
    plugins.set(
        listOf(
            "com.intellij.java",  // Java语言支持
            "org.jetbrains.kotlin" // Kotlin支持（可选）
        )
    )
}


dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    // 设置Java版本
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    // 配置插件XML更新
    patchPluginXml {
        sinceBuild.set("231")    // 最低兼容版本
        untilBuild.set("241.*")  // 最高兼容版本
    }

    // 运行配置
    runIde {
        ideDir.set(file("D:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA 2023.3.4")) // 您的IDEA安装路径
    }

    test {
        useJUnitPlatform()
    }
}