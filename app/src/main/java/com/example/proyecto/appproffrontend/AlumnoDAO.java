package com.example.proyecto.appproffrontend;

import org.json.JSONException;
import org.json.JSONObject;

public class AlumnoDAO {

    public AlumnoVO perfilProfesor(String alumno_) {

        //Integrar con la base de datos, con la conexión parsear el JSON
        AlumnoVO alumno = null;

        /* Falta realizar hash de la password en el lado de la autenticación
         * Se está usando Guava de Google
         *       - https://github.com/google/guava
         * Ejemplo:
         * final HashCode hashCode = Hashing.sha1().hashString(yourValue, Charset.defaultCharset());
         */
        alumno = new AlumnoVO("lfueris", "Salty_as02!KjanUQP},,<as");

        return alumno;
    }

    public int registro_alumno(API api, AlumnoVO alum) throws APIexception{
        JSONObject payload = new JSONObject();
        try
        {
            payload.put("userName", alum.getNombreUsuario());
            payload.put("password", alum.getPassword());
            payload.put("tipo", 0);
        } catch (JSONException ex) { return 10; }

        api.post("/api/register", payload);
        return -1;

    }

    public void anyadir_profesor_favorito(API api, ProfesorVO profesor) throws APIexception {
        JSONObject payload = new JSONObject();
        try
        {
            payload.put("profesor", profesor.getNombreUsuario());

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        api.post("/api/favoritos/add", payload);
    }

}
