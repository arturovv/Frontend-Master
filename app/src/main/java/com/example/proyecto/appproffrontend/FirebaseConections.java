package com.example.proyecto.appproffrontend;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.quickstart.database.models.Post;
//import com.google.firebase.quickstart.database.models.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Usuario on 23/12/2017.
 */

public class FirebaseConections extends AppCompatActivity {


    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


    public String tranformarEmail(String email){
        return email.replace(".","?");
    }

    // Importante acordarse de añadir longitud y latutid al profesor
    public void writeNewTeacherLocation(FirebaseAuth mAuth, String email, String ciudad, String longitud, String latitud) {
        DatabaseReference currentUserDB = mDatabase.child(tranformarEmail(email));

        currentUserDB.child("email").setValue(tranformarEmail(email));
        currentUserDB.child("longitud").setValue(longitud);
        currentUserDB.child("ciudad").setValue(ciudad);
        currentUserDB.child("latitud").setValue(latitud);
    }


    // [START basic_write]
    public void writeNewUser(FirebaseAuth mAuth, String usr) {
        DatabaseReference currentUserDB = mDatabase.child(tranformarEmail(usr));
        currentUserDB.setValue(usr);
    }
    // [END basic_write]

    public void modifyTeacher(FirebaseAuth mAuth, String ciudad, String longitud, String latitud) {
        DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
        if (longitud!=null) {
            currentUserDB.child("longitud").setValue(longitud);
        }
        if (ciudad!=null) {
            currentUserDB.child("ciudad").setValue(ciudad);
        }
        if (latitud!=null) {
            currentUserDB.child("latitud").setValue(latitud);
        }
    }
}