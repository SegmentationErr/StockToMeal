package comp5216.sydney.edu.au.stocktomeal;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import comp5216.sydney.edu.au.stocktomeal.Model.Recipe;

public class FavoriteListActivity extends AppCompatActivity {


    public RecyclerView firestoreList;
    public RecyclerView.LayoutManager layoutManager;

    private FirestoreRecyclerAdapter adapter;
    private FirebaseUser user;
    private String userID;
    private FirebaseFirestore db;


    ListView listView;
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_list);
        firestoreList = findViewById(R.id.Fav_RecyclerList);

        //connect firestore
        db = FirebaseFirestore.getInstance();
        //connect auth, get current userID
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getEmail();

        //query current user fav recipe list
        Query query = db.collection("recipe").whereEqualTo("userID",userID);

        FirestoreRecyclerOptions<Recipe> options = new FirestoreRecyclerOptions.Builder<Recipe>()
                .setQuery(query,Recipe.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Recipe, RecipeViewHolder>(options) {
            @NonNull
            @Override
            public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_recipe,parent,false);
                return new RecipeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RecipeViewHolder holder, int position, @NonNull Recipe model) {
                holder.recipeName.setText(model.getRecipeName());
            }
        };

        String toAddID = getIntent().getStringExtra("recipeId");
        String toAddName = getIntent().getStringExtra("recipeName");
        String toAddString = toAddID + " " + toAddName;
        if (toAddID != null && toAddString.length() > 1) {

            // Add fav recipe to database
            Recipe recipe = new Recipe(toAddID,toAddName,userID);
            db.collection("recipe").document(toAddName + "_" + userID).set(recipe);

            Toast.makeText(getApplicationContext(), toAddName +
                    " has been added into your favourite list", Toast.LENGTH_SHORT).show();
        }

        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(this));
        firestoreList.setAdapter(adapter);

        setupRecyclerViewListener();


    }

    private void setupRecyclerViewListener() {
        firestoreList.addOnItemTouchListener(new RecyclerItemClickListener(FavoriteListActivity.this, firestoreList, new RecyclerItemClickListener.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int position) {

                Recipe model = (Recipe) adapter.getItem(position);
                String recipeName = model.getRecipeName();
                String recipeID = model.getRecipeID();
                String allery = "";
                Log.d(TAG, "This is the recipe id: " + recipeID);
                Intent intent = new Intent(FavoriteListActivity.this,
                        RecipeDetailActivity.class);
                if (intent != null) {
                    // put "extras" into the bundle for access in the detail activity
                    intent.putExtra("id", recipeID);
                    //intent.putExtra("search", null);
                    intent.putExtra("allery", allery);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                Recipe model = (Recipe) adapter.getItem(position);
                String recipeName = model.getRecipeName();
                Log.i("FavoriteListActivity", "Long Clicked item " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteListActivity.this);
                builder.setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_msg)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //Remove item from the database
                                db.collection("recipe").document(recipeName + "_" + userID).delete();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancelled the dialog
                                // Nothing happens
                            }
                        });

                builder.create().show();

            }


        }));


    }


    private class RecipeViewHolder extends RecyclerView.ViewHolder{

        private TextView recipeName;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.list_item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

