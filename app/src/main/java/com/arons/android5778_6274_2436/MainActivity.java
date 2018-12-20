package com.arons.android5778_6274_2436;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arons.android5778_6274_2436.Model.Backend.DBManager;
import com.arons.android5778_6274_2436.Model.Backend.DBManager_Factory;
import com.arons.android5778_6274_2436.Model.Backend.FieldCheck;
import com.arons.android5778_6274_2436.Model.Backend.MapsFunction;
import com.arons.android5778_6274_2436.Model.Entities.Classes.MyLocation;
import com.arons.android5778_6274_2436.Model.Entities.Classes.Ride;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;


public class MainActivity extends Activity implements View.OnClickListener {


    private EditText _name;
    private EditText _phoneNumber;
    private EditText _mail;
    private EditText _startLocation;
    private EditText _endLocation;
    private Button buttonGet;
    DBManager mydb;
    private FusedLocationProviderClient mFusedLocationClient;


    private void findViews() {
        _name = ((EditText) findViewById(R.id.editName));
        _phoneNumber = (EditText) findViewById(R.id.editPhone);
        _mail = (EditText) findViewById(R.id.editMail);
        _startLocation = (EditText) findViewById(R.id.editDep);
        _endLocation = (EditText) findViewById(R.id.editDes);
        buttonGet = (Button) findViewById(R.id.button);
        buttonGet.setOnClickListener(this);
    }

    @SuppressLint("StaticFieldLeak")
    private void addRide() {
        try {
            final Ride newRide = new Ride();

            if (checkFields()) {

                //MyLocation newLocation = getCurrentLocation();


                // Google Maps Task + Firebase task
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            MyLocation endLocation = MapsFunction.StringToLocation(_endLocation.getText().toString(), getApplicationContext());
                            MyLocation startLocation = MapsFunction.StringToLocation(_startLocation.getText().toString(), getApplicationContext());
                            newRide.setEndLocation(endLocation);
                            newRide.setStartLocation(startLocation);
                        } catch (Exception e) {
                            messageBox("Error", e.getMessage() + "\n\n" + e.getCause().getMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        // Firebase task
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... voids) {
                                try {
                                    mydb.addNewRide(newRide, getApplicationContext());

                                } catch (Exception e) {
                                    messageBox("Error", e.getMessage() + "\n\n" + e.getCause().getMessage());
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                Toast.makeText(MainActivity.this, "You have ordered a Taxi", Toast.LENGTH_SHORT).show();
                            }
                        }.execute();

                    }
                }.execute();


                newRide.setMailOfCustomer(this._mail.getText().toString());
                newRide.setNameOfCustomer(this._name.getText().toString());
                newRide.setPhoneNumberOfCustomer(this._phoneNumber.getText().toString());
                newRide.setBeginningTime(new Date());
            }

        } catch (Exception e) {
            messageBox("Error", e.getMessage() + "\n\n" + e.getCause().getMessage());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mydb = DBManager_Factory.getInstance();
        super.onCreate(savedInstanceState);
        try {
           // mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }
        catch(Exception e){
            messageBox("Error",e.getMessage());
        }

        setContentView(R.layout.activity_main);

        findViews();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button) {
            addRide();
        }
    }

    private void messageBox(String method, String message) {
        Log.d("EXCEPTION: " + method, message);

        AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        messageBox.setTitle(method);
        messageBox.setMessage(message);
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }

    private boolean checkFields() {
        boolean emailValid = FieldCheck.isEmailValid(this._mail.getText().toString());
        boolean phoneValid = FieldCheck.isPhoneNumberValid(this._phoneNumber.getText().toString());

        if (!emailValid) {
            messageBox("Argument Error", "Email is not valid");
            return false;
        }
        if (!phoneValid) {
            messageBox("Argument Error", "Phone is not valid");
            return false;
        }
        return true;
    }

    private MyLocation getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        Task<Location> t = mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onSuccess(final Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            try{
                                MyLocation newLocation = MapsFunction.StringToLocation(location.toString(),getApplicationContext());
                            }
                            catch(Exception e)
                            {

                            }
                        }

                    }
                });
        return new MyLocation("test");
    }
}


