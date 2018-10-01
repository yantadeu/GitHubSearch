package yan.githubsearch.data;

import com.rx2androidnetworking.Rx2AndroidNetworking;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by yan on 30/09/2018.
 */
public class WebApiHelper {

    public static <T> Observable<List<T>> doGetRequest(String url, Class<T> type) {
        return Rx2AndroidNetworking
                .get(url)
                .build()
                .getObjectListObservable(type);
    }

}
