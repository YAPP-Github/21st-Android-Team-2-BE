import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val querydslVersion: String by project
val springdocVersion: String by project
val kotestVersion: String by project
val mockkVersion: String by project
val jjwtVersion: String by project
val embeddedRedisVersion: String by project

plugins {
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    kotlin("kapt") version "1.7.10" //Querydsl
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0" //ktlint
}

group = "com.yapp"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    //querydsl
    implementation("com.querydsl:querydsl-jpa:$querydslVersion")
    kapt("com.querydsl:querydsl-apt:$querydslVersion:jpa")
    // swagger
    implementation("org.springdoc:springdoc-openapi-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")
    // security
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
    //h2
    testImplementation("com.h2database:h2")

    // kotest
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-extensions-spring:$kotestVersion") // @SpringBootTest 통합테스트에서 Kotest 레이아웃을 사용하기 위함

    // mockk
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.ninja-squad:springmockk:3.0.1")

    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("it.ozimov:embedded-redis:$embeddedRedisVersion")
    // jwt
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")
}

noArg {
    annotation("javax.persistence.Entity")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
