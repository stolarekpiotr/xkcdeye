package thescypion.xkcdeye.XkcdAPI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class XkcdController {

    public static final int NEWEST = -1;
    private static final String BASE_URL = "https://xkcd.com/";

    private XkcdAPI xkcdAPI;
    private ComicReceivedListener listener;

    public XkcdController(ComicReceivedListener listener) {
        this.listener = listener;
        Gson gson = createGson();
        Retrofit retrofit = createRetrofit(gson);
        setXkcdApi(retrofit);
    }

    private Gson createGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    private Retrofit createRetrofit(Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private void setXkcdApi(Retrofit retrofit) {
        xkcdAPI = retrofit.create(XkcdAPI.class);
    }

    public Disposable getComic(Integer id) {
        final Boolean isNewest = (id == NEWEST);
        Single<Comic> call = xkcdAPI.loadComic((isNewest) ? "" : id.toString());
        Disposable disposable = call
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Comic>() {
                    @Override
                    public void accept(Comic comic) throws Exception {
                        listener.onComicReceived(comic, isNewest);
                    }
                });
        return disposable;
    }
}
