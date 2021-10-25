package comp5216.sydney.edu.au.stocktomeal;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    private String keywords;
    public RequestQueue requestQueue;

    // Layout components
    private ListView resultsView;
    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> recipes;
    private ArrayList<String> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        // Property init
        recipes = new ArrayList<>();
        ids = new ArrayList<>();
        resultsView = (ListView) findViewById(R.id.search_container);
        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, recipes);
        Intent intent = getIntent();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Search keywords receive
        if (intent != null && intent.getStringExtra("search") != null) {
            keywords = intent.getStringExtra("search");
            Log.d(TAG, "Terminal print keywords " + keywords);
        } else {
            Log.d(TAG, "Intent doesn't put any keywords in.");
            startActivity(new Intent(SearchResultActivity.this,
                    RecipeRecommdationActivity.class) );
        }

        searchAPIResult();
        setupListViewListener();
    }

    private void searchAPIResult() {
        requestQueue = Volley.newRequestQueue(this);

        String apiKey = "c380ae7249f047898648b09c147204a4";

        String url = "https://api.spoonacular.com/recipes/findByIngredients?apiKey="+
                apiKey+"&ingredients="+keywords;

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                recipes.add((String) jsonObject.get("title"));
                                ids.add(String.valueOf(jsonObject.get("id")));
                                //Log.d(TAG, (String) jsonObject.get("title"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        resultsView.setAdapter(itemsAdapter);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);

    }

    public void setupListViewListener() {
        resultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) itemsAdapter.getItem(position);
                Log.d(TAG, "This is the recipe name: " + name);
                Intent intent = new Intent(SearchResultActivity.this,
                        RecipeDetailActivity.class);
                if (intent != null) {
                    // put "extras" into the bundle for access in the edit activity
                    int index = recipes.indexOf(name);
                    intent.putExtra("id", ids.get(index));
                    intent.putExtra("search", keywords);
                    startActivity(intent);
                }
            }
        });
    }
}