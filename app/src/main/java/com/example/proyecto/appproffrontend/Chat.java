package com.example.proyecto.appproffrontend;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Rubenbros on 29/01/2018.
 */

public class Chat extends AppCompatActivity {
    private TextView other;
    private TextView messages;
    private EditText message;
    private Button sender;
    private InfoSesion info;

    //for google maps
    private SharedPreferences sharedPref;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String prof = savedInstanceState.getString("other");
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
        DatabaseReference otherUserDB = mDatabase.child(prof);
        boolean existe = currentUserDB.child(prof) != null;
        if (existe) currentUserDB.child(savedInstanceState.getString("other")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("messages", dataSnapshot.getValue(String.class)).apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        else{
            currentUserDB.child(prof).setValue(new String(""));
            otherUserDB.child(prof).setValue(new String(""));
        }
    }
}
