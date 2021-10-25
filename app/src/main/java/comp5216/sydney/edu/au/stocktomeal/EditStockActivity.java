package comp5216.sydney.edu.au.stocktomeal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditStockActivity extends AppCompatActivity {

    private EditText foodNameView;
    private EditText foodAmountView;
    private EditText expireDateView;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stock);

        String foodName = getIntent().getStringExtra("foodName");
        String amount = getIntent().getStringExtra("amount");
        String expireDate = getIntent().getStringExtra("expireDate");

        foodNameView = (EditText) findViewById(R.id.foodNameView);
        foodAmountView = (EditText) findViewById(R.id.editFoodAmount);
        expireDateView = (EditText) findViewById(R.id.expireDateView);

        foodNameView.setText(foodName);
        foodAmountView.setText(amount);
        expireDateView.setText(expireDate);

        setupDateListener();
    }

    /**
     * Setup Listener on Date edit text filed to enable Date picker when user click it.
     */
    private void setupDateListener() {
        expireDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupDatePicker();          // Setup listener
                datePickerDialog.show();    // Show Date picker dialog
            }
        });
    }

    /**
     * Get date from date text filed and initialise a Date picker dialog.
     */
    private void setupDatePicker() {
        // Read date from date text filed and separate into year/month/day
        String prevDate[] = expireDateView.getText().toString().split(" - ");
        int year = Integer.parseInt(prevDate[0]);
        int month = Integer.parseInt(prevDate[1]) - 1;
        int day = Integer.parseInt(prevDate[2]);

        // Initialise Date picker dialog
        datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Panel,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String s = String.format("%4d - %02d - %02d", year, month+1, day);
                        expireDateView.setText(s);
                    }
                }, year, month, day);
    }

    public void onDecreaseClick(View v) {
        int amount = Integer.parseInt(foodAmountView.getText().toString());
        if (amount <= 1) return;
        foodAmountView.setText(String.valueOf(amount - 1));
    }

    public void onIncreaseClick(View v) {
        int amount = Integer.parseInt(foodAmountView.getText().toString());
        foodAmountView.setText(String.valueOf(amount + 1));
    }

    public void onCancelClick(View v) {
        Intent data = new Intent();

        // Activity finishes OK, return the data
        // Set result code and bundle data for response
        setResult(RESULT_CANCELED, data);
        finish();
    }

    public void onSaveClick(View v) {
        // Read task name from text field
        String foodName = foodNameView.getText().toString();
        String amount = foodAmountView.getText().toString();
        String expireDate = expireDateView.getText().toString();

        // Handle invalid input and popup a warning message
        if (foodName.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Task Name Cannot Be Empty !",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data intent for sending it back
        Intent data = new Intent();

        // Pass relevant data back as a result
        data.putExtra("foodName", foodName);
        data.putExtra("amount", amount);
        data.putExtra("expireDate", expireDate);

        // Activity finishes OK, return the data
        setResult(RESULT_OK, data);     // Set result code and bundle data for response
        finish();                       // Close the activity, pass data to parent
    }
}
