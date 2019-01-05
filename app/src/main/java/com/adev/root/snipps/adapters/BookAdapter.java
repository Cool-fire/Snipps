package com.adev.root.snipps.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adev.root.snipps.R;
import com.adev.root.snipps.model.entities.Book;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.support.constraint.Constraints.TAG;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private final RealmResults<Book> books;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View ItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.booklist_row, parent, false);
        return new ViewHolder(ItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        try {
            Book book = books.get(position);
            holder.title.setText(book.getBookTitle());
            holder.author.setText(book.getBookAuthor());
            int noOfSnippets = book.getSnippetsList().size();
            holder.snippetNo.setText(Integer.toString(noOfSnippets));
            SimpleDateFormat month_date = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            String month_name = month_date.format(book.getCreationDate());
            holder.bookDate.setText(month_name);
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }


    public void deleteBook(Realm realm, final int position) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                books.deleteFromRealm(position);
            }
        });
        notifyDataSetChanged();
        notifyItemRangeChanged(position, getItemCount());

    }

    public BookAdapter(RealmResults<Book> booksList) {

        this.books = booksList;
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView author;
        private final View ItemView;
        private final TextView snippetNo;
        private final TextView bookDate;


        public ViewHolder(View itemView) {
            super(itemView);
            this.ItemView = itemView;
            title = ItemView.findViewById(R.id.title);
            author = ItemView.findViewById(R.id.author);
            snippetNo = ItemView.findViewById(R.id.snippetsNumber);
            bookDate = ItemView.findViewById(R.id.bookDatecreation);
        }
    }

}
