package com.example.proyecto.appproffrontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Modificar_Perfil_2 extends AppCompatActivity implements MultiSpinner.MultiSpinnerListener {

    private TextView user;
    private Facade facade = null;
    private ProfesorVO profesor = null;
    private InfoSesion sesion = null;
    private API api;
    private EditText experiencia;
    private MultiSpinner cursos = null;
    private Spinner modalidad = null;
    private Button confirmarButton = null;
    private JSONObject respuesta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar__perfil_2);

        confirmarButton = (Button) findViewById(R.id.confirmarMod);
        sesion = new InfoSesion(this);
        api = new API(this);
        facade = new Facade(api);
        try {
            profesor = facade.perfilProfesor(sesion.getUsername());
            populateFields();
        } catch (APIexception ex) { respuesta = ex.json; }
        final Intent i = new Intent(this, Perfil_Profesor.class);
        confirmarButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                final int code = guardarEnBd();
                if (code == -1) startActivity(i);
                else error(code);

            }
        });

    }

    private void populateFields() {

        user = (TextView) findViewById(R.id.usuarioProfesorMod);
        user.setText(profesor.getNombreUsuario());

        experiencia = (EditText) findViewById(R.id.experienciaProfesorMod);
        experiencia.setText(profesor.getExperiencia());

        cursos = (MultiSpinner) findViewById(R.id.cursosProfesorMod);
        cursos.setItems(facade.getCursosDisponibles(),facade.getCursosDisponibles(),
                profesor.getCursos(),"", this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.row_spinner, facade.getModalidadesDisponibles());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modalidad = (Spinner) findViewById(R.id.modalidadProfesorMod);
        modalidad.setAdapter(adapter);
        if(profesor.getModalidad().equals("Presencial"))
            modalidad.setSelection(1);
        else if(profesor.getModalidad().equals("On-line"))
            modalidad.setSelection(2);
    }

    private int guardarEnBd() {
        //Comprobar campos
        String exp = experiencia.getText().toString();
        if (exp.equals("")) exp = null;
        ArrayList<String> cursosProf = cursos.getValues();
        if (cursosProf.isEmpty()) cursosProf = null;
        String modulo = modalidad.getSelectedItem().toString();
        if (modulo.equals("---")) modulo = null;
        facade = new Facade(api);
        try {
            return facade.actualizar_profesor(new ProfesorVO(
                    profesor.getNombreUsuario(),
                    profesor.getPassword(),
                    getIntent().getExtras().getString("profesor_tlf"),
                    getIntent().getExtras().getString("profesor_mail"),
                    getIntent().getExtras().getString("profesor_ciu"),
                    getIntent().getExtras().getStringArrayList("profesor_hor"),
                    cursosProf,
                    getIntent().getExtras().getStringArrayList("profesor_asig"),
                    profesor.getValoracion(),
                    exp,
                    modulo));
        } catch (APIexception ex) {
            respuesta = ex.json;
            return 10;
        }
    }

    private void error(int code) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        switch (code) {
            case 10:
                try {
                    dlgAlert.setMessage("Error durante el registro: \n" + respuesta.getString("message"));
                } catch (JSONException ex) {
                    dlgAlert.setMessage("Error durante el registro:");
                }
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
