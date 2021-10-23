package comp5216.sydney.edu.au.stocktomeal;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    // The name of the recipe. Please do not upload the repeated recipes
    private String recipeId;
    private String keyword;

    public RequestQueue requestQueue;
    private JSONArray ingredientsJson;

    // Design of the layout
    private TextView recipe;
    private TextView ingredients;
    private TextView steps;
    private TextView flavour;
    private ImageView image;
    private Button favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail);
        Intent intent = getIntent();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        keyword = intent.getStringExtra("search");

        if (intent != null && intent.getStringExtra("id") != null) {
            recipeId = intent.getStringExtra("id");
            Log.d(TAG,"Terminal Print IDs: " + recipeId);
        } else {
            Log.d(TAG, "Intent doesn't put any keywords in.");
            Intent other = new Intent(RecipeDetailActivity.this,
                    RecipeRecommdationActivity.class);
            other.putExtra("search", keyword);
            startActivity(other);
        }

        // Init the layout
        recipe = (TextView) findViewById(R.id.recipe_text);
        ingredients = (TextView) findViewById(R.id.ingredients_text);
        steps = (TextView) findViewById(R.id.step_text);
        flavour = (TextView) findViewById(R.id.flavour_text);
        image = (ImageView) findViewById(R.id.recipe_image);
        favourite = (Button) findViewById(R.id.like_it_button);

        getRecipeDetailsFromAPI();
    }

    private void getRecipeDetailsFromAPI() {
        requestQueue = Volley.newRequestQueue(this);

        String apiKey = "c380ae7249f047898648b09c147204a4";

        String url = "https://api.spoonacular.com/recipes/" + recipeId + "/information?" +
                "apiKey=" + apiKey;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,response.toString());
                        try {
                            recipe.setText((String) response.get("title"));
                            //image.setImageURI(Uri.parse(response.get("image").toString()));
                            StringBuilder sb = new StringBuilder();
                            ingredientsJson = (JSONArray) response.get("extendedIngredients");
                            for (int i = 0; i < ingredientsJson.length(); i++) {
                                JSONObject object = (JSONObject) ingredientsJson.get(i);
                                sb.append(object.get("name").toString()+"\n");
                            }
                            ingredients.setText(sb.toString());
                            getTaste();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d(TAG, error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void getCookStep() {
        requestQueue = Volley.newRequestQueue(this);

        String apiKey = "c380ae7249f047898648b09c147204a4";

        String url = "https://api.spoonacular.com/recipes/" + recipeId + "/analyzedInstructions?" +
                "apiKey=" + apiKey;

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jObject = (JSONObject) response.get(0);
                            StringBuilder sb = new StringBuilder("");
                            JSONArray jsonArray = (JSONArray) jObject.get("steps");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = (JSONObject) jsonArray.get(i);
                                sb.append(object.get("step").toString()+"\n");
                            }
                            steps.setText(sb.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d(TAG, error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void getTaste() {
        requestQueue = Volley.newRequestQueue(this);

        String apiKey = "c380ae7249f047898648b09c147204a4";

        String url = "https://api.spoonacular.com/recipes/" + recipeId + "/tasteWidget.json?" +
                "apiKey=" + apiKey;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            StringBuilder sb = new StringBuilder();
                            sb.append(response.get("sweetness").toString() + "% sweet\n");
                            sb.append(response.get("saltiness").toString() + "% salt\n");
                            sb.append(response.get("spiciness").toString() + "% spicy\n");
                            sb.append(response.get("sourness").toString() + "% sour\n");
                            sb.append(response.get("bitterness").toString() + "% bitter\n");
                            flavour.setText(sb.toString());
                            getCookStep();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d(TAG, error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

}