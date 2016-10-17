package br.com.aprendendo.vitor.marvel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.com.aprendendo.vitor.marvel.model.Personagem;

/**
 * Created by vitor on 18/09/2016.
 */
public class PersonagemAdapter extends RecyclerView.Adapter<PersonagemAdapter.VH> {

    private List<Personagem> mPersonagens;
    private Context mContext;
    private OnPersonagemClickListener mOnPersonagemClickListener;

    public void setOnPersonagemClickListener(OnPersonagemClickListener onPersonagemClickListener) {
        mOnPersonagemClickListener = onPersonagemClickListener;
    }

    public PersonagemAdapter(Context context, List<Personagem> personagens) {
        this.mContext = context;
        this.mPersonagens = personagens;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_personagem_layout, parent, false);
        final VH viewHolder = new VH(view);
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int pos = viewHolder.getAdapterPosition();
                if (mOnPersonagemClickListener != null){
                    mOnPersonagemClickListener.onPersonagemClick(mPersonagens.get(pos), pos);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Personagem personagem = mPersonagens.get(position);

        Glide.with(mContext)
                .load(personagem.getImagem().getPath() + "." + personagem.getImagem().getExtension())
                .placeholder(R.drawable.ic_empty_photo)
                .into(holder.imageViewPersonagem);
        holder.textViewNome.setText(personagem.getNome());

    }

    @Override
    public int getItemCount() {
        return mPersonagens.size();
    }

    class VH extends RecyclerView.ViewHolder {

        ImageView imageViewPersonagem;
        TextView textViewNome;

        public VH(View itemView) {
            super(itemView);
            imageViewPersonagem = (ImageView) itemView.findViewById(R.id.personagem_item_image);
            textViewNome = (TextView) itemView.findViewById(R.id.personagem_item_text_name);

        }
    }
}
