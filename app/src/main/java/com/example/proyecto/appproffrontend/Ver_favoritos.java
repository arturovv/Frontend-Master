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

import java.util.ArrayList;

public class Ver_favoritos extends AppCompatActivity {

    /**
     * identificador para la actividad de ver profesor seleccionado
     */
    private static final int ACTIVITY_VER_PROFESOR = 0;
    private String profesor;
    private String nombre;
    private ArrayList<String> asignatura;
    private ListView listView;
    private ArrayList<ProfesorVO> m_profesores;
    private API api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar__profesores);

        api = new API(this);
        listView = (ListView) findViewById(R.id.list);

        // Recogemos los datos para realizar la busqueda
        Bundle extras = getIntent().getExtras();


        // Nombre y ciudad si no han sido rellenados -> ""
        nombre = (extras != null) ? extras.getString("nombre") : null;
        // Horario, asignatura y curso si no han sido rellenados -> "---"
        asignatura = (extras != null) ? extras.getStringArrayList("asignatura") : null;

        try {
            fillData();
        } catch (APIexception apIexception) {
            apIexception.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView c = (TextView) view.findViewById(R.id.nombreProfesorListar);
                profesor = c.getText().toString();
                verProfesor();
            }
        });

    }

    /**
     * Rellena la lista de notas con la información de la base de datos.
     */
    private void fillData() throws APIexception {
        // Acceso a la base de datos
        Facade facade = new Facade(api);
        //Buscamos los profesores
        m_profesores = facade.getProfesoresFavoritos(); //Facade.buscarProfesores(nombre,ciudad, horario, asignatura, curso);
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
            String[] from = new String[]{"Nombre", "Asignaturas"};

            // Se crea un cursor para mostrar los profesores
            MatrixCursor profesorCursor = new MatrixCursor(
                    new String[]{"_id", "Nombre", "Asignaturas"});
            startManagingCursor(profesorCursor);

            // Se añaden los profesores encontrados al cursor
            for (int i = 0; i < m_profesores.size(); i++) {
                ProfesorVO p = m_profesores.get(i);
                profesorCursor.addRow(new Object[]{i, p.getNombreUsuario(),
                        p.getAsignaturas()});
            }

            // and an array of the fields we want to bind those fields to
            int[] to = new int[]{R.id.nombreProfesorListar,
                    R.id.asignaturasProfesorListar};

            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter profesor =
                    new SimpleCursorAdapter(this, R.layout.row_ver_favoritos, profesorCursor,
                            from, to);
            listView.setAdapter(profesor);
        }
    }

    private void verProfesor(){
        Intent i = new Intent(this, Ver_Profesor.class);
        i.putExtra("nombreUsuario", profesor);
        startActivityForResult(i, ACTIVITY_VER_PROFESOR);
    }

}
