package com.example.jimenez.appmunitacna;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class categoriaAdapter extends RecyclerView.Adapter<categoriaAdapter.ViewHolder> {


    private List<Categoria> categorias;
    private Context context;
    private onItemClickListener listener;

    public categoriaAdapter(List<Categoria> categorias, onItemClickListener listener) {
        this.categorias = categorias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria, parent, false);
        this.context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Categoria categoria = categorias.get(position);

        holder.setListener(categoria, listener);
        holder.tvNombreCategoria.setText(categoria.getNombre());
        holder.imgFotoCategoria.setImageResource(categoria.getImagen());


    }

    @Override
    public int getItemCount() {
        return this.categorias.size();
    }

    public void add(Categoria categoria){
        if (!categorias.contains(categoria)) {
            categorias.add(categoria);
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgFotoCategoria)
        AppCompatImageView imgFotoCategoria;
        @BindView(R.id.tvNombreCategoria)
        AppCompatTextView tvNombreCategoria;

        @BindView(R.id.containerMain)
        RelativeLayout containerMain;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setListener(final Categoria categoria, final onItemClickListener listener) {
            containerMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(categoria);
                }
            });
            containerMain.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongItemClick(categoria);
                    return true;
                }
            });
        }

    }
}
