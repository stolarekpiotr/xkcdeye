package thescypion.xkcdeye.XkcdAPI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class XkcdController {

    static final String BASE_URL = "https://xkcd.com/";

    public Single<Comic> getComic(Integer id) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        XkcdAPI xkcdAPI = retrofit.create(XkcdAPI.class);

        return xkcdAPI.loadComic(id);
    }
}
