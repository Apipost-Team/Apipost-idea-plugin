plugins {
  id("java")
  id("org.jetbrains.intellij") version "1.10.1"
}

group = "com.wwr"
version = "1.6-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation ("io.swagger.core.v3:swagger-models:2.2.6")
  implementation ("io.github.openfeign:feign-core:12.1")
  implementation ("io.github.openfeign:feign-gson:12.1")
  implementation ("io.github.openfeign.form:feign-form:3.8.0")
  implementation ("org.slf4j:slf4j-api:2.0.5")
  // lombok
  compileOnly ("org.projectlombok:lombok:1.18.24")
  annotationProcessor ("org.projectlombok:lombok:1.18.24")
  testCompileOnly ("org.projectlombok:lombok:1.18.24")
  testAnnotationProcessor ("org.projectlombok:lombok:1.18.24")
  testImplementation ("org.junit.jupiter:junit-jupiter-api:5.9.0")
  testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.9.0")

  implementation ("cn.hutool:hutool-http:5.8.15")
  implementation ("cn.hutool:hutool-json:5.8.15")

  implementation ("org.yaml:snakeyaml:1.29")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2021.3")
  type.set("IC") // Target IDE Platform
  sandboxDir.set("${project.rootDir}/.sandbox")
  updateSinceUntilBuild.set(false)
  plugins.set(listOf("java", "properties"))
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "11"
    targetCompatibility = "11"
  }

  patchPluginXml {
    sinceBuild.set("211")
    untilBuild.set("231.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}