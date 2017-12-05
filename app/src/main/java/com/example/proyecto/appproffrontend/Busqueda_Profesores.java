package com.example.proyecto.appproffrontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class Busqueda_Profesores extends AppCompatActivity implements MultiSpinner.MultiSpinnerListener {

    //private String nombreUsuario;


    /**
     * identificador para la actividad de listar profesores de busqueda
     */
    private static final int ACTIVITY_LISTAR_BUSQUEDA = 0;
    /**
     * identificador para la actividad de listar profesores favoritos
     */
    private static final int ACTIVITY_LISTAR_FAVORITOS = 1;
    private API api;
    private EditText nombre;
    private EditText ciudad;
    private MultiSpinner horarios;
    private MultiSpinner asignaturas;
    private MultiSpinner cursos;
    private Button buscarProfesor = null;
    private Button favoritoProfesor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda__profesores);

        api = new API(this);
        final com.example.proyecto.appproffrontend.Busqueda_Profesores local = this;
        buscarProfesor = (Button) findViewById(R.id.buscarProfesor);
        favoritoProfesor = (Button) findViewById(R.id.favoritoProfesor);

        // Bundle extras = getIntent().getExtras();
        //Recoger informacion del usuario de la sesion
        //nombreUsuario = (extras != null) ? extras.getString("nombreUsuario") : null;

        populatefields();
//        final Intent i = new Intent(this, Ver_Profesor.class);
//        i.putExtra("nombreUsuario", "profesor2");
        buscarProfesor.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                //startActivity(i);
                if(nombre.getText().toString().equals("") &&
                        ciudad.getText().toString().equals("") &&
                        horarios.getValues().equals("") &&
                        asignaturas.getValues().equals("") &&
                        cursos.getValues().equals(""))
                {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(local);

                    dlgAlert.setMessage("Es necesario rellenar al menos un campo");
                    dlgAlert.setTitle("Error...");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();

                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                } else {
                    //PEDIR A LA BASE DE DATOS QUE REALICE LA BUSQUEDA DEL PROFESOR
                    listar_busqueda();
                }
            }
        });

        favoritoProfesor.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                listar_favoritos();
            }
        });
    }

    private void populatefields(){
        Facade facade = new Facade(api);
        nombre = (EditText) findViewById(R.id.usuarioProfesorBusqueda);
        ciudad = (EditText) findViewById(R.id.ciudadProfesorBusqueda);

        horarios = (MultiSpinner) findViewById(R.id.horariosProfesorBusqueda);
        horarios.setItems(facade.getHorariosDisponibles(),facade.getHorariosDisponibles(),
                null,"", this);

        asignaturas = (MultiSpinner) findViewById(R.id.asignaturasProfesorBusqueda);
        asignaturas.setItems(facade.getAsignaturasDisponibles(),facade.getAsignaturasDisponibles(),
                null,"", this);

        cursos = (MultiSpinner) findViewById(R.id.cursosProfesorBusqueda);
        cursos.setItems(facade.getCursosDisponibles(),facade.getCursosDisponibles(),
                null,"", this);
    }

    private void listar_favoritos(){
        Intent i = new Intent(this, Ver_favoritos.class); //Profesores_Favoritos.class);
        //i.putExtra("nombreUsuario", nombreUsuario);
        startActivityForResult(i, ACTIVITY_LISTAR_FAVORITOS);
    }

    private void listar_busqueda(){
        Intent i = new Intent(this, Listar_Profesores.class);
        //i.putExtra("nombreUsuario", nombreUsuario);
        i.putExtra("nombre", nombre.getText().toString());
        i.putExtra("ciudad", ciudad.getText().toString());
        ArrayList<String> horario = horarios.getValues();
        i.putExtra("horarios", horario);
        ArrayList<String> asignatura = asignaturas.getValues();
        i.putExtra("asignaturas", asignatura);
        ArrayList<String> curso = cursos.getValues();
        i.putExtra("cursos", curso);
        startActivityForResult(i, ACTIVITY_LISTAR_BUSQUEDA);
    }

    @Override
    public void onItemsSelected(boolean[] selected) {

    }
}
