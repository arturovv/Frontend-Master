package com.example.proyecto.appproffrontend;

import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

public class Listar_Profesores extends AppCompatActivity {

    /**
     * identificador para la actividad de ver profesor seleccionado
     */
    private static final int ACTIVITY_VER_PROFESOR = 0;
    private static Bundle extras;
    private String profesor;
    private String nombre;
    private String ciudad;
    private ArrayList<String> horarios;
    private ArrayList<String> asignaturas;
    private ArrayList<String> cursos;
    private ArrayList<String> userNames;
    private ListView listView;
    private ArrayList<ProfesorVO> m_profesores;

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar__profesores);

        listView = (ListView) findViewById(R.id.list);

        // Recogemos los datos para realizar la busqueda
        if(getIntent().getExtras() != null)
            extras = getIntent().getExtras();


        // Nombre y ciudad si no han sido rellenados -> ""
        nombre = (extras != null) ? extras.getString("nombre") : null;
        ciudad = (extras != null) ? extras.getString("ciudad") : null;
        // Horario, asignatura y curso si no han sido rellenados -> "---"
        horarios = (extras != null) ? extras.getStringArrayList("horarios") : null;
        asignaturas = (extras != null) ? extras.getStringArrayList("asignaturas") : null;
        cursos = (extras != null) ? extras.getStringArrayList("cursos") : null;

        fillData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView c = (TextView) view.findViewById(R.id.nombreProfesorListar);
                //profesor = c.getText().toString();
                profesor = m_profesores.get(position).getNombreUsuario();
                verProfesor();
            }
        });

    }

    /**
     * Rellena la lista de notas con la información de la base de datos.
     */
    private void fillData() {
        // Acceso a la base de datos
        Facade facade = new Facade();
        //Buscamos los profesores
        m_profesores = getData();//Facade.buscarProfesores(nombre,ciudad, horario, asignatura, curso);
        Collections.sort(m_profesores);
        //m_profesores.add(facade.perfilProfesor("David"));
        //m_profesores.add(facade.perfilProfesor("Fuste"));

        // Si no existen profesores se muestra mensaje
        if (m_profesores.isEmpty()){
            TextView empty = (TextView)findViewById(R.id.empty);
            empty.setVisibility(View.VISIBLE);
        }
        // Si si que existen se procede a mostrarlos
        else {

            // Create an array to specify the fields we want to display in the list
            String[] from = new String[]{"Nombre", "Ciudad",
                    "Asignaturas", "Horarios", "Valoración media"};

            // Se crea un cursor para mostrar los profesores
            MatrixCursor profesorCursor = new MatrixCursor(
                    new String[]{"_id", "Nombre", "Ciudad", "Asignaturas",
                            "Horarios", "Valoración media"});
            startManagingCursor(profesorCursor);

            // Se añaden los profesores encontrados al cursor
            for (int i = 0; i < m_profesores.size(); i++) {
                ProfesorVO p = m_profesores.get(i);
                if (p.getValoracion() < 0.0){
                    p.setValoracion(0.0);
                }

                p.setValoracion((round(p.getValoracion(),2)));
                //p.setAsignaturas(asignaturasMostrar(p));
                //p.setHorarios(horariosMostrar(p));

                profesorCursor.addRow(new Object[]{i, p.getNombreUsuario(), p.getCiudad(),
                        p.getAsignaturas(), p.getHorarios(), p.getValoracion()});
            }

            // and an array of the fields we want to bind those fields to
            int[] to = new int[]{R.id.nombreProfesorListar, R.id.ciudadProfesorListar,
                    R.id.asignaturasProfesorListar, R.id.horariosProfesorListar,
                    R.id.valoracionesProfesorListar};

            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter profesor =
                    new SimpleCursorAdapter(this, R.layout.row_listar_profesores, profesorCursor,
                            from, to);
            listView.setAdapter(profesor);
        }
    }

    private void verProfesor(){
        Intent i = new Intent(this, Ver_Profesor.class);
        i.putExtra("nombreUsuario", profesor);
        startActivityForResult(i, ACTIVITY_VER_PROFESOR);
    }

    private ArrayList<ProfesorVO> getData() {
        API api = new API(this);
        JSONObject jsonPost = new JSONObject();
        ArrayList<ProfesorVO> list = new ArrayList<>();
        userNames = new ArrayList<>();
        try{
            jsonPost.put("nombre",nombre);
            jsonPost.put("ciudad",ciudad);
            jsonPost.put("horarios",new JSONArray(horarios));
            jsonPost.put("asignaturas",new JSONArray(asignaturas));
            jsonPost.put("cursos",new JSONArray(cursos));
            try {
                JSONArray jArray = api.postArray("/api/busqueda", jsonPost);
                for (int i=0; i < jArray.length(); i++) {
                    JSONObject jaux = jArray.getJSONObject(i);
                    userNames.add(jaux.getString("userName"));
                    list.add(new ProfesorVO(jaux));
                }
            } catch (APIexception epi) {epi.printStackTrace();}
        } catch (JSONException e){e.printStackTrace();}
        return list;
    }

    private ArrayList<String> asignaturasMostrar(ProfesorVO p){
        ArrayList<String> asignaturasprof = new ArrayList<>();
        if (asignaturas.size() >= 2){
            // Si se buscaron mas de 2 se muestran
            asignaturasprof.add(asignaturas.get(1));
            asignaturasprof.add(asignaturas.get(2));
        } else if (asignaturas.size() == 1){
            // Si se busco 1 se muestra
            asignaturasprof.add(asignaturas.get(1));
            if (p.getAsignaturas().size() > 1){
                // Si se busco 1 y tiene mas de 1 se busca uno diferente para mostrar
                if (!p.getAsignaturas().get(1).equals((asignaturas.get(1)))){
                    asignaturasprof.add(p.getAsignaturas().get(1));
                } else {
                    asignaturasprof.add(p.getAsignaturas().get(2));
                }
            }
        } else {
            // Si no se busco ninguno se añaden maximo 2
            asignaturasprof = p.getAsignaturas();
            if (p.getAsignaturas().size() <= 2){
                asignaturasprof = p.getAsignaturas();
            } else {
                asignaturasprof.add(p.getAsignaturas().get(1));
                asignaturasprof.add(p.getAsignaturas().get(2));
            }
        }
        return asignaturasprof;
    }

    private ArrayList<String> horariosMostrar(ProfesorVO p){
        ArrayList<String> horariosprof = new ArrayList<>();
        if (horarios.size() >= 2){
            // Si se buscaron mas de 2 se muestran
            horariosprof.add(horarios.get(1));
            horariosprof.add(horarios.get(2));
        } else if (horarios.size() == 1){
            // Si se busco 1 se muestra
            horariosprof.add(horarios.get(1));
            if (p.getHorarios().size() > 1){
                // Si se busco 1 y tiene mas de 1 se busca uno diferente para mostrar
                if (!p.getHorarios().get(1).equals((horarios.get(1)))){
                    horariosprof.add(p.getHorarios().get(1));
                } else {
                    horariosprof.add(p.getHorarios().get(2));
                }
            }
        } else {
            // Si no se busco ninguno se añaden maximo 2
            if (p.getHorarios().size() <= 2){
                horariosprof = p.getHorarios();
            } else {
                horariosprof.add(p.getHorarios().get(1));
                horariosprof.add(p.getHorarios().get(2));
            }
        }
        return horariosprof;
    }

}
