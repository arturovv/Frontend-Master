package com.example.proyecto.appproffrontend;

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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * Created by Rubenbros on 14/04/2017.
 */

public class Registro2 extends AppCompatActivity {

    private static final String TAG = null;
    private String usr;
    private String psw;
    private Facade facade;
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private EditText ciudad;
    private EditText experiencia;
    private EditText email;
    private EditText tlf;
    private FirebaseAuth mAuth;
    private FirebaseUser ses_user;
    private Intent i = null;

    private API api;
    private InfoSesion info;

    // anyadido adrian
    private EditText longit;
    private EditText latit;
    // stop anyadido

    // BD firebase
    FirebaseConections firebaseConections=new FirebaseConections();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    //google maps code
    private FusedLocationProviderClient mFusedLocationClient;
    private int LOCATION_REQUEST_CODE;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        info = new InfoSesion(this);
        int prof = info.getTipo();
        api = new API(this);

        if (prof == 1) setContentView(R.layout.activity_registro2_profesor);
        else setContentView(R.layout.activity_registro2_alumno);

        //code for google auth
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) { //user already with firebase account but without old cloud account
            if(prof == 0) onlySaveInOldCloud(user);
            else updateUI(user);
        }

        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm);

        if (prof == 1) {
            email = (EditText) findViewById(R.id.email);
            tlf = (EditText) findViewById(R.id.telefono);
            ciudad = (EditText) findViewById(R.id.ciudadProfesorReg);
            longit = (EditText) findViewById(R.id.longitud);
            latit = (EditText) findViewById(R.id.latitud);
            experiencia = (EditText) findViewById(R.id.experienciaProfesorReg);
            Button siguiente = (Button) findViewById(R.id.siguiente);
            //debug username.setText(String.valueOf(info.getErrorRegProf()));
            switch (this.getSharedPreferences("APPROF", Context.MODE_PRIVATE).getInt("error", 0)) {
                case 1:
                    email.setError("Invalid email.");
                    this.getSharedPreferences("APPROF", Context.MODE_PRIVATE).edit().putInt("error", 0).apply();//reset error
                    break;
                case 2:
                    email.setError("Email used before.");
                    this.getSharedPreferences("APPROF", Context.MODE_PRIVATE).edit().putInt("error", 0).apply();//reset error
                    break;
                case 3:
                    password.setError("Too weak.");
                    confirmPassword.setError("Too weak.");
                    this.getSharedPreferences("APPROF", Context.MODE_PRIVATE).edit().putInt("error", 0).apply();//reset error
            }
            i = new Intent(this, Registro3.class);
            siguiente.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    final int code = guardarEnBdProf(i);
                    if (code == -1) startActivity(i);
                    else error(code);
                }
            });

            //google maps code
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            ((Button) findViewById(R.id.btnLocation)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Toast.makeText(Registro2.this, "Searching...",
                            Toast.LENGTH_SHORT).show();
                    ciudad.setText("");
                    getLocation();
                }
            });


        } else {
            Button registro = (Button) findViewById(R.id.register);
            i = new Intent(this, Busqueda_Profesores.class);
            registro.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    findViewById(R.id.register).setVisibility(View.GONE);
                    Toast.makeText(Registro2.this, "Loading...",
                            Toast.LENGTH_SHORT).show();
                    error(guardarEnBdAl());
                }
            });
        }

    }

    private int guardarEnBdProf(final Intent i) {

        //code for google auth
        String cpsw = "";
        FirebaseUser user = mAuth.getCurrentUser();
        if(user !=null) {
            psw = "ZwgMAEioujBT4fZPAizP"; //PASSWORD IN OLD CLOUD FOR ALL GOOGLE-USERS
            cpsw = "ZwgMAEioujBT4fZPAizP";

        } else {
            psw = password.getText().toString();
            cpsw = confirmPassword.getText().toString();
        }//end of code for google auth

        usr = username.getText().toString();
        String mail = email.getText().toString();
        String phone = tlf.getText().toString();
        String city = ciudad.getText().toString();
        String experience = experiencia.getText().toString();

        // anyadido adrian
        String longitud=  longit.getText().toString();
        String latitud= latit.getText().toString();
        // stop anyadido

        if (usr.isEmpty()) return 0;
        else if (psw.isEmpty()) return 1;
        else if (cpsw.isEmpty()) return 2;
        else if (!cpsw.equals(psw)) return 3;
        else if (mail.isEmpty()) return 4;
        else if (!mail.matches("[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+")) return 5;
        else if (phone.isEmpty()) return 6;
        else if (!phone.matches("[0-9]{9}")) return 7;
        else if (city.isEmpty()) return 9;
        else if (longitud.isEmpty()) return 11;
        else if (latitud.isEmpty()) return 12;

        i.putExtra("profesor_user", usr);
        i.putExtra("profesor_psw", psw);
        i.putExtra("profesor_tlf", phone);
        i.putExtra("profesor_mail", mail);
        i.putExtra("profesor_ciu", city);
        i.putExtra("profesor_exp", experience);
        i.putExtra("profesor_long",longitud);
        i.putExtra("profesor_lat",latitud);
        return -1;
    }

    private int guardarEnBdAl() {
        usr = username.getText().toString();
        psw = password.getText().toString();
        if(!validateForm()) {
            findViewById(R.id.register).setVisibility(View.VISIBLE);
            return -1;
        }

        //Guardar en base de datos
        CheckBox tyc = (CheckBox) findViewById(R.id.TyC);
        if (!tyc.isChecked()) {
            findViewById(R.id.register).setVisibility(View.VISIBLE);
            return 8;
        }
        int error = -1;
        mAuth.createUserWithEmailAndPassword(usr, psw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {int error = 10;
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            ses_user = mAuth.getCurrentUser();

                            // adrian code
                            firebaseConections.writeNewUser(mAuth,usr);

                            // finalitation adrian super code

                            facade = new Facade(api);
                            try {
                                error = facade.registro_alumno(new AlumnoVO(usr, psw));
                                info.set(username.getText().toString(),0);
                                info.setSession(ses_user);
                                facade.login(new PersonaVO(usr,psw),0);
                                startActivity(i);
                            } catch (APIexception ex) { error = 10; }

                        } else {
                            // If sign in fails, display a message to the user.
                            Exception e = task.getException();
                            Log.w(TAG, "createUserWithEmail:failure", e);
                            Toast.makeText(Registro2.this, "An error has occurred.",
                                    Toast.LENGTH_SHORT).show();
                            ses_user = null;

                            try {
                                throw e;
                            } catch(FirebaseAuthWeakPasswordException e1) {
                                password.setError("Too weak.");
                                password.requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e1) {
                                username.setError("Invalid email.");
                                username.requestFocus();
                            } catch(FirebaseAuthUserCollisionException e1) {
                                username.setError("Email used before.");
                                username.requestFocus();
                            } catch(Exception e1) {
                                error = 100;
                            }
                            findViewById(R.id.register).setVisibility(View.VISIBLE);
                        }

                    }
                });

        return error;
    }

    private void error(int code) {
        if(code != -1) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            switch (code) {
                case 0:
                    dlgAlert.setMessage("Introduce an username");
                    break;
                case 1:
                    dlgAlert.setMessage("Introduce a password");
                    break;
                case 2:
                    dlgAlert.setMessage("Repeat your password");
                    break;
                case 3:
                    dlgAlert.setMessage("Passwords do not match");
                    break;
                case 4:
                    dlgAlert.setMessage("Introduce an e-mail");
                    break;
                case 5:
                    dlgAlert.setMessage("Introduce a valid e-mail");
                    break;
                case 6:
                    dlgAlert.setMessage("Introduce a phone number");
                    break;
                case 7:
                    dlgAlert.setMessage("Introduce a valid phone number");
                    break;
                case 8:
                    dlgAlert.setMessage("You must accept the Terms & Conditions");
                    break;
                case 9:
                    dlgAlert.setMessage("Error with Location. Try again.");
                case 10:
                    dlgAlert.setMessage("Registration error. Try again");
                // anyadido adrian
                case 11:
                    dlgAlert.setMessage("Error with Location. Try again.");
                case 12:
                    dlgAlert.setMessage("Error with Location. Try again.");
                // stop anyadido
                default:
                    dlgAlert.setMessage("Error desconocido");
            }

            dlgAlert.setTitle("Error...");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = username.getText().toString();
        if (TextUtils.isEmpty(email)) {
            username.setError("Required.");
            valid = false;
        } else {
            username.setError(null);
        }

        String pass = password.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            password.setError("Required.");
            valid = false;
        } else if (pass.length() < 4) {
            password.setError("Too short.");
            valid = false;
        } else {
            password.setError(null);
        }

        String pass2 = confirmPassword.getText().toString();
        if(!pass.equals(pass2)) {
            confirmPassword.setError("Not equal.");
            valid = false;
        } else {
            confirmPassword.setError(null);
        }

        return valid;
    }

    //code for google auth
    private void onlySaveInOldCloud(FirebaseUser user) {
        usr = user.getEmail();
        psw = "ZwgMAEioujBT4fZPAizP"; //PASSWORD IN OLD CLOUD FOR ALL GOOGLE-USERS
        firebaseConections.writeNewUser(mAuth, usr);
        Intent i0 = new Intent(this, Busqueda_Profesores.class);
        Intent error = new Intent(this, LoginActivity.class);

        facade = new Facade(api);
        try {
            facade.registro_alumno(new AlumnoVO(user.getEmail(), psw));
            info.set(usr, 0);
            info.setSession(user);
            facade.login(new PersonaVO(user.getEmail(), psw), 0);
            startActivity(i0);
        } catch (APIexception ex) {
            Toast.makeText(Registro2.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
            startActivity(error);
        } /*catch (Exception e) {
            Toast.makeText(Registro2.this, "An error has occurred.",
                    Toast.LENGTH_SHORT).show();
            startActivity(error);
        }*/
    }

    //code for google auth
    private void updateUI(FirebaseUser user) {
        //Profesor que ya tiene cuenta en firebase

        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm);
        email = (EditText) findViewById(R.id.email);
        email.setText(user.getEmail());
        email.setFocusable(false);
        password.setVisibility(View.GONE);
        confirmPassword.setVisibility(View.GONE);


    }

    //code for google maps
    private void getLocation() {
        try {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(Registro2.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object

                                    double lon = location.getLongitude();
                                    double lat = location.getLatitude();

                                    longit.setText(String.valueOf(lon));
                                    latit.setText(String.valueOf(lat));

                                    Geocoder gcd = new Geocoder(Registro2.this, Locale.getDefault());
                                    try {
                                        List<Address> addresses = gcd.getFromLocation(lat, lon, 1);
                                        if (addresses != null && addresses.size() > 0) {
                                            ciudad.setText(addresses.get(0).getLocality());
                                        } else {
                                            Toast.makeText(Registro2.this, "No addresses found. Try again.", Toast.LENGTH_LONG).show();
                                        }

                                    }catch (Exception e) {
                                        Toast.makeText(Registro2.this, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                                    }


                                } else {
                                    Toast.makeText(Registro2.this, "Error. Active Location Services and try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            } else {
                    // Solicitar permiso
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_REQUEST_CODE);
            }

        } catch (SecurityException e) {
            Toast.makeText(Registro2.this, "Permission error. Active Location Services and try again.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(Registro2.this, "Permission error. Active Location Services and try again.", Toast.LENGTH_LONG).show();
        }

        ((Button) findViewById(R.id.btnLocation)).setText("TRY AGAIN");
    }

}