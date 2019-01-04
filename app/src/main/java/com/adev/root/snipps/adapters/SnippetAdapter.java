package com.adev.root.snipps.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adev.root.snipps.R;
import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.model.entities.Snippet;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;

import static android.support.constraint.Constraints.TAG;

public class SnippetAdapter extends RecyclerView.Adapter<SnippetAdapter.ViewHolder> {
    private final Book book;
    private ImageView snippetImage;
    private ProgressBar snippetimageProgress;
    private File imageFile;
    private Context context;

    @NonNull
    @Override
    public SnippetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View ItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippetlist_row,parent,false);
        return new SnippetAdapter.ViewHolder(ItemView);

    }

    @Override
    public void onBindViewHolder(@NonNull SnippetAdapter.ViewHolder holder, int position) {
        Snippet snippet = book.getSnippetsList().get(position);
        holder.snippetName.setText(snippet.getSnippetName());
        holder.snippetPage.setText("page no: "+snippet.getSnippetPageNo());
        snippetImage = holder.snippetImage;
        snippetimageProgress = holder.snippetimageProgress;
        snippetimageProgress.setVisibility(View.VISIBLE);
        try
        {
            imageFile = new File(snippet.getImagePath().toString());
            if(imageFile.exists())
            {
                Picasso.get().load(imageFile).memoryPolicy(MemoryPolicy.NO_CACHE).resize(context.getResources().getDimensionPixelSize(R.dimen.snippet_thumb_width),
                        context.getResources().getDimensionPixelSize(R.dimen.snippet_thumb_height)).centerCrop().into(snippetImage);
                snippetimageProgress.setVisibility(View.INVISIBLE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void deleteBook(Realm realm, final int position)
    {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
               book.getSnippetsList().deleteFromRealm(position);
            }
        });
        notifyDataSetChanged();
        notifyItemRangeChanged(position,getItemCount());

    }
    public SnippetAdapter(Book book) {
        this.book = book;
    }

    @Override
    public int getItemCount() {
        return book.getSnippetsList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView snippetName;
        private final TextView snippetPage;
        private final ImageView snippetImage;
        private final ProgressBar snippetimageProgress;

        public ViewHolder(View itemView) {
            super(itemView);
            snippetName = itemView.findViewById(R.id.snippetNames);
            snippetPage = itemView.findViewById(R.id.snippetPageNumber);
            snippetImage = itemView.findViewById(R.id.Snippetimage);
            snippetimageProgress = itemView.findViewById(R.id.progressBarId);

        }
    }
}
