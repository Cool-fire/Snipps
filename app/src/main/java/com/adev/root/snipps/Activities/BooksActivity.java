package com.adev.root.snipps.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.adev.root.snipps.R;
import com.adev.root.snipps.adapters.BookAdapter;
import com.adev.root.snipps.adapters.RecyclerTouchListener;
import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.presenter.BooksActivityPresenter;
import com.adev.root.snipps.view.BooksActivityView;

import io.realm.Realm;
import io.realm.RealmResults;

public class BooksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,BooksActivityView {

    private RealmResults<Book> books;
    private Realm realm;
    private BooksActivityPresenter presenter;
    private BooksActivityView view;
    private BookAdapter mAdapter;
    private RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NewBookActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        view = this;
        presenter = new BooksActivityPresenter(view,realm);

        recyclerview = (RecyclerView)findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.Clicklistner() {
            @Override
            public void onclick(View view, int position) {
                // Toast.makeText(getApplicationContext(),books.get(position).getBookAuthor(),Toast.LENGTH_SHORT).show();
               Intent toSnippet = new Intent(getApplicationContext(),SnippetActivity.class);
               toSnippet.putExtra("position",position);
               startActivity(toSnippet);
                Log.d("TAG", "onclickit: "+position);
            }

            @Override
            public void onLongClick(View view, int position) {
                // Toast.makeText(getApplicationContext(),books.get(position).getBookTitle(),Toast.LENGTH_SHORT).show();
                mAdapter.deleteBook(realm,position);
                Log.d("TAG", "onLongclick: "+position);
            }

            @Override
            public void onDoubleTap(View view, int position) {
                Log.d("TAG", "onDoubleTapit: "+position);
            }
        }));


    }

    @Override
    protected void onStart() {
        super.onStart();
        books = getBooksList();
        mAdapter = new BookAdapter(books);
        recyclerview.setAdapter(mAdapter);

    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public RealmResults<Book> getBooksList() {
        return presenter.getBooks();
    }
}
