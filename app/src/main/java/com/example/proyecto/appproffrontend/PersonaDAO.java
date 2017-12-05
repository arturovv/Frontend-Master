package com.example.proyecto.appproffrontend;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by android on 7/05/17.
 */

public class PersonaDAO {

    public JSONObject login(API api, PersonaVO per, int tipo) throws APIexception{
        /* Prepara la petición POST al backend */
        JSONObject payload = new JSONObject();
        try{
            payload.put("userName", per.getNombreUsuario());
            payload.put("password", per.getPassword());
            payload.put("tipo", tipo);
        } catch (JSONException ex) {}

        return api.post("/api/login", payload);
    }

}
