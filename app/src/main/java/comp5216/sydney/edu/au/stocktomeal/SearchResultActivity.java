package comp5216.sydney.edu.au.stocktomeal;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    private String keywords;

    // Layout components
    private ListView resultsView;
    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        // Property init
        recipes = new ArrayList<>();
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

        // Use ingredients to filter the search result
        db.collection("recipe").whereArrayContains("ingredients", keywords).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "-" + document.getData());
                                recipes.add((String) document.get("name"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        resultsView.setAdapter(itemsAdapter);
                    }
                });

        setupListViewListener();
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
                    intent.putExtra("recipe", name);
                    intent.putExtra("search", keywords);
                    startActivity(intent);
                }
            }
        });
    }
}