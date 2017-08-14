package com.example.sarvesh.mycaloriecounter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.hasura.sdk.Callback;
import io.hasura.sdk.Hasura;
import io.hasura.sdk.HasuraClient;
import io.hasura.sdk.HasuraUser;
import io.hasura.sdk.ProjectConfig;
import io.hasura.sdk.exception.HasuraException;
import io.hasura.sdk.exception.HasuraInitException;

public class AddIntake extends AppCompatActivity {

    private Button btnIntake;
    private TextView Date;
    private TextView food;
    private TextView quantity;
    private String mealtype;

    ArrayList<String> foodName = new ArrayList<String>();
    ArrayList<Integer> calCount = new ArrayList<Integer>();
    //mylist.add(mystring); this adds an element to the list.

    HasuraUser user;
    HasuraClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_intake);

        try {
            Hasura.setProjectConfig(new ProjectConfig.Builder()
                    .setCustomBaseDomain("becalmed98.hasura-app.io")
                    //.enableOverHttp()
                    .build())
                    .initialise(this);
        } catch (HasuraInitException e) {
            e.printStackTrace();
        }
        client = Hasura.getClient();
        user = client.getUser();

        btnIntake = (Button) findViewById(R.id.btnIntake);
        Date = (TextView) findViewById(R.id.txtDate);
        food = (TextView) findViewById(R.id.editTextFood);
        quantity = (TextView) findViewById(R.id.editTextQuantity);
        final Calendar cal = Calendar.getInstance();
        // dd = cal.get(Calendar.DAY_OF_MONTH);
        //mm = cal.get(Calendar.MONTH);
        //yy = cal.get(Calendar.YEAR);
        // set current date into TextView
        Date.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(cal.get(Calendar.DAY_OF_MONTH)).append("-").append((cal.get(Calendar.MONTH)) + 1).append("-")
                .append(cal.get(Calendar.YEAR)));

        final MultiAutoCompleteTextView mt = (MultiAutoCompleteTextView)
                findViewById(R.id.mealType);

        mt.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, MEALTYPE);

        mt.setThreshold(1);
        mt.setAdapter(adp);
        mt.setTokenizer(new SpaceTokenizer());

        btnIntake.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //validation
                if (food.getText().length() == 0) {
                    Toast.makeText(AddIntake.this, "Please enter the food item.", Toast.LENGTH_LONG).show();
                    food.setText("");
                    food.requestFocus();
                    return;
                }
                if (quantity.getText().length() == 0) {
                    Toast.makeText(AddIntake.this, "Specify the quantity.", Toast.LENGTH_LONG).show();
                    quantity.setText("");
                    quantity.requestFocus();
                    return;
                }
                // meal type is stored in type
                mealtype = mt.getEditableText().toString();
                if(mealtype.equals("")){
                    Toast.makeText(AddIntake.this,"Please enter value",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!mealtype.equals("Breakfast") && !mealtype.equals("Lunch") && !mealtype.equals("Snacks") && !mealtype.equals("Dinner")){
                    Toast.makeText(AddIntake.this,"Please enter a valid value",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mealtype.equals("Breakfast") || mealtype.equals("Snacks")){
                    mealtype = "Breakfast";
                }
                if(mealtype.equals("Lunch") || mealtype.equals("Dinner")){
                    mealtype = "Lunch";
                }
                fetchCaloriesFromDB();
            }
        });
    }
    private void fetchCaloriesFromDB(){
        try {

            JSONArray columnsArray = new JSONArray();
            columnsArray.put("Item");
            columnsArray.put("calories");

            JSONObject args = new JSONObject();
            args.put("table", mealtype);
            args.put("columns",columnsArray);

//            JSONObject foodname = new JSONObject();
//            foodname.put("Item",food.getEditableText().toString());
//
//            args.put("where",foodname);

            JSONObject selectIntakeQuery = new JSONObject();
            selectIntakeQuery.put("type", "select");
            selectIntakeQuery.put("args", args);
            Log.i("ResponseRecord", selectIntakeQuery.toString());
            client.useDataService()
                    .setRequestBody(selectIntakeQuery)
                    .expectResponseTypeArrayOf(IntakeRecord.class)
                    .enqueue(new Callback<List<IntakeRecord>, HasuraException>() {
                        @Override
                        public void onSuccess(List<IntakeRecord> response) {
                            for (IntakeRecord record:response) {
                                String myfood =record.getItem();
                                foodName.add(myfood);
                                Integer mycal = record.getCalories();
                                calCount.add(mycal);
                            }
                            Log.i("FoodName",foodName.toString());
                            Log.i("CalCount",calCount.toString());
                        }

                        @Override
                        public void onFailure(HasuraException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private final String[] MEALTYPE = new String[] {
            "Breakfast", "Lunch", "Snack", "Dinner"
    };
}
