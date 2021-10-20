package comp5216.sydney.edu.au.stocktomeal;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RecipeDetailActivity extends AppCompatActivity {

    private String recipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        Intent intent = getIntent();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (intent != null && intent.getStringExtra("search") != null) {
            recipeName = intent.getStringExtra("search");
            System.out.println("Terminal Print Keywords: " + recipeName);
        } else {
            System.out.println("Intent doesn't put any keywords in.");
            startActivity(new Intent(RecipeDetailActivity.this,
                    RecipeRecommdationActivity.class) );
        }

        DocumentReference docRef = db.collection("recipe").document(recipeName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
}