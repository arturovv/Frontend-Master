package com.example.proyecto.appproffrontend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class Ver_Profesor extends AppCompatActivity {

    private TextView user;
    private TextView ciudad;
    private TextView experiencia;
    private Spinner asignaturas;
    private Spinner cursos;
    private Spinner horarios;
    private TextView email;
    private TextView telefono;
    private TextView modalidad;
    private ArrayList<ProfesorVO> m_profesores;

    private RatingBar barraValoracion;
    // Quitar texto que sale después de la valoración
    private TextView txtValoracionPerfil;
    private Button btnEnviarValoracionPerfil;

    // Falta ListenerOnButton para anyadir profesor a favoritos
    private Button btnAnyadirProfesorFav;

    // Profesor de prueba para poblar los distintos campos
    private Facade facade = null;
    private ProfesorVO profesor = null;

    private JSONObject respuesta;
    private API api;

    private String buscarProfesor;

    private InfoSesion info;

    //for google maps
    private SharedPreferences sharedPref;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_profesor);

        Intent intent = getIntent();

        // Devuelve el nombre del profesor no el userName
        buscarProfesor = intent.getStringExtra("nombreUsuario");

        info = new InfoSesion(this);
        api = new API(this);
        facade = new Facade(api);
        try {
            profesor = facade.verProfesor(buscarProfesor);
            populateFields();
        } catch (APIexception ex) {
            respuesta = ex.json;
        }

        populateFields();

        // Listeners barra de Rating y botón de enviar la valoración
        addListenerOnRatingBar();
        addListenerOnButtonValoracion();
        // Listener botón de profesor favorito
        addListenerOnButtonProfFavorito();


        //google maps code
        mAuth = FirebaseAuth.getInstance();

        //EN PRINCIPIO, AQUI TE DEJO LA IDENTIFICACION DEL USER (EL ID CON EL QUE SE CREA EL ARBOL EN LA BD)
        String idUser = mAuth.getCurrentUser().getUid(); //puedes buscar por esto y luego obtener los hijos latitud y longitud

        //*implementar busqueda*


        //strings!!!
        final String lat = "39.4757192"; //LATITUD: COMPLETAR CON LOS RESULTADOS DE LA BUSQUEDA EN FIREBASE
        final String lon = "-0.4258722"; //LONGITUD: COMPLETAR CON LOS RESULTADOS DE LA BUSQUEDA EN FIREBASE

        final Intent i = new Intent(this, MapsActivity.class);
        sharedPref = this.getSharedPreferences("APPROF", Context.MODE_PRIVATE);
        ((Button) findViewById(R.id.btnMaps)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("lat", lat).apply();
                editor.putString("lon", lon).apply();
                startActivity(i);
            }
        });

    }

    /*
    * Método para añadir el Listener al botón de añadir a favoritos al profesor en cuestión
    *
    */
    public void addListenerOnButtonProfFavorito() {

        btnAnyadirProfesorFav = (Button) findViewById(R.id.btnAnyadirFavoritoProfesor);

        btnAnyadirProfesorFav.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Facade facade = new Facade(api);
                try {
                    boolean flag = true;
                    m_profesores = facade.getProfesoresFavoritos();
                    for(ProfesorVO i_prof: m_profesores)
                        if(i_prof.getNombreUsuario().equals(profesor.getNombreUsuario()))
                            flag = false;

                    if(flag) {
                        facade.anyadir_profesor_favorito(profesor);
                        Toast.makeText(Ver_Profesor.this, "Profesor " + user.getText() +
                                        " añadido a favoritos", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(Ver_Profesor.this, "Profesor " + user.getText() +
                                " ya ha sido añadido", Toast.LENGTH_SHORT).show();
                } catch (APIexception apIexception) {
                    apIexception.printStackTrace();
                }
            }

        });

    }

    /*
    * Método para añadir el Listener a la barra de valoración
    *
    */
    public void addListenerOnRatingBar() {

        barraValoracion = (RatingBar) findViewById(R.id.ratingBarVerProfesor);
        barraValoracion.setRating(profesor.getValoracion().floatValue());
        txtValoracionPerfil = (TextView) findViewById(R.id.txtValoracionPerfilProfesor);

        barraValoracion.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                txtValoracionPerfil.setText(String.valueOf(rating));

            }
        });
    }

    /*
    * Método para añadir el Listener al botón de enviar la valoración al profesor en cuestión
    *
    */
    public void addListenerOnButtonValoracion() {

        barraValoracion = (RatingBar) findViewById(R.id.ratingBarVerProfesor);
        btnEnviarValoracionPerfil = (Button) findViewById(R.id.btnEnviarValoracion);

        btnEnviarValoracionPerfil.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String msg = "Error al enviar valoracion";

                // Enviar valoración a la base de datos
                try {
                    JSONObject respuesta = facade.enviarValoracion(profesor.getId(), info.getUsername(), barraValoracion.getRating());
                    try {
                        msg = respuesta.getString("message");
                    } catch (JSONException jex) {System.out.println(jex);}
                } catch (APIexception ex) { respuesta = ex.json; }

                Toast.makeText(Ver_Profesor.this,
                        String.valueOf(msg),
                        Toast.LENGTH_SHORT).show();

            }

        });

    }

    /*
     * Método para poblar el Spinner de asignaturas que imparte el profesor en cuestión
     *
     */
    private void populateAsignaturasSpinner() {

        if(profesor.getAsignaturas() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, R.layout.row_spinner, profesor.getAsignaturas());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            asignaturas = (Spinner) findViewById(R.id.asignaturasProfesorPerfil);
            asignaturas.setAdapter(adapter);
        }

    }

    /*
     * Método para poblar el Spinner de cursos a los que imparte el profesor en cuestión
     *
     */
    private void populateCursosSpinner() {

        if(profesor.getCursos() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, R.layout.row_spinner, profesor.getCursos());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cursos = (Spinner) findViewById(R.id.cursosProfesorPerfil);
            cursos.setAdapter(adapter);
        }

    }

    /*
     * Método para poblar el Spinner de horarios a los que imparte el profesor en cuestión
     *
     */
    private void populateHorariosSpinner() {

        if(profesor.getCursos() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, R.layout.row_spinner, profesor.getHorarios());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            horarios = (Spinner) findViewById(R.id.horarioProfesorPerfil);
            horarios.setAdapter(adapter);
        }

    }


    /*
    * Método para poblar todos los campos del perfil del profesor en cuestión
    *
    */
    private void populateFields() {

        // TODO: acceso a la base de datos en función del profesor que el usuario selecciona
        user = (TextView) findViewById(R.id.nombreProfesorPerfil);
        user.setText(profesor.getNombreUsuario());

        ciudad = (TextView) findViewById(R.id.ciudadProfesorPerfil);
        ciudad.setText(profesor.getCiudad());

        experiencia = (TextView) findViewById(R.id.experienciaProfesorPerfil);
        experiencia.setText(profesor.getExperiencia());

        // Llamada a los métodos para poblar los distintos Spinners. Solo está el de asignaturas.
        populateAsignaturasSpinner();
        populateCursosSpinner();
        populateHorariosSpinner();

        //final HashCode hashCode = Hashing.sha1().hashString("luis", Charset.defaultCharset());

        email = (TextView) findViewById(R.id.emailProfesorPerfil);
        email.setText(profesor.getMail());

        telefono = (TextView) findViewById(R.id.tlfnoProfesorPerfil);
        telefono.setText(profesor.getTelefono());

        modalidad = (TextView) findViewById(R.id.modalidadProfesorPerfil);
        modalidad.setText(profesor.getModalidad());

    }

}
