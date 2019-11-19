package com.example.moviedirectory;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.moviedirectory.Data.MovieRecyclerViewAdapter;
import com.example.moviedirectory.Model.Movie;
import com.example.moviedirectory.Util.Constants;
import com.example.moviedirectory.Util.Prefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView             recyclerView;
    MovieRecyclerViewAdapter movieRecyclerViewAdapter;
    RequestQueue             queue;
    AlertDialog.Builder  alartDialogBuilder;
    AlertDialog  dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queue= Volley.newRequestQueue(this);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Prefs prefs=new Prefs(MainActivity.this);
        String search=prefs.getSearch();
        getMovies(search);

    }

    public void getMovies(final String searchTerm)
    {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,Constants.URL_LEFT + Constants.URL_RIGHT +searchTerm,null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i(MainActivity.class.getSimpleName(), "Response body: "+response.toString());

                try{
                    JSONArray moviesArray=response.getJSONArray("Search");
                    List<Movie> movieList = new ArrayList<>();

                    for(int i=0;i<moviesArray.length();i++)
                    {

                        JSONObject movieObj=moviesArray.getJSONObject(i);

                        Movie movie=new Movie();

                        movie.setTitle(movieObj.getString("Title"));
                        movie.setYear(movieObj.getString("Year"));
                        movie.setMovieType(movieObj.getString("Type"));
                        movie.setPoster(movieObj.getString("Poster"));
                        movie.setImdbId(movieObj.getString("imdbID"));

                         Log.d("Movies: ",movie.getTitle());

                         movieList.add(movie);
                    }


                    initMovieListAdapter(movieList);

                }catch (JSONException e)
                {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volly Error", error.toString());
            }
        });

        queue.add(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_search) {
            showInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    public void initMovieListAdapter(List<Movie> movies){
        movieRecyclerViewAdapter=new MovieRecyclerViewAdapter(this,movies);
        recyclerView.setAdapter(movieRecyclerViewAdapter);
        movieRecyclerViewAdapter.notifyDataSetChanged();
    }


    public void showInputDialog()
    {
        alartDialogBuilder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.dialog_view,null);
        final EditText newSearchEdt=view.findViewById(R.id.searchEdit);
        Button submitButton=view.findViewById(R.id.submitButton);

        alartDialogBuilder.setView(view);
        dialog=alartDialogBuilder.create();
        dialog.show();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs  prefs=new Prefs(MainActivity.this);

                if(!newSearchEdt.getText().toString().isEmpty())
                {
                    String search=newSearchEdt.getText().toString();
                    prefs.setSearch(search);
                    getMovies(search);
                    movieRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });

    }

}
