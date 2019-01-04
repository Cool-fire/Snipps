package com.adev.root.snipps.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adev.root.snipps.Activities.DrawImageActivity;
import com.adev.root.snipps.Activities.OpenSnippetActivity;
import com.adev.root.snipps.Activities.SnippetActivity;
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
    private final Activity parentActivity;
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
    public void onBindViewHolder(@NonNull SnippetAdapter.ViewHolder holder, final int position) {
        Snippet snippet = book.getSnippetsList().get(position);
        final String name = snippet.getSnippetName();
        final String note = snippet.getNotes();
        long pageno = snippet.getSnippetPageNo();
        holder.snippetName.setText(name);
        holder.snippetPage.setText("page no: "+pageno);
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
        if(snippet.getNotes() != null)
        {
            holder.snippetNote.setVisibility(View.VISIBLE);
            holder.snippetNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNote(view,name,note);
                }
            });
        }
    }
    private void showNote(View view, String name, String note) {
        final AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage(note).setTitle(name).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

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
    public SnippetAdapter(Book book, Activity parentActivity) {
        this.book = book;
        this.parentActivity = parentActivity;

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
        private final ImageView snippetNote;
        private final LinearLayout snippetLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            snippetName = itemView.findViewById(R.id.snippetNames);
            snippetPage = itemView.findViewById(R.id.snippetPageNumber);
            snippetImage = itemView.findViewById(R.id.Snippetimage);
            snippetimageProgress = itemView.findViewById(R.id.progressBarId);
            snippetNote = itemView.findViewById(R.id.notesBtn);
            snippetLayout = itemView.findViewById(R.id.snippetListlayout);

            snippetLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Intent intent1 = new Intent(view.getContext(),OpenSnippetActivity.class);
                    long bookId = book.getId();
                    intent1.putExtra("snippetPosition",Integer.toString(pos));
                    intent1.putExtra("bookId",String.valueOf(bookId));
                    view.getContext().startActivity(intent1);
                }
            });

        }
    }
}
