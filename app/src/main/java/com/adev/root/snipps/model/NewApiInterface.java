package com.adev.root.snipps.model;

import com.adev.root.snipps.utils.NewBookUtil;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewApiInterface {

    @GET("volumes")
    Call<NewBookUtil> getBookDetails(@Query("q") String isbn);
}
