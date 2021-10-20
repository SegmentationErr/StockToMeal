package comp5216.sydney.edu.au.stocktomeal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SearchResultActivity extends AppCompatActivity {

    private String keywords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        Intent intent = getIntent();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (intent != null && intent.getStringExtra("search") != null) {
            keywords = intent.getStringExtra("search");
            System.out.println("Terminal Print Keywords: " + keywords);
        } else {
            System.out.println("Intent doesn't put any keywords in.");
            startActivity(new Intent(SearchResultActivity.this,
                    RecipeRecommdationActivity.class) );
        }


        /** Use ingredients to filter the search result
        db.collection("recipe").whereEqualTo("name", keywords).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // ...
                    }
                });*/


    }
}