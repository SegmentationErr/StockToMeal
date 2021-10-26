package comp5216.sydney.edu.au.stocktomeal;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import comp5216.sydney.edu.au.stocktomeal.Model.Food;

public class StockListActivity extends AppCompatActivity {

    public RecyclerView firestoreList;
    public RecyclerView.LayoutManager layoutManager;

    private FirestoreRecyclerAdapter adapter;
    private FirebaseUser user;
    private String userID;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_list);
        firestoreList = findViewById(R.id.RecyclerList);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getEmail();


        Query query = db.collection("food").whereEqualTo("userID",userID);
       FirestoreRecyclerOptions<Food> options = new FirestoreRecyclerOptions.Builder<Food>()
                .setQuery(query,Food.class)
                .build();

       adapter = new FirestoreRecyclerAdapter<Food, StockViewHolder>(options) {
           @NonNull
           @Override
           public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item,parent,false);
               return new StockViewHolder(view);
           }

           @Override
           protected void onBindViewHolder(@NonNull StockViewHolder holder, int position, @NonNull Food model) {
               holder.list_food.setText(model.getName());
               holder.list_amount.setText(model.getAmount());
           }
       };

        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(this));
        firestoreList.setAdapter(adapter);








        /*
        // Reference the "listView" variable to the id "lstView" in the layout
        listView = (ListView) findViewById(R.id.lstView);

        // Create an ArrayList of String
        items = new ArrayList<String>(); items.add("Food 1"); items.add("Food 2");

        // Create an adapter for the list view using Android's built-in item layout
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        // Connect the listView and the adapter
        listView.setAdapter(itemsAdapter);

        // Setup listView listeners
        setupListViewListener();*/
    }



    /*
    private void setupListViewListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long rowId)
            {
                Log.i("StockListActivity", "Long Clicked item " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(StockListActivity.this);
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
                String stock = (String) itemsAdapter.getItem(position);
                Log.i("StockListActivity", "Clicked item " + position + ": " + stock);

                Intent intent = new Intent(StockListActivity.this, StockDetailActivity.class);
                if (intent != null) {
                    // put "extras" into the bundle for access in the detail activity
                    intent.putExtra("stock", stock);
                    intent.putExtra("position", position);

                    startActivity(intent);
                    itemsAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    */


    public void onAddButtonClick(View v) {
        Intent intent = new Intent(StockListActivity.this, EditStockActivity.class);
        if (intent != null) {
            // put "extras" into the bundle for access in the detail activity

            String currentDate = new SimpleDateFormat("yyyy - MM - dd", Locale.getDefault())
                    .format(new Date());
            intent.putExtra("foodName", "");
            intent.putExtra("amount", "1");
            intent.putExtra("expireDate", currentDate);

            mLauncher.launch(intent);
        }
    }

    // Initialise ActivityResultLauncher and setup callback function
    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // If user successfully edit item and send data back
                if (result.getResultCode() == RESULT_OK) {
                    // Read all data from data intent sent from edit activity
                    String foodName = result.getData().getStringExtra("foodName");
                    String amount = result.getData().getStringExtra("amount");
                    String expireDate = result.getData().getStringExtra("expireDate");

                    // Show notification of update
                    Toast.makeText(getApplicationContext(),
                            "Added: " + foodName,
                            Toast.LENGTH_SHORT).show();

                    // TODO 更新对应数据！！！！！！！！！
                    Food food = new Food(userID,foodName,amount,expireDate);
                    db.collection("food").document(foodName + "_" + userID).set(food);



                }

                // If user cancelled editing
                else if (result.getResultCode() == RESULT_CANCELED) {
                    // Nothing to do
                }
            }
    );

    private class StockViewHolder extends RecyclerView.ViewHolder{

        private TextView list_food;
        private TextView list_amount;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            list_food = itemView.findViewById(R.id.list_food);
            list_amount = itemView.findViewById(R.id.list_amount);
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
