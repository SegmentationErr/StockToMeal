package comp5216.sydney.edu.au.stocktomeal;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    // The name of the recipe. Please do not upload the repeated recipes
    private String recipeName;

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

        if (intent != null && intent.getStringExtra("recipe") != null) {
            recipeName = intent.getStringExtra("recipe");
            Log.d(TAG,"Terminal Print Keywords: " + recipeName);
        } else {
            Log.d(TAG, "Intent doesn't put any keywords in.");
            Intent other = new Intent(RecipeDetailActivity.this,
                    RecipeRecommdationActivity.class);
            other.putExtra("search", intent.getStringExtra("search"));
            startActivity(other);
        }

        // Init the layout
        recipe = (TextView) findViewById(R.id.recipe_text);
        ingredients = (TextView) findViewById(R.id.ingredients_text);
        steps = (TextView) findViewById(R.id.step_text);
        flavour = (TextView) findViewById(R.id.flavour_text);
        image = (ImageView) findViewById(R.id.recipe_image);
        favourite = (Button) findViewById(R.id.like_it_button);

        // Get the recipe details from the database
        db.collection("recipe")
                .whereEqualTo("name", recipeName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                recipe.setText((String) document.get("name"));
                                String f = "";
                                for (String fla : (ArrayList<String>) document.get("ingredients")) {
                                    f += fla + "\n";
                                }
                                ingredients.setText(f);
                                f = "";
                                for (String fla : (ArrayList<String>) document.get("flavour")) {
                                    f += fla + "\n";
                                }
                                flavour.setText(f);
                                f = "";
                                for (String fla : (ArrayList<String>) document.get("step")) {
                                    f += fla + "\n";
                                }
                                steps.setText(f);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}