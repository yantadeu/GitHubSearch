package yan.githubsearch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import yan.githubsearch.model.Item;
import yan.githubsearch.ui.detail.DetailFragment;
import yan.githubsearch.ui.main.MainFragment;
import yan.githubsearch.ui.main.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        mViewModel.getItemSelecionado().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item item) {
                if (item != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, DetailFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }


}
