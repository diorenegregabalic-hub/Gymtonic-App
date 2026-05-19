package com.falyrion.gymtonicapp.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // KUNG EMULATOR IMONG GAMIT, KINI:
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    // KUNG TINUOD NGA CELLPHONE, ILISI ANG IP SAMA NIINI:
    // private static final String BASE_URL = "http://192.168.1.15:3000/";

    private static Retrofit retrofit = null;

    public static ApiClient getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiClient.class);
    }
}
