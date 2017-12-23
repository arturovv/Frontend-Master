package com.example.proyecto.appproffrontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

public class Perfil_Profesor extends AppCompatActivity {

    private Facade facade = null;
    private ProfesorVO profesor = null;

    private JSONObject respuesta;
    private TextView user;
    private RatingBar valoracion;
    private TextView telefono;
    private TextView email;
    private TextView ciudad;
    private TextView experiencia;
    private TextView modalidad;
    private Button modificarButton = null;
    private Button pagarButton = null;
    private Spinner horarios = null;
    private Spinner cursos = null;
    private Spinner asignaturas = null;
    private API api;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil__profesor);
        InfoSesion info = new InfoSesion(this);

        final com.example.proyecto.appproffrontend.Perfil_Profesor local = this;
        modificarButton = (Button) findViewById(R.id.modifyProfesor);
        pagarButton = (Button) findViewById(R.id.pagarProfesor);


        modificarButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(local, Modificar_Perfil_1.class);
                startActivityForResult(i, 0);
            }
        });

        pagarButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(local, Pagar_Profesor.class);
                startActivityForResult(i, 0);
            }
        });

        api = new API(this);
        facade = new Facade(api);
        try {
            profesor = facade.perfilProfesor(info.getUsername());
            populateFields();
        } catch (APIexception ex) { respuesta = ex.json; }


    }

    private void populateFields() {
        user = (TextView) findViewById(R.id.usuarioProfesor);
        user.setText(profesor.getNombreUsuario());

        valoracion = (RatingBar) findViewById(R.id.valoracionProfesor);
        valoracion.setRating(profesor.getValoracion().floatValue());

        telefono = (TextView) findViewById(R.id.tlfnoProfesor);
        telefono.setText(profesor.getTelefono());

        email = (TextView) findViewById(R.id.emailProfesor);
        email.setText(profesor.getMail());

        ciudad = (TextView) findViewById(R.id.ciudadProfesor);
        ciudad.setText(profesor.getCiudad());

        experiencia = (TextView) findViewById(R.id.experienciaProfesor);
        experiencia.setText(profesor.getExperiencia());

        modalidad = (TextView) findViewById(R.id.modalidadProfesor);
        modalidad.setText(profesor.getModalidad());

        if(profesor.getHorarios() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, R.layout.row_spinner, profesor.getHorarios());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            horarios = (Spinner) findViewById(R.id.horariosProfesor);
            horarios.setAdapter(adapter);
        }

        if(profesor.getCursos() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, R.layout.row_spinner, profesor.getCursos());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cursos = (Spinner) findViewById(R.id.cursosProfesor);
            cursos.setAdapter(adapter);
        }

        if(profesor.getAsignaturas() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, R.layout.row_spinner, profesor.getAsignaturas());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            asignaturas = (Spinner) findViewById(R.id.asignaturasProfesor);
            asignaturas.setAdapter(adapter);
        }
    }
}
