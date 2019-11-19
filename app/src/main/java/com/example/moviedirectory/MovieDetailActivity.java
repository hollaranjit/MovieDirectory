package com.example.moviedirectory;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.moviedirectory.Model.Movie;
import com.example.moviedirectory.Util.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailActivity extends AppCompatActivity {
    Movie movie;
    ImageView movieImg;
    TextView  titleTextview,releasedate;
    RequestQueue queue;
    String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

          movie= (Movie) getIntent().getSerializableExtra("movie");
          movieImg=findViewById(R.id.movieImageIDDet);
          titleTextview=findViewById(R.id.movieTitleIDets);
          releasedate=findViewById(R.id.movieReleaseIDDets);


          queue= Volley.newRequestQueue(this);
          movieId=movie.getImdbId();

          getMovieDetails(movieId);
    }

    private void getMovieDetails(String id) {

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,Constants.URL+id,null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //Log.i(MainActivity.class.getSimpleName(), "Response body: "+response.toString());

                try{
                    if(response.has("Ratings"))
                    {
                        JSONArray ratings=response.getJSONArray("Ratings");

                        String source=null;
                        String value =null;

                        if(ratings.length()>0)
                        {
                            JSONObject mRatings=ratings.getJSONObject(ratings.length()-1);
                            source=mRatings.getString("Source");
                            value=mRatings.getString("Value");

                        }

                    }
                    titleTextview.setText(response.getString("Title"));
                    releasedate.setText(response.getString("Released"));
                    Picasso.get().load(response.getString("Poster")).into(movieImg);




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
}
