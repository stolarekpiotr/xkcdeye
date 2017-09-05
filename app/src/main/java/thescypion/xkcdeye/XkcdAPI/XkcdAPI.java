package thescypion.xkcdeye.XkcdAPI;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface XkcdAPI {

    @GET("{id}/info.0.json")
    Single<Comic> loadComic(@Path("id") Integer id);

}
