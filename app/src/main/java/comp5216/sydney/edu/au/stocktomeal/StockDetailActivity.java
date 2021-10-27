package comp5216.sydney.edu.au.stocktomeal;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import comp5216.sydney.edu.au.stocktomeal.Model.Food;


public class StockDetailActivity extends AppCompatActivity {

    private String stockImage;
    private String stockName;
    private String stockAmount;
    private String stockTime;
    private String position;

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

        stockImage = getIntent().getStringExtra("stockImage");
        stockName = getIntent().getStringExtra("stockName");
        stockAmount = getIntent().getStringExtra("stockAmount");
        stockTime = getIntent().getStringExtra("stockTime");
        position = getIntent().getStringExtra("position");

        image = (ImageView) findViewById(R.id.stock_image);
        name = (TextView) findViewById(R.id.stock_name);
        amount = (TextView) findViewById(R.id.stock_amount);
        expireDate = (TextView) findViewById(R.id.stock_expireDate);
        photo = (TextView) findViewById(R.id.stock_photo);
        edit = (Button) findViewById(R.id.edit);

        name.setText("Food name:    " + stockName);
        amount.setText("Amount:          " + stockAmount);
        expireDate.setText("Expire date:    " + stockTime);
        photo.setText("Photo:");
        if (! stockImage.equals("")) {
            image.setImageBitmap(Utils.stringToBitmap(stockImage));
        }
    }

    public void onEditSaveClick(View v) {
        // Read task name from text field
        String FoodName = name.getText().toString();
        String Amount = amount.getText().toString();
        String ExpireDate = expireDate.getText().toString();

        String currFoodName = FoodName.substring(14,FoodName.length());
        String currAmount = Amount.substring(17,Amount.length());
        String currExpireDate = ExpireDate.substring(16,ExpireDate.length());


        // Prepare data intent for sending it back
        Intent intent = new Intent();

        // Pass relevant data back as a result
        if (image.getDrawable().getClass() == BitmapDrawable.class) {
            intent.putExtra("foodImage",
                    Utils.bitmapToString(((BitmapDrawable)image.getDrawable()).getBitmap()));
        } else {
            intent.putExtra("foodImage", "");
        }
        intent.putExtra("foodName", currFoodName);
        intent.putExtra("amount", currAmount);
        intent.putExtra("expireDate", currExpireDate);
        intent.putExtra("position", position);

        // Activity finishes OK, return the data
        setResult(102, intent);     // Set result code and bundle data for response
        finish();                       // Close the activity, pass data to parent
    }

    public void onEditClick(View v) {
        Intent intent = new Intent(StockDetailActivity.this, EditStockActivity.class);
        if (intent != null) {
            // put "extras" into the bundle for access in the detail activity
            if (image.getDrawable().getClass() == BitmapDrawable.class) {
                intent.putExtra("foodImage",
                        Utils.bitmapToString(((BitmapDrawable)image.getDrawable()).getBitmap()));
            } else {
                intent.putExtra("foodImage", "");
            }
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
                if (result.getResultCode() == 101) {
                    // Read all data from data intent sent from edit activity
                    String newFoodImage = result.getData().getStringExtra("foodImage");
                    String newFoodName = result.getData().getStringExtra("foodName");
                    String newAmount = result.getData().getStringExtra("amount");
                    String newExpireDate = result.getData().getStringExtra("expireDate");

                    if (! newFoodImage.equals("")) {
                        image.setImageBitmap(Utils.stringToBitmap(newFoodImage));
                    }
                    name.setText("Food name:    " + newFoodName);
                    amount.setText("Amount:          " + newAmount);
                    expireDate.setText("Expire date:    " + newExpireDate);

//                    // Update data from database
//                    DocumentReference stockRef = db.collection("food").document(newFoodName + "_" + userID);
//                    stockRef.update("name",newFoodName,"amount",newAmount,"time",newExpireDate);

                    // Show notification of update
                    Toast.makeText(getApplicationContext(),
                            "UpdatedInApp: " + newFoodName,
                            Toast.LENGTH_SHORT).show();
                }

                // If user cancelled editing
                else if (result.getResultCode() == RESULT_CANCELED) {
                    // Nothing to do
                }
            }
    );


}