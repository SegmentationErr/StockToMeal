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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StockDetailActivity extends AppCompatActivity {

    private String stockName;

    private TextView name;
    private TextView weight;
    private TextView bb;
    private TextView ad;
    private TextView photo;
    private ImageView image;
    private Button edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail);

        stockName = getIntent().getStringExtra("stock");

        name = (TextView) findViewById(R.id.stock_name);
        weight = (TextView) findViewById(R.id.stock_weight);
        bb = (TextView) findViewById(R.id.stock_bb);
        ad = (TextView) findViewById(R.id.stock_ad);
        photo = (TextView) findViewById(R.id.stock_photo);
        edit = (Button) findViewById(R.id.edit);

        name.setText("Food name:    " + stockName);
        weight.setText("Weight:    ");
        bb.setText("Best before:    ");
        ad.setText("Added date:    ");
        photo.setText("Photo:");
    }

    public void onEditClick(View v) {
        Intent intent = new Intent(StockDetailActivity.this, EditStockActivity.class);
        if (intent != null) {
            // put "extras" into the bundle for access in the detail activity

            // TODO 改成对应的数据！！！！！！！！！！
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

                    // TODO 更新对应数据！！！！！！！！！

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