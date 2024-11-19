package com.example.test_project.viewHelper;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author: Albert Kaminski
 * Interceptor class to save the token
 **/
public class AuthInterceptor implements Interceptor {
    private UserSessionManager userSession;

    /**
     * Token called from current UserSession
     */
    public AuthInterceptor(Context context) {
        this.userSession = new UserSessionManager(context);
    }


    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder builder = request.newBuilder()
                .header("Authorization", this.userSession.getToken());

        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }

}
