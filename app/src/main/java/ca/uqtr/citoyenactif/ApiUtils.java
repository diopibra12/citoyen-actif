package ca.uqtr.citoyenactif;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {

    private static final Gson GSON = new Gson();
    private static Retrofit retrofit = null;

    public static Retrofit getAPIService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl("http://citoyenactif.glitch.me").addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

}
