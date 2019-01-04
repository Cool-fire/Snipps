package com.adev.root.snipps.model;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewBookApiclient {

    private static Retrofit retrofit = null;
    private static String BASE_URL = "https://www.googleapis.com/books/v1/";

    public static Retrofit getClient()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
