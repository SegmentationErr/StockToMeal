package comp5216.sydney.edu.au.stocktomeal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
}
