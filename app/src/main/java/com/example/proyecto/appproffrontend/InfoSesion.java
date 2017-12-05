package com.example.proyecto.appproffrontend;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by android on 8/05/17.
 */

public class InfoSesion {

    private String username;
    private int tipo;

    private SharedPreferences sharedPref;

    public InfoSesion (Activity contexto)
    {
        //sharedPref = contexto.getPreferences(Context.MODE_PRIVATE);
        sharedPref = contexto.getSharedPreferences("APPROF", Context.MODE_PRIVATE);
        this.tipo = sharedPref.getInt("tipo", -1);
        this.username = sharedPref.getString("username", null);
    }

    public InfoSesion (Activity contexto, String _username, int _tipo)
    {
        SharedPreferences sharedPref = contexto.getSharedPreferences("APPROF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", _username);
        editor.putInt("tipo", _tipo);
        editor.apply();

        this.tipo = _tipo;
        this.username = _username;
    }

    public String getUsername() {
        return username;
    }

    public int getTipo ()
    {
        return this.tipo;
    }

    public void set(String _username, int _tipo) {
        SharedPreferences.Editor editor = sharedPref.edit();
        if (_username != null) {
            editor.putString("username", _username);
            username = _username;
        }
        editor.putInt("tipo", _tipo);
        editor.apply();

    }
}
