package io.github.jyotimoykashyap.chatapp.api

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("X-Branch-Auth-Token" , "somevalue")
            .build()
        return chain.proceed(request)
    }
}