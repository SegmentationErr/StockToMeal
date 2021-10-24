package comp5216.sydney.edu.au.stocktomeal;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FavoriteListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_list);

        // Reference the "listView" variable to the id "lstView" in the layout
        listView = (ListView) findViewById(R.id.lstView);

        // Create an ArrayList of String
        items = new ArrayList<String>();
        items.add("716342 Chicken Suya");
        items.add("662744 Taco Egg Roll");

        // Create an adapter for the list view using Android's built-in item layout
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        String toAddID = getIntent().getStringExtra("recipeId");
        String toAddName = getIntent().getStringExtra("recipeName");
        String toAddString = toAddID + " " + toAddName;
        if (toAddID != null && toAddString.length() > 1) {
            itemsAdapter.add(toAddString);
            Toast.makeText(getApplicationContext(), toAddName +
                    " has been added into your favourite list", Toast.LENGTH_SHORT).show();
        }

        // Connect the listView and the adapter
        listView.setAdapter(itemsAdapter);

        setupListViewListener();
    }

    private void setupListViewListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long rowId) {
                Log.i("FavoriteListActivity", "Long Clicked item " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteListActivity.this);
                builder.setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_msg)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                items.remove(position); // Remove item from the ArrayList
                                itemsAdapter.notifyDataSetChanged(); // Notify listView adapter to update the list
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancelled the dialog
                                // Nothing happens
                            }
                        });

                builder.create().show();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String recipe = (String) itemsAdapter.getItem(position);
                String ID = recipe.substring(0, 6);
                Log.d(TAG, "This is the recipe id: " + ID);
                Intent intent = new Intent(FavoriteListActivity.this,
                        RecipeDetailActivity.class);
                if (intent != null) {
                    // put "extras" into the bundle for access in the detail activity
                    intent.putExtra("id", ID);
                    //intent.putExtra("search", null);
                    startActivity(intent);
                }
            }
        });
    }
}

