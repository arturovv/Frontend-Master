package com.example.proyecto.appproffrontend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Facade {

    API api = null;

    //Deberían ser consultas
    private ArrayList<String> horariosDisponibles = new ArrayList<String>() {{
       add("Monday Morning"); add("Monday evening"); add("Tuesday morning"); add("Tuesday evening");
        add("Wednesday morning"); add("Wednesday evening"); add("Thursday morning"); add("Thursday evening");
        add("Friday morning"); add("Friday evening");
    }};
    private ArrayList<String> modalidadesDisponibles = new ArrayList<String>() {{
        add("---"); add("On-site"); add("On-line");
    }};

    public Facade(API api) {
        this.api = api;
    }

    public Facade() {
        this.api = null;
    }

    public ProfesorVO perfilProfesor(String profesor) throws APIexception {
        ProfesorDAO profesorDAO = new ProfesorDAO();
        return profesorDAO.perfilProfesor(api, profesor);
    }

    public ProfesorVO verProfesor(String profesor) throws APIexception {
        ProfesorDAO profesorDAO = new ProfesorDAO();
        return profesorDAO.verProfesor(api, profesor);
    }

    public void profesorPagar() throws APIexception{
        ProfesorDAO profesorDAO = new ProfesorDAO();
        profesorDAO.profesorPagar(api);
    }

    public JSONObject enviarValoracion(String profesorID, String alumno, float valoracion)
            throws APIexception {
        ProfesorDAO profesorDAO = new ProfesorDAO();
        return profesorDAO.enviarValoracion(api, profesorID, alumno, valoracion);
    }

    public int registro_alumno(AlumnoVO alum) throws APIexception{
        AlumnoDAO alumnoDAO = new AlumnoDAO();
        return alumnoDAO.registro_alumno(api, alum);
    }

    public int registro_profesor(ProfesorVO prof) throws APIexception{
        ProfesorDAO profesorDAO = new ProfesorDAO();
        return profesorDAO.registro_profesor(api, prof);
    }

    public int actualizar_profesor(ProfesorVO prof) throws APIexception {
        ProfesorDAO profesorDAO = new ProfesorDAO();
        return profesorDAO.actualizar_profesor(api, prof);
    }

    public JSONObject login(PersonaVO per, int tipo) throws APIexception{
        PersonaDAO personaDAO = new PersonaDAO();
        return personaDAO.login(api, per, tipo);
    }


    public ArrayList<String> getHorariosDisponibles() {
        return horariosDisponibles;
    }

    public ArrayList<String> getAsignaturasDisponibles() {
        ArrayList<String> lista = new ArrayList<String>();
        try
        {
            JSONArray json = api.getArray("/api/asignaturas/get");

            for(int i = 0; i < json.length(); i++)
            {
                try {
                    JSONObject jo = json.getJSONObject(i);
                    lista.add(jo.getString("nombre"));
                } catch (JSONException e){e.printStackTrace();}
            }

        } catch (APIexception ex) {
            ex.printStackTrace();}

        return lista;

    }

    public ArrayList<String> getCursosDisponibles() {
        ArrayList<String> lista = new ArrayList<String>();
        try
        {
            JSONArray json = api.getArray("/api/asignaturas/get");

            for(int i = 0; i < json.length(); i++)
            {
                try {
                    JSONObject jo = json.getJSONObject(i);
                    lista.add(jo.getString("nivel"));
                } catch (JSONException e){e.printStackTrace();}
            }

        } catch (APIexception ex) {
            ex.printStackTrace();}

        //Lo convertimos a un set, que no admite repetidos, y de nuevo a una lista
        return (new ArrayList<>( new LinkedHashSet<>(lista)));

    }

    public ArrayList<String> getModalidadesDisponibles() {
        return modalidadesDisponibles;
    }

    public void anyadir_profesor_favorito(ProfesorVO profesor) throws APIexception {
        AlumnoDAO alumnoDAO = new AlumnoDAO();
        alumnoDAO.anyadir_profesor_favorito(api, profesor);
    }

    public ArrayList<ProfesorVO> getProfesoresFavoritos() throws APIexception {
        ArrayList<ProfesorVO> lista = new ArrayList<ProfesorVO>();
        ProfesorVO profesorVO = null;
        try {
            JSONArray array =  api.getArray("/api/favoritos/get");

            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jsonObject = array.getJSONObject(i);
                    if (jsonObject != null) {
                        String nombre = jsonObject.getString("userName");
                        lista.add(this.verProfesor(nombre));
                    }
                } catch (JSONException e) { e.printStackTrace(); }
            }
        } catch (APIexception e) {
            e.printStackTrace();
        }
        return (new ArrayList<>( new LinkedHashSet<>(lista)));
    }
}