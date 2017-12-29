package com.example.proyecto.appproffrontend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;


public class Registro3 extends AppCompatActivity implements MultiSpinner.MultiSpinnerListener {

    private static final String TAG = null;
    private String password;
    private String email;
    private FirebaseUser user;
    private MultiSpinner horario;
    private MultiSpinner asignaturas;
    private MultiSpinner curso;
    private Spinner modo;
    private Facade facade = null;
    private API api;
    private JSONObject respuesta;
    private InfoSesion info;
    private FirebaseAuth mAuth;
    private Intent i, ActivityError;
    private SharedPreferences sharedPref;

    // BD firebase
    FirebaseConections firebaseConections=new FirebaseConections();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        email = getIntent().getExtras().getString("profesor_mail");
        password = getIntent().getExtras().getString("profesor_psw");
        info = new InfoSesion(this);
        ActivityError = new Intent(this, Registro2.class);
        sharedPref = this.getSharedPreferences("APPROF", Context.MODE_PRIVATE);


        api = new API(this);
        facade = new Facade(api);
        setContentView(R.layout.activity_registro3_profesor);
        horario = (MultiSpinner) findViewById(R.id.horariosProfesorReg);
        asignaturas = (MultiSpinner) findViewById(R.id.asignaturasProfesorReg);
        curso = (MultiSpinner) findViewById(R.id.cursoProfesorReg);
        modo = (Spinner) findViewById(R.id.modalidadProfesorReg);
        populateFields();
        Button registro = (Button) findViewById(R.id.registerbutton);
        i = new Intent(this, Perfil_Profesor.class);
        registro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                findViewById(R.id.registerbutton).setVisibility(View.GONE);
                int code = -1;
                try {
                    code = guardarEnBd();
                } catch (APIexception apIexception) {code=10;}
                if(code != -1) {
                    findViewById(R.id.registerbutton).setVisibility(View.VISIBLE);
                    error(code);
                }
            }
        });


    }

    private int guardarEnBd() throws APIexception {
        //Comprobar campos
        CheckBox tyc = (CheckBox) findViewById(R.id.TyC);
        final ArrayList<String> horariosProf = horario.getValues();
        final ArrayList<String> asignaturasProf = asignaturas.getValues();

        String expaux = getIntent().getExtras().getString("profesor_exp");
        if(expaux.equals("")) expaux = null;
        final String exp = expaux;

        ArrayList<String> cursosProfaux = curso.getValues();
        if(cursosProfaux.isEmpty()) cursosProfaux = null;
        final ArrayList<String> cursosProf = cursosProfaux;

        final String modulo = modo.getSelectedItem().toString();

        if (!tyc.isChecked()) return 8;
        else if (horariosProf.isEmpty()) return 1;
        else if (asignaturasProf.isEmpty()) return 2;

        if(mAuth.getCurrentUser() == null) {//google auth comprobation
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                user = mAuth.getCurrentUser();
                                // adrian code

                                firebaseConections.writeNewTeacherLocation(mAuth, email, getIntent().getExtras().getString("profesor_ciu"),
                                        getIntent().getExtras().getString("profesor_long"), getIntent().getExtras().getString("profesor_lat"));

                                // finalitation adrian super code


                                try {
                                    facade.registro_profesor(new ProfesorVO(email, password, getIntent().getExtras().getString("profesor_tlf"),
                                            getIntent().getExtras().getString("profesor_user"),
                                            getIntent().getExtras().getString("profesor_ciu"),
                                            horariosProf, cursosProf, asignaturasProf, -1.00, exp, modulo, getIntent().getExtras().getString("profesor_long"),
                                            getIntent().getExtras().getString("profesor_lat")));
                                    info.set(email, 1);
                                    info.setSession(user);
                                    facade.login(new PersonaVO(email, password), 1);
                                    startActivity(i);
                                } catch (APIexception ex) {
                                    user = null;
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Exception e = task.getException();
                                Log.w(TAG, "createUserWithEmail:failure", e);
                                Toast.makeText(Registro3.this, "An error has occurred.",
                                        Toast.LENGTH_SHORT).show();
                                findViewById(R.id.registerbutton).setVisibility(View.VISIBLE);
                                user = null;
                                SharedPreferences.Editor editor = sharedPref.edit();

                                try {
                                    throw e;
                                } catch (FirebaseAuthWeakPasswordException e1) {
                                    editor.putInt("error", 3).apply();//Pass to weak
                                } catch (FirebaseAuthInvalidCredentialsException e1) {
                                    editor.putInt("error", 1).apply();//Invalid email
                                } catch (FirebaseAuthUserCollisionException e1) {
                                    editor.putInt("error", 2).apply();//email reused
                                } catch (Exception e1) {
                                }
                                startActivity(ActivityError);
                            }

                            // [START_EXCLUDE]
                            //hideProgressDialog();
                            // [END_EXCLUDE]

                        }
                    });

        } else {//code for google auth
            user = mAuth.getCurrentUser();

            // adrian code
            firebaseConections.writeNewTeacherLocation(mAuth, email, getIntent().getExtras().getString("profesor_ciu"),
                    getIntent().getExtras().getString("profesor_long"), getIntent().getExtras().getString("profesor_lat"));
            // finalitation adrian super code

            try {
                facade.registro_profesor(new ProfesorVO(email, password, getIntent().getExtras().getString("profesor_tlf"),
                        getIntent().getExtras().getString("profesor_user"),
                        getIntent().getExtras().getString("profesor_ciu"),
                        horariosProf, cursosProf, asignaturasProf, -1.00, exp, modulo, getIntent().getExtras().getString("profesor_long"),
                        getIntent().getExtras().getString("profesor_lat")));
                info.set(email, 1);
                info.setSession(user);
                facade.login(new PersonaVO(email, password), 1);
                startActivity(i);
            } catch (APIexception ex) {
                Toast.makeText(Registro3.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }

        }

            return -1;
    }

    private void populateFields() {

        horario.setItems(facade.getHorariosDisponibles(), facade.getHorariosDisponibles(),
                null, "", this);

        asignaturas.setItems(facade.getAsignaturasDisponibles(), facade.getAsignaturasDisponibles(),
                null, "", this);

        curso.setItems(facade.getCursosDisponibles(), facade.getCursosDisponibles(),
                null, "", this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.row_spinner, facade.getModalidadesDisponibles());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modo.setAdapter(adapter);
        modo.setSelection(0);

    }

    private void error(int code) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        switch (code) {
            case 1:
                dlgAlert.setMessage("Debe seleccionar al menos un horario");
                break;
            case 2:
                dlgAlert.setMessage("Debe seleccionar al menos una asignatura");
                break;
            case 8:
                dlgAlert.setMessage("Acepte los terminos y condiciones");
                break;
            case 10:
                    dlgAlert.setMessage("Error durante el registro.");
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

    public void onItemsSelected(boolean[] selected) {
        //Esto hay que ponerlo pero a saber para que... seguiremos investigando
    }
}