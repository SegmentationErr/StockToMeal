package comp5216.sydney.edu.au.stocktomeal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RecipeRecommdationActivity extends AppCompatActivity {

    // Search method
    private EditText searchField;
    private Button searchButton;
    private Button stockButton;
    private Button favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_recommdation);

        // search attribute
        searchField = (EditText) findViewById(R.id.search_field);
        searchButton = (Button) findViewById(R.id.search_button);
        stockButton = (Button) findViewById(R.id.stock_button);
        favoriteButton = (Button) findViewById(R.id.favorite_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipeRecommdationActivity.this,
                        SearchResultActivity.class);
                intent.putExtra("search", searchField.getText().toString());
                startActivity(intent);
            }
        });

        stockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipeRecommdationActivity.this,
                        StockListActivity.class);
                startActivity(intent);
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipeRecommdationActivity.this,
                        FavoriteListActivity.class);
                startActivity(intent);
            }
        });
    }
}