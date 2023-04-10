
version = "0.6"

dependencies {
    api(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))
    api("com.google.code.gson:gson:2.8.9") // patch for CVE-2022-25647
    api("com.squareup.okhttp3:logging-interceptor")
    api("com.squareup.okhttp3:okhttp-dnsoverhttps")
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
}