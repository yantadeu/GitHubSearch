package yan.githubsearch.ui.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import yan.githubsearch.R;
import yan.githubsearch.model.Issue;
import yan.githubsearch.model.Item;
import yan.githubsearch.model.PullRequest;
import yan.githubsearch.ui.main.MainViewModel;

/**
 * Created by yan on 01/10/2018.
 */
public class DetailFragment extends Fragment {

    private TextView mTvTipo;
    private TextView mTvNumero;
    private TextView mTvTitulo;
    private TextView mTvDescricao;

    private MainViewModel mViewModel;
    private Context context;


    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        context = container.getContext();

        return inflater.inflate(R.layout.detail_fragment, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvTipo = view.findViewById(R.id.tvTipo);
        mTvNumero = view.findViewById(R.id.tvNumero);
        mTvTitulo = view.findViewById(R.id.tvTitulo);
        mTvDescricao = view.findViewById(R.id.tvDescricao);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        mViewModel.getItemSelecionado().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item item) {
                if (item instanceof Issue) {
                    mTvTipo.setText("Issue");
                } else if(item instanceof PullRequest) {
                    mTvTipo.setText("Pull Request");
                }

                mTvNumero.setText(String.valueOf(item.number));
                mTvTitulo.setText(new StringBuilder().append(item.titulo).append("\n").toString());
                mTvDescricao.setText(item.descricao);

            }
        });
    }

    @Override
    public void onDestroyView() {

        SharedPreferences preferences =
                context.getSharedPreferences("yan.githubsearch", Context.MODE_PRIVATE);

        preferences.edit().putString("details", "1").apply();

        super.onDestroyView();
    }

}
