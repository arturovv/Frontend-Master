package com.example.proyecto.appproffrontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rubenbros on 14/04/2017.
 */

public class Registro3 extends AppCompatActivity implements MultiSpinner.MultiSpinnerListener {

    private String password;
    private String user;
    private MultiSpinner horario;
    private MultiSpinner asignaturas;
    private MultiSpinner curso;
    private Spinner modo;
    private Facade facade = null;
    private API api;
    private JSONObject respuesta;
    private InfoSesion info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getIntent().getExtras().getString("profesor_user");
        password = getIntent().getExtras().getString("profesor_psw");
        info = new InfoSesion(this);

        api = new API(this);
        facade = new Facade(api);
        setContentView(R.layout.activity_registro3_profesor);
        horario = (MultiSpinner) findViewById(R.id.horariosProfesorReg);
        asignaturas = (MultiSpinner) findViewById(R.id.asignaturasProfesorReg);
        curso = (MultiSpinner) findViewById(R.id.cursoProfesorReg);
        modo = (Spinner) findViewById(R.id.modalidadProfesorReg);
        populateFields();
        Button registro = (Button) findViewById(R.id.registerbutton);
        final Intent i = new Intent(this, Perfil_Profesor.class);
        registro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final int code = guardarEnBd();
                if (code == -1) {
                    info.set(user,1);
                    try {
                        facade.login(new PersonaVO(user,password),1);
                    } catch (APIexception ex) {}
                    startActivity(i);
                }
                else error(code);
            }
        });


    }

    private int guardarEnBd() {
        //Comprobar campos
        CheckBox tyc = (CheckBox) findViewById(R.id.TyC);
        ArrayList<String> horariosProf = horario.getValues();
        ArrayList<String> asignaturasProf = asignaturas.getValues();

        String exp = getIntent().getExtras().getString("profesor_exp");
        if(exp.equals("")) exp = null;
        ArrayList<String> cursosProf = curso.getValues();
        if(cursosProf.isEmpty()) cursosProf = null;
        String modulo = modo.getSelectedItem().toString();

        if (!tyc.isChecked()) return 8;
        else if (horariosProf.isEmpty()) return 1;
        else if (asignaturasProf.isEmpty()) return 2;

        try {
            return facade.registro_profesor(new ProfesorVO(
                    user, password, getIntent().getExtras().getString("profesor_tlf"),
                    getIntent().getExtras().getString("profesor_mail"),
                    getIntent().getExtras().getString("profesor_ciu"),
                    horariosProf,cursosProf,asignaturasProf,-1.00, exp, modulo));
        } catch (APIexception ex) { respuesta = ex.json; return 10; }
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
                try {
                    dlgAlert.setMessage("Error durante el registro: \n" + respuesta.getString("message"));
                } catch (JSONException ex) {dlgAlert.setMessage("Error durante el registro:");}
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