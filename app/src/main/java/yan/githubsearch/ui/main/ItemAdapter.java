package yan.githubsearch.ui.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import yan.githubsearch.R;
import yan.githubsearch.model.Issue;
import yan.githubsearch.model.Item;
import yan.githubsearch.model.PullRequest;

/**
 * Created by yan on 01/10/2018.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final OnItemClickListener listener;
    private List<Item> itens;

    public ItemAdapter(List<Item> itens, OnItemClickListener listener) {

        this.itens = itens;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        public void onItemClick(Item item);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View itemView = inflater.inflate(R.layout.row_item_list, parent, false);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        final Item item = itens.get(position);

        holder.tvNumber.setText(String.valueOf(item.number));
        holder.tvTitulo.setText(new StringBuilder().append(item.titulo).append("\n").toString());

        if (item instanceof Issue) {
            holder.tvTipo.setText("Issue");
        } else if(item instanceof PullRequest) {
            holder.tvTipo.setText("Pull Request");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return itens.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNumber;
        private TextView tvTitulo;
        private TextView tvTipo;

        ItemViewHolder(View itemView) {
            super(itemView);

            tvNumber = itemView.findViewById(R.id.tvRowNumero);
            tvTitulo = itemView.findViewById(R.id.tvRowTitulo);
            tvTipo = itemView.findViewById(R.id.tvRowTipo);
        }
    }
}
