package com.example.proyecto.appproffrontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.app.AlertDialog.Builder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.ArrayList;

public class Modificar_Perfil_1 extends AppCompatActivity implements MultiSpinner.MultiSpinnerListener {

    private Facade facade = null;
    private ProfesorVO profesor = null;
    private API api;
    private TextView user;
    private EditText telefono;
    private EditText email;
    private EditText ciudad;
    private MultiSpinner horarios = null;
    private MultiSpinner asignaturas = null;
    private Button siguienteButton = null;
    private JSONObject respuesta;
    private InfoSesion sesion = null;

    // codigo de adrian
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar__perfil_1);

        // codigo anyadido por adrian
        mAuth = FirebaseAuth.getInstance();
        //stop anyadido

        siguienteButton = (Button) findViewById(R.id.siguienteMod);

        sesion = new InfoSesion(this);
        api = new API(this);
        facade = new Facade(api);
        try {
            profesor = facade.perfilProfesor(sesion.getUsername());
            populateFields();
        } catch (APIexception ex) { respuesta = ex.json; }

        populateFields();
        final Intent i = new Intent(this, Modificar_Perfil_2.class);
        siguienteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                final int code = guardarEnBdProf(i);
                if (code == -1) startActivity(i);
                else error(code);
            }
        });

    }

    private void populateFields() {

        telefono = (EditText) findViewById(R.id.tlfnoProfesorMod);
        telefono.setText(profesor.getTelefono());

        email = (EditText) findViewById(R.id.emailProfesorMod);
        email.setText(profesor.getMail());

        ciudad = (EditText) findViewById(R.id.ciudadProfesorMod);
        ciudad.setText(profesor.getCiudad());

        // AQUI IRA LA INFORMACION DEL MAPA PARA MODIFICAR

        user = (TextView) findViewById(R.id.usuarioProfesorMod);
        user.setText(profesor.getNombreUsuario());

        horarios = (MultiSpinner) findViewById(R.id.horariosProfesorMod);
        horarios.setItems(facade.getHorariosDisponibles(),facade.getHorariosDisponibles(),
                profesor.getHorarios(),"", this);

        asignaturas = (MultiSpinner) findViewById(R.id.asignaturasProfesorMod);
        asignaturas.setItems(facade.getAsignaturasDisponibles(),facade.getAsignaturasDisponibles(),
                profesor.getAsignaturas(),"", this);

    }

    private int guardarEnBdProf(final Intent i) {
        String mail = email.getText().toString();
        String phone = telefono.getText().toString();
        String city = ciudad.getText().toString();
        ArrayList<String> horariosProf = horarios.getValues();
        ArrayList<String> asignaturasProf = asignaturas.getValues();
        if (mail.isEmpty()) return 4;
        //if (!mail.matches("[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+")) return 5;
        else if (phone.isEmpty()) return 6;
        else if (!phone.matches("[0-9]{9}")) return 7;
        else if (city.isEmpty()) return 9;
        else if (horariosProf.isEmpty()) return 8;
        else if (asignaturasProf.isEmpty()) return 10;

        i.putExtra("profesor_tlf", phone);
        i.putExtra("profesor_mail", mail);
        i.putExtra("profesor_ciu", city);
        i.putExtra("profesor_hor", horariosProf);
        i.putExtra("profesor_asig", asignaturasProf);
        // aqui ira la localizacion, algo asi:
        //i.putExtra("profesor_long", asignaturasProf);
        //i.putExtra("profesor_lat", asignaturasProf);
        return -1;
    }

    private void error(int code) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        switch (code) {
            case 4:
                dlgAlert.setMessage("Rellene el campo de e-mail");
                break;
            case 5:
                dlgAlert.setMessage("Introduzca un e-mail valido");
                break;
            case 6:
                dlgAlert.setMessage("Rellene el campo de telefono");
                break;
            case 7:
                dlgAlert.setMessage("Introduzca un telefono valido");
                break;
            case 8:
                dlgAlert.setMessage("Debe seleccionar al menos un horario");
                break;
            case 9:
                dlgAlert.setMessage("Rellene el campo de Ciudad");
            case 10:
                dlgAlert.setMessage("Debe seleccionar al menos una asignatura");
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
