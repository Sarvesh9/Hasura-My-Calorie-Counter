package com.example.sarvesh.mycaloriecounter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import io.hasura.sdk.responseListener.LogoutResponseListener;

import static com.example.sarvesh.mycaloriecounter.R.id.breakfastCal;

public class TotalIntakeView extends AppCompatActivity {

    public TextView breakfast;
    public TextView lunch;
    public TextView snacks;
    public TextView dinner;
    public TextView goal;
    private TextView txtresult;
    private Integer result;
    private ImageView logout;
    private String todaydate;
    private String TodayDate;
    private TextView Date;
    private Button btnaddFood;
    private Button btnSubmit;
    private LinearLayout linearLayout;;

    HasuraUser user;
    HasuraClient client;

    int[] foodCalorie = new int[4];
    public Integer reqcal;
    public Integer brkcal=0;
    public Integer lunchcal=0;
    public Integer snackcal=0;
    public Integer dinnercal=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_intake_view);

        this.onResume();

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnaddFood = (Button) findViewById(R.id.btnAddFood);
        breakfast =  (TextView) findViewById(breakfastCal);
        lunch =  (TextView) findViewById(R.id.lunchCal);
        snacks =  (TextView) findViewById(R.id.snacksCal);
        dinner =  (TextView) findViewById(R.id.dinnerCal);
        goal = (TextView) findViewById(R.id.txtviewGoalCalorie);
        txtresult = (TextView) findViewById(R.id.txtAddF);
        Date =  (TextView) findViewById(R.id.txtDate);
        logout = (ImageView) findViewById(R.id.imgLogout);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.setVisibility(LinearLayout.GONE);

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

        final Calendar cal = Calendar.getInstance();
        Integer dd = cal.get(Calendar.DAY_OF_MONTH);
        Integer mm = cal.get(Calendar.MONTH);
        Integer yy = cal.get(Calendar.YEAR);
        // set current date into TextView

        todaydate = dd + "-" + (mm+1) + "-" + yy;

        TodayDate = todaydate;
        Log.i("Date",todaydate);

        Date.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(cal.get(Calendar.DAY_OF_MONTH)).append("-").append(cal.get(Calendar.MONTH) + 1).append("-")
                .append(cal.get(Calendar.YEAR)));

//        Fetching required calories for he current user
        fetchReqCalorie();

