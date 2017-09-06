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
    private static final Integer MIN_COMIC_ID = 1;
    private static XkcdController instance;
    public Integer MAX_COMIC_ID = null;

    private XkcdAPI xkcdAPI;
    private ComicReceivedListener listener;

    private XkcdController() {
        Gson gson = createGson();
        Retrofit retrofit = createRetrofit(gson);
        setXkcdApi(retrofit);
    }

    public static XkcdController getInstance(ComicReceivedListener listener) {
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
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private void setXkcdApi(Retrofit retrofit) {
        xkcdAPI = retrofit.create(XkcdAPI.class);
    }

    public Disposable getComic(Integer id) {
        final Boolean isNewest = (id == NEWEST);
        Disposable disposable = null;
        if (isNewest || (id >= MIN_COMIC_ID && id <= MAX_COMIC_ID)) {
            Single<Comic> call = xkcdAPI.loadComic((isNewest) ? "" : id.toString());
            disposable = call
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Comic>() {
                        @Override
                        public void accept(Comic comic) throws Exception {
                            if (MAX_COMIC_ID == null && isNewest) {
                                MAX_COMIC_ID = comic.getNum();
                            }
                            listener.onComicReceived(comic, isNewest);
                        }
                    });
        }
        return disposable;
    }


}
