package com.adev.root.snipps.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Trace;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.adev.root.snipps.R;
import com.adev.root.snipps.adapters.BookAdapter;
import com.adev.root.snipps.adapters.RecyclerTouchListener;
import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.model.entities.Snippet;
import com.adev.root.snipps.presenter.BooksActivityPresenter;
import com.adev.root.snipps.view.BooksActivityView;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class BooksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BooksActivityView {

    private RealmResults<Book> books;
    private Realm realm;
    private BooksActivityPresenter presenter;
    private BooksActivityView view;
    private BookAdapter mAdapter;
    private RecyclerView recyclerview;
    private RealmResults<Book> sortedBooks;
    private String sortedTitle;
    private int Position;
    private DrawerLayout drawer;
    private ImageView bookOutline;
    private PopupMenu popup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewBookActivity.class);
                intent.putExtra("NewBookActivitytype","NewBook");
                startActivity(intent);
            }
        });

       drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();


        view = this;
        presenter = new BooksActivityPresenter(view, realm);

        recyclerview = findViewById(R.id.recycler_view);
        bookOutline = findViewById(R.id.bookOutline);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.Clicklistner() {


            @Override
            public void onclick(View view, int position) {
                sortedTitle = sortedBooks.get(position).getBookTitle();
                Intent toSnippet = new Intent(getApplicationContext(), SnippetActivity.class);
                toSnippet.putExtra("position", Integer.toString(position));
                toSnippet.putExtra("title", sortedTitle);
                startActivity(toSnippet);
            }

            @Override
            public void onLongClick(View view, int position) {
                // Toast.makeText(getApplicationContext(),books.get(position).getBookTitle(),Toast.LENGTH_SHORT).show();
                Position = position;
                showPopup(view);
            }

            @Override
            public void onDoubleTap(View view, int position) {
            }
        }));


    }

    @Override
    protected void onStart() {
        super.onStart();
        try
        {
            books = getBooksList();
            if(books.size()>0)
            {
                bookOutline.setVisibility(View.GONE);
            }
            sortedBooks = books.sort("creationDate", Sort.DESCENDING);
            mAdapter = new BookAdapter(sortedBooks);
            recyclerview.setAdapter(mAdapter);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
      int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public RealmResults<Book> getBooksList() {
        return presenter.getBooks();
    }


    public void showPopup(View v) {
        popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.action_list, popup.getMenu());
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            popup.dismiss();
        }
        else
        {
            popup.show();
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        deleteItem();
                        return true;
                    case R.id.edit:
                        updateItem();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void updateItem() {
        Book book = sortedBooks.get(Position);
        long id = book.getId();
        Intent intent = new Intent(BooksActivity.this,NewBookActivity.class);
        intent.putExtra("updatebookId",String.valueOf(id));
        intent.putExtra("NewBookActivitytype","UpdateBook");
        startActivity(intent);

    }

    private void deleteItem() {
        Book book = sortedBooks.get(Position);
        sortedTitle = book.getBookTitle();
        File storageDir = getExternalFilesDir("Pictures/Snipps/");
        if (storageDir.isDirectory()) {
            RealmList<Snippet> snippetsList = book.getSnippetsList();
            for (int i = 0; i < snippetsList.size(); i++) {

                String imagePath = snippetsList.get(i).getImagePath();
                File imageFile = new File(imagePath);
                if(imageFile.exists())
                {
                    imageFile.delete();
                }
            }
        }
        mAdapter.deleteBook(realm, Position);
        Snackbar.make(drawer,"Book Deleted",Snackbar.LENGTH_SHORT).show();
        try
        {
            if(books.size()==0)
            {
                bookOutline.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
