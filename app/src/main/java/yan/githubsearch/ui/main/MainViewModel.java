package yan.githubsearch.ui.main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import yan.githubsearch.data.ApiEndpoint;
import yan.githubsearch.data.WebApiHelper;
import yan.githubsearch.model.Issue;
import yan.githubsearch.model.Item;
import yan.githubsearch.model.PullRequest;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Boolean> loading;
    private MutableLiveData<String> message;

    private MutableLiveData<List<Item>> itens;
    private MutableLiveData<Item> itemSelecionado;

    private AlertDialog myErrorDialog;
    private Context myContext;

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @SuppressLint("CheckResult")
    public void pesquisar(final String repoName, Context context) {
        loading.setValue(true);
        myContext = context;
        if(isOnline()){
            try{
                Observable.zip(WebApiHelper
                                .doGetRequest(ApiEndpoint.getIssueEndpoint(repoName), Issue.class),
                        WebApiHelper
                                .doGetRequest(ApiEndpoint.getPullRequestEndpoint(repoName), PullRequest.class),
                        new BiFunction<List<Issue>, List<PullRequest>, List<Item>>() {
                            @Override
                            public List<Item> apply(List<Issue> issues, List<PullRequest> pulls) throws Exception {
                                ArrayList<Item> itens = new ArrayList<>();

                                itens.addAll(issues);
                                itens.addAll(pulls);

                                return itens;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Item>>() {
                                       @Override
                                       public void accept(List<Item> i) throws Exception {
                                           itens.setValue(i);
                                       }
                                   },
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        loading.setValue(false);
                                        showDialog(myContext, "Erro", "Confira sua conexão com a internet");
                                        //message.setValue(throwable.getMessage());
                                    }
                                },
                                new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        loading.setValue(false);
                                    }
                                });
            }catch (Exception $e){
                showDialog(myContext, "Erro", "Houve um erro! Confira a conexão com a internet e Tente novamente mais tarde");
            }
        }else{
            showDialog(myContext, "Erro", "Confira sua conexão com a internet");
        }



    }

    private void showDialog(Context context, String title, String message) {

        SharedPreferences preferences =
                context.getSharedPreferences("yan.githubsearch", Context.MODE_PRIVATE);

        String details = preferences.getString("details", "0");

        if(!details.equals("1")){
            if( myErrorDialog != null && myErrorDialog.isShowing() ) return;

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.dismiss();
                }});
            builder.setCancelable(false);
            myErrorDialog = builder.create();
            myErrorDialog.show();
        }else{
            preferences.edit().putString("details", "0").apply();
        }

    }

    public void selecionarItem(Item item) {
        itemSelecionado.setValue(item);
    }


    public LiveData<List<Item>> getItens() {
        if (itens == null) {
            itens = new MutableLiveData<>();
        }

        return itens;
    }

    public LiveData<Boolean> isLoading() {
        if (loading == null) {
            loading = new MutableLiveData<>();
        }

        return loading;
    }

    public LiveData<String> getMessage() {
        if (message == null) {
            message = new MutableLiveData<>();
        }

        return message;
    }

    public LiveData<Item> getItemSelecionado() {
        if (itemSelecionado == null) {
            itemSelecionado = new MutableLiveData<>();
        }

        return itemSelecionado;
    }
}
