package yan.githubsearch.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import yan.githubsearch.R;
import yan.githubsearch.model.Item;

public class MainFragment extends Fragment implements ItemAdapter.OnItemClickListener {

    private EditText mEtPesquisa;
    private ImageButton mBtnPesquisar;
    private ProgressBar mProgressBar;
    private TextView mTvMensagem;

    private RecyclerView rvLista;

    private MainViewModel mViewModel;

    public AlertDialog myAlertDialog;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEtPesquisa = view.findViewById(R.id.etMainFragmentPesquisa);
        mBtnPesquisar = view.findViewById(R.id.btnMainFragmentPesquisar);
        mProgressBar = view.findViewById(R.id.pbRecyclerViewBaseLoading);
        mTvMensagem = view.findViewById(R.id.tvRecyclerViewBaseListaVazia);

        rvLista = view.findViewById(R.id.rvRecyclerViewBaseLista);


        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);

        rvLista.addItemDecoration(decoration);

        mBtnPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mEtPesquisa.getText().toString().equals("")){
                    mViewModel.pesquisar(mEtPesquisa.getText().toString(), mEtPesquisa.getContext());
                }else{
                    showDialog(mEtPesquisa.getContext(), "Atenção", "É necessário inserir um nome de repositório");
                }

            }
        });

        rvLista.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        mViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        mViewModel.getMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if ((s == null || s.isEmpty())) {
                    showDialog(mEtPesquisa.getContext(), "Atenção", "Não foram encontrados resultados com a pesquisa realizada!");

                } else {
                    mTvMensagem.setVisibility(View.VISIBLE);
                    mTvMensagem.setText(s);

                }
            }
        });

        mViewModel.getItens().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                ItemAdapter adapter = new ItemAdapter(items, MainFragment.this);

                rvLista.setAdapter(adapter);
            }
        });
    }



    public void showDialog(Context context, String title, String message) {

        SharedPreferences preferences =
                context.getSharedPreferences("yan.githubsearch", Context.MODE_PRIVATE);

        String details = preferences.getString("details", "0");

        if(!details.equals("1")){
            if( myAlertDialog != null && myAlertDialog.isShowing() ) return;

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.dismiss();
                }});
            builder.setCancelable(false);
            myAlertDialog = builder.create();
            myAlertDialog.show();
        }else{
            preferences.edit().putString("details", "0").apply();
        }

    }

    @Override
    public void onItemClick(Item item) {
        mViewModel.selecionarItem(item);
    }
}
