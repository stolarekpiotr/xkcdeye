package thescypion.xkcdeye.XkcdAPI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class XkcdController {
    private static final String BASE_URL = "https://xkcd.com/";
    private static final Integer MIN_COMIC_ID = 1;
    private static XkcdController instance;
    public Integer MAX_COMIC_ID = 0;

    private XkcdAPI xkcdAPI;
    private Consumer<Comic> listener;

    private XkcdController() {
        Gson gson = createGson();
        Retrofit retrofit = createRetrofit(gson);
        setXkcdApi(retrofit);
        getNewestComic();
    }

    public static XkcdController getInstance(Consumer<Comic> listener) {
        if (instance == null) {
            instance = new XkcdController();
        }
        instance.listener = listener;
        return instance;
    }

    private Gson createGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    private Retrofit createRetrofit(Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private OkHttpClient getHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }

    private void setXkcdApi(Retrofit retrofit) {
        xkcdAPI = retrofit.create(XkcdAPI.class);
    }

    private void getNewestComic() {
        Single<Comic> call = xkcdAPI.loadNewestComic();
        call
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Comic>() {
                    @Override
                    public void accept(Comic comic) throws Exception {
                        listener.accept(comic);
                        MAX_COMIC_ID = comic.getNum();
                    }
                }, createThrowableConsumer());
    }

    public Disposable getComic(Integer id) {
        Disposable disposable = null;
        if (idInRange(id)) {
            Single<Comic> call = xkcdAPI.loadComic(id.toString());
            disposable = call
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listener, createThrowableConsumer());
        }
        return disposable;
    }

    private boolean idInRange(Integer id) {
        return (id >= MIN_COMIC_ID && id <= MAX_COMIC_ID);
    }

    private Consumer<Throwable> createThrowableConsumer() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        };
    }
}
