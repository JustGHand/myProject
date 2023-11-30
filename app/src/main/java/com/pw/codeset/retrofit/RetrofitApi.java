package com.pw.codeset.retrofit;

import java.util.Observer;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;

public interface RetrofitApi {

    @GET("test")
    Observable<Response<String>> testApi();

}
