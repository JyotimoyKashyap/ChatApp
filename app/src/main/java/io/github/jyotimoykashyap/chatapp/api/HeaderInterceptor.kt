package io.github.jyotimoykashyap.chatapp.api


import io.github.jyotimoykashyap.chatapp.util.SharedPref
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("X-Branch-Auth-Token" , SharedPref.readEntry(SharedPref.BRANCH_AUTH_TOKEN))
            .build()
        return chain.proceed(request)
    }
}