//        Fetching value from DB for each Textview
        brkfastfetchCaloriesDB();
        lunchfetchCaloriesDB();
        snackfetchCaloriesDB();
        dinnerfetchCaloriesDB();

        Log.i("Brkcal","value = " + foodCalorie[0]);
        Log.i("Lunchcal","value = " + foodCalorie[1]);
        Log.i("Snackcal","value = " + foodCalorie[2]);
        Log.i("Dinnercal","value = " + foodCalorie[3]);

        btnaddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(TotalIntakeView.this, AddIntake.class);
                startActivity(myIntent);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result = brkcal + lunchcal + snackcal + dinnercal;
                linearLayout.setVisibility(LinearLayout.VISIBLE);
                txtresult.setText("Your Intake of Calories today is " + result + "cal.");
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogout();
            }
        });
    }
    private void userLogout(){
        final AlertDialog.Builder signOutAlert = new AlertDialog.Builder(this);
        signOutAlert.setTitle("Sign Out");
        signOutAlert.setMessage("Are you sure you want to sign out?");
        signOutAlert.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        signOutAlert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.logout(new LogoutResponseListener() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(TotalIntakeView.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(TotalIntakeView.this, Login.class);
                        startActivity(myIntent);
                    }

                    @Override
                    public void onFailure(HasuraException e) {
                        Toast.makeText(TotalIntakeView.this, "Couldn't logout.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        signOutAlert.show();
    }
    private void fetchReqCalorie(){
        try{
            JSONArray columnsArray = new JSONArray();;
            columnsArray.put("req_cal");

            JSONObject args = new JSONObject();
            args.put("table", "Personal_info");
            args.put("columns",columnsArray);

            JSONObject reqquery = new JSONObject();
            reqquery.put("user_id",user.getId());

            args.put("where", reqquery);

            JSONObject selectCalorieJSON = new JSONObject();
            selectCalorieJSON.put("type", "select");
            selectCalorieJSON.put("args", args);
            Log.i("ResponseRecord", selectCalorieJSON.toString());
            client.useDataService()
                    .setRequestBody(selectCalorieJSON)
                    .expectResponseTypeArrayOf(RequiredCalorieResponse.class)
                    .enqueue(new Callback<List<RequiredCalorieResponse>, HasuraException>() {
                        @Override
                        public void onSuccess(List<RequiredCalorieResponse> response) {
                            for (RequiredCalorieResponse record:response) {
                                reqcal = record.getReq_cal();
                            }
                            goal.setText(Integer.toString(reqcal));
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
    private void brkfastfetchCaloriesDB(){
        try{
            JSONArray columnsArray = new JSONArray();;
            columnsArray.put("calories");

            JSONObject args = new JSONObject();
            args.put("table", "Intake");
            args.put("columns",columnsArray);

            JSONObject brkquery = new JSONObject();
            brkquery.put("user_id",user.getId());
            brkquery.put("date",todaydate.toString());
            brkquery.put("mealtype","Breakfast");

            args.put("where", brkquery);

            JSONObject selectIntakeJSON = new JSONObject();
            selectIntakeJSON.put("type", "select");
            selectIntakeJSON.put("args", args);
            Log.i("ResponseRecord", selectIntakeJSON.toString());
            client.useDataService()
                    .setRequestBody(selectIntakeJSON)
                    .expectResponseTypeArrayOf(CalorieResponse.class)
                    .enqueue(new Callback<List<CalorieResponse>, HasuraException>() {
                        @Override
                        public void onSuccess(List<CalorieResponse> response) {
                            for (CalorieResponse record:response) {
                                Integer cal = record.getCalories();
                                brkcal = brkcal + cal;
                                Log.i("Breakfast", "value =" + brkcal);
                            }
                            foodCalorie[0]= brkcal;
                            breakfast.setText(Integer.toString(foodCalorie[0]));
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
    private void lunchfetchCaloriesDB(){
        try{
            JSONArray columnsArray = new JSONArray();
            columnsArray.put("calories");

            JSONObject args = new JSONObject();
            args.put("table", "Intake");
            args.put("columns",columnsArray);

            JSONObject lunchquery = new JSONObject();
            lunchquery.put("user_id",user.getId());
            lunchquery.put("date",todaydate.toString());
            lunchquery.put("mealtype","Lunch");

            args.put("where", lunchquery);

            JSONObject selectIntakeJSON = new JSONObject();
            selectIntakeJSON.put("type", "select");
            selectIntakeJSON.put("args", args);
            Log.i("ResponseRecord", selectIntakeJSON.toString());
            client.useDataService()
                    .setRequestBody(selectIntakeJSON)
                    .expectResponseTypeArrayOf(CalorieResponse.class)
                    .enqueue(new Callback<List<CalorieResponse>, HasuraException>() {
                        @Override
                        public void onSuccess(List<CalorieResponse> response) {
                            for (CalorieResponse record:response) {
                                lunchcal = lunchcal + record.getCalories();
                            }
                            foodCalorie[1]=lunchcal;
                            lunch.setText(Integer.toString(foodCalorie[1]));
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
    private void snackfetchCaloriesDB(){
        try{
            JSONArray columnsArray = new JSONArray();
            columnsArray.put("calories");

            JSONObject args = new JSONObject();
            args.put("table", "Intake");
            args.put("columns",columnsArray);

            JSONObject snackquery = new JSONObject();
            snackquery.put("user_id",user.getId());
            snackquery.put("date",todaydate.toString());
            snackquery.put("mealtype","Snacks");

            args.put("where", snackquery);

            JSONObject selectIntakeJSON = new JSONObject();
            selectIntakeJSON.put("type", "select");
            selectIntakeJSON.put("args", args);
            Log.i("ResponseRecord", selectIntakeJSON.toString());
            client.useDataService()
                    .setRequestBody(selectIntakeJSON)
                    .expectResponseTypeArrayOf(CalorieResponse.class)
                    .enqueue(new Callback<List<CalorieResponse>, HasuraException>() {
                        @Override
                        public void onSuccess(List<CalorieResponse> response) {
                            for (CalorieResponse record:response) {
                                snackcal = snackcal + record.getCalories();
                            }
                            foodCalorie[2] = snackcal;
                            snacks.setText(Integer.toString(foodCalorie[2]));
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
    private void dinnerfetchCaloriesDB(){
        try{
            JSONArray columnsArray = new JSONArray();
            columnsArray.put("calories");

            JSONObject args = new JSONObject();
            args.put("table", "Intake");
            args.put("columns",columnsArray);

            JSONObject dinnerquery = new JSONObject();
            dinnerquery.put("user_id",user.getId());
            dinnerquery.put("date",todaydate.toString());
            dinnerquery.put("mealtype","Dinner");

            args.put("where", dinnerquery);

            JSONObject selectIntakeJSON = new JSONObject();
            selectIntakeJSON.put("type", "select");
            selectIntakeJSON.put("args", args);
            Log.i("ResponseRecord", selectIntakeJSON.toString());
            client.useDataService()
                    .setRequestBody(selectIntakeJSON)
                    .expectResponseTypeArrayOf(CalorieResponse.class)
                    .enqueue(new Callback<List<CalorieResponse>, HasuraException>() {
                        @Override
                        public void onSuccess(List<CalorieResponse> response) {
                            for (CalorieResponse record:response) {
                                dinnercal = dinnercal + record.getCalories();
                            }
                            foodCalorie[3] = dinnercal;
                            dinner.setText(Integer.toString(foodCalorie[3]));
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
}
