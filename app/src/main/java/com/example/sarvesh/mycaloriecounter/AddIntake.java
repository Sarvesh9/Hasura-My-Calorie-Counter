package com.example.sarvesh.mycaloriecounter;

import android.content.Intent;
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
    private Integer totalCal;
    private String todaydate;
    private MultiAutoCompleteTextView mt;

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
        Integer dd = cal.get(Calendar.DAY_OF_MONTH);
        Integer mm = cal.get(Calendar.MONTH);
        Integer yy = cal.get(Calendar.YEAR);
        // set current date into TextView

        todaydate = dd + "-" + (mm+1) + "-" + yy;
        Log.i("Date",todaydate);

        Date.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(cal.get(Calendar.DAY_OF_MONTH)).append("-").append(cal.get(Calendar.MONTH) + 1).append("-")
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
                fetchCaloriesFromDB();
                //validation
                // meal type is stored in mealtype
                mealtype = mt.getEditableText().toString();
                if(mealtype.equals("")){
                    Toast.makeText(AddIntake.this,"Please enter a meal type.",Toast.LENGTH_SHORT).show();
                    mt.requestFocus();
                    return;
                }
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
                if(!mealtype.equals("Breakfast") && !mealtype.equals("Lunch") && !mealtype.equals("Snacks") && !mealtype.equals("Dinner")){
                    Toast.makeText(AddIntake.this,"Please enter a valid value for Meal.",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
    private void fetchCaloriesFromDB(){
        try {
            JSONArray columnsArray = new JSONArray();
            columnsArray.put("fooditem");
            columnsArray.put("calories");

            JSONObject args = new JSONObject();
            args.put("table", "Meal");
            args.put("columns",columnsArray);

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
                                String myfood =record.getFooditem();
                                Integer mycal = record.getCalories();
                                Log.i("Food",food.getEditableText().toString());
                                Log.i("Food name",myfood);
                                Log.i("Calories","Value = " + mycal);
                                if(myfood.equalsIgnoreCase(food.getEditableText().toString())){
                                    Integer quant = Integer.parseInt(quantity.getText().toString());
                                    totalCal =  mycal * quant;
                                    Toast.makeText(AddIntake.this,"Selected Food Item : " + food.getText().toString() + "\n Calorie Count : " + totalCal,Toast.LENGTH_SHORT).show();
                                    storeCalorie();
                                    Intent myIntent = new Intent(AddIntake.this, TotalIntakeView.class);
                                    startActivity(myIntent);
                                    return;
                                }
                            }
                            Toast.makeText(AddIntake.this,"Sorry this food item is not present in our Database.",Toast.LENGTH_SHORT).show();
                            food.setText("");
                            quantity.setText("");
                            mt.setText("");
                            mt.requestFocus();
                            return;
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

    private void storeCalorie(){
        try{
            Log.i("TotalCal","value" + totalCal);
            Integer quant = Integer.parseInt(quantity.getText().toString());
            JSONObject nameJSON = new JSONObject();
            nameJSON.put("user_id", user.getId());
            nameJSON.put("food_item",food.getEditableText().toString());
            nameJSON.put("calories",totalCal);
            nameJSON.put("date",todaydate.toString());
            nameJSON.put("quantity",quant);
            nameJSON.put("mealtype",mealtype.toString());

            JSONArray colsList = new JSONArray();
            colsList.put(nameJSON);

            JSONObject args = new JSONObject();
            args.put("table", "Intake");
            args.put("objects", colsList);

            JSONObject insertUserJSON = new JSONObject();
            insertUserJSON.put("type", "insert");
            insertUserJSON.put("args", args);
            Log.i("ResponseRecord", insertUserJSON.toString());
            client.useDataService()
                    .setRequestBody(insertUserJSON)
                    .expectResponseType(InsertIntake.class)
                    .enqueue(new Callback<InsertIntake, HasuraException>() {
                        @Override
                        public void onSuccess(InsertIntake insertIntake) {

                        }

                        @Override
                        public void onFailure(HasuraException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (JSONException e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private final String[] MEALTYPE = new String[] {
            "Breakfast", "Lunch", "Snacks", "Dinner"
    };
}
