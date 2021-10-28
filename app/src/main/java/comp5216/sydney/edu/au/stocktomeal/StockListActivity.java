package comp5216.sydney.edu.au.stocktomeal;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
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
        //connect firestore
        db = FirebaseFirestore.getInstance();
        //connect auth, get current userID
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getEmail();

        //query current user stock status
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

           @RequiresApi(api = Build.VERSION_CODES.O)
           @Override
           protected void onBindViewHolder(@NonNull StockViewHolder holder, int position, @NonNull Food model) {
               holder.list_food.setText(model.getName());
               holder.list_amount.setText(model.getAmount());
               holder.list_expire.setText(model.getTime());
               DateFormat dateFormat = new SimpleDateFormat("yyyy - MM - dd");
               Calendar cal = Calendar.getInstance();
               cal.add(Calendar.DATE, 1);
               Date todate1 = cal.getTime();
               String fromDate = dateFormat.format(todate1);
               if (fromDate.equals(model.getTime())) {
                   holder.list_expire.setText("One Day To Expire");     // About one day to expire
                   holder.list_expire.setBackgroundColor(Color.rgb(246, 155, 0));
                   holder.list_expire.setTextColor(Color.WHITE);
               } else {
                      // will not be expire in one day
               }

               LocalDate localDate = LocalDate.now();
               DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy - MM - dd");
               String today = localDate.format(formatter);
               if (today.equals(model.getTime())) {
                   holder.list_expire.setText("Expires Today");     // Expires today
                   holder.list_expire.setBackgroundColor(Color.rgb(246, 97, 0));
                   holder.list_expire.setTextColor(Color.WHITE);
               } else {
                   // will not be expire today
               }
           }
       };

        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(this));
        firestoreList.setAdapter(adapter);

        setupRecyclerViewListener();
    }

    private void setupRecyclerViewListener() {
        firestoreList.addOnItemTouchListener(new RecyclerItemClickListener(StockListActivity.this, firestoreList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Food model = (Food) adapter.getItem(position);
                String stockImage = model.getPicture();
                String stockName = model.getName();
                String stockAmount = model.getAmount();
                String stockTime = model.getTime();
                Log.i("StockListActivity", "Clicked item " + position + ": " + stockName);

                Intent intent = new Intent(StockListActivity.this, StockDetailActivity.class);
                if (intent != null) {
                    // put "extras" into the bundle for access in the detail activity
                    intent.putExtra("stockImage", stockImage);
                    intent.putExtra("stockName", stockName);
                    intent.putExtra("stockAmount", stockAmount);
                    intent.putExtra("stockTime", stockTime);
                    intent.putExtra("position", position);

                    mLauncher.launch(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                Food model = (Food) adapter.getItem(position);
                String foodName = model.getName();

                //Toast.makeText(StockListActivity.this,"长按",Toast.LENGTH_SHORT).show();
                Log.i("StockListActivity", "Long Clicked item " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(StockListActivity.this);
                builder.setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_msg)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {

                                 // Remove item from the database
                                db.collection("food").document(foodName + "_" + userID).delete();

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

    public void onAddButtonClick(View v) {
        Intent intent = new Intent(StockListActivity.this, EditStockActivity.class);
        if (intent != null) {
            // put "extras" into the bundle for access in the detail activity

            String currentDate = new SimpleDateFormat("yyyy - MM - dd", Locale.getDefault())
                    .format(new Date());
            intent.putExtra("foodImage", "");
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
                // Add item
                if (result.getResultCode() == 101) {
                    // Read all data from data intent sent from edit activity
                    String foodImage = result.getData().getStringExtra("foodImage");
                    String foodName = result.getData().getStringExtra("foodName");
                    String amount = result.getData().getStringExtra("amount");
                    String expireDate = result.getData().getStringExtra("expireDate");

                    // Show notification of update
                    Toast.makeText(getApplicationContext(),
                            "Added: " + foodName,
                            Toast.LENGTH_SHORT).show();

                    // Add new data to database
                    Food food = new Food(userID,foodName,amount,foodImage,expireDate);
                    db.collection("food").document(foodName + "_" + userID).set(food);

                } else if (result.getResultCode() == 102) {     // Edit item
                    String foodImage = result.getData().getStringExtra("foodImage");
                    String foodName = result.getData().getStringExtra("foodName");
                    String amount = result.getData().getStringExtra("amount");
                    String expireDate = result.getData().getStringExtra("expireDate");
                    int position = result.getData().getIntExtra("position", -1);

                    // Show notification of update
                    Toast.makeText(getApplicationContext(),
                            "Updated",Toast.LENGTH_SHORT).show();

                    // Update data from database
                    DocumentReference stockRef = db.collection("food").document(foodName + "_" + userID);
                    stockRef.update("name",foodName,"amount",amount,"time",expireDate,"picture",foodImage);


                }
            }
    );


    private class StockViewHolder extends RecyclerView.ViewHolder{

        private TextView list_food;
        private TextView list_amount;
        private TextView list_expire;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            list_food = itemView.findViewById(R.id.list_food);
            list_amount = itemView.findViewById(R.id.list_amount);
            list_expire = itemView.findViewById(R.id.list_expire);
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
