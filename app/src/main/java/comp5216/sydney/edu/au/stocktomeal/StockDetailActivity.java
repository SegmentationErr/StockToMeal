package comp5216.sydney.edu.au.stocktomeal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import comp5216.sydney.edu.au.stocktomeal.Model.Food;


public class StockDetailActivity extends AppCompatActivity {

    private String stockName;
    private String stockAmount;
    private String stockTime;

    private TextView name;
    private TextView amount;
    private TextView expireDate;
    private TextView photo;
    private ImageView image;
    private Button edit;

    private FirebaseUser user;
    private String userID;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail);

        //connect firestore
        db = FirebaseFirestore.getInstance();
        //connect auth, get current userID
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getEmail();

        stockName = getIntent().getStringExtra("stockName");
        stockAmount = getIntent().getStringExtra("stockAmount");
        stockTime = getIntent().getStringExtra("stockTime");

        name = (TextView) findViewById(R.id.stock_name);
        amount = (TextView) findViewById(R.id.stock_amount);
        expireDate = (TextView) findViewById(R.id.stock_expireDate);
        photo = (TextView) findViewById(R.id.stock_photo);
        edit = (Button) findViewById(R.id.edit);

        name.setText("Food name:    " + stockName);
        amount.setText("Amount:          " + stockAmount);
        expireDate.setText("Expire date:    " + stockTime);
        photo.setText("Photo:");
    }

    public void onEditClick(View v) {
        Intent intent = new Intent(StockDetailActivity.this, EditStockActivity.class);
        if (intent != null) {
            // put "extras" into the bundle for access in the detail activity

            String currentDate = new SimpleDateFormat("yyyy - MM - dd", Locale.getDefault())
                    .format(new Date());
            intent.putExtra("foodName", stockName);
            intent.putExtra("amount", stockAmount);
            intent.putExtra("expireDate", stockTime);

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

                    // Update data from database
                    DocumentReference stockRef = db.collection("food").document(foodName + "_" + userID);
                    stockRef.update("name",foodName,"amount",amount,"time",expireDate);

                    // Show notification of update
                    Toast.makeText(getApplicationContext(),
                            "Updated: " + foodName,
                            Toast.LENGTH_SHORT).show();
                }

                // If user cancelled editing
                else if (result.getResultCode() == RESULT_CANCELED) {
                    // Nothing to do
                }
            }
    );
}