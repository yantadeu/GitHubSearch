package yan.githubsearch.ui.main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.DialogInterface;
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

    @SuppressLint("CheckResult")
    public void pesquisar(final String repoName, Context context) {
        loading.setValue(true);

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
                                    message.setValue(throwable.getMessage());
                                }
                            },
                            new Action() {
                                @Override
                                public void run() throws Exception {
                                    loading.setValue(false);
                                }
                            });
        }catch (Exception $e){
            new AlertDialog.Builder(context)
                    .setTitle("Erro")
                    .setMessage("Houve um erro! Confira a conexão com a internet e Tente novamente mais tarde")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("MainActivity", "Concordou");
                        }
                    })
                    .show();
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
