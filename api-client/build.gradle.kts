version = "0.11"

val retrofitVersion = "2.11.0"
val jacksonVersion = "2.17.0"

dependencies {
    api(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    api("com.squareup.okhttp3:logging-interceptor")
    api("com.squareup.okhttp3:okhttp-dnsoverhttps")
    api("com.squareup.retrofit2:retrofit:$retrofitVersion")
    api("com.squareup.retrofit2:adapter-rxjava3:$retrofitVersion")
    api("com.squareup.retrofit2:converter-jackson:$retrofitVersion")
    api("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
}