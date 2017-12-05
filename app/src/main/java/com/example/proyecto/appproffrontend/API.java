package com.example.proyecto.appproffrontend;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class API
{
	// Direccion base al backend
    private String baseurl = new String("http://approf-approf.1d35.starter-us-east-1.openshiftapps.com");

	// Token de la app
	private String token;

    // Almacenamiento token
    private SharedPreferences sharedPref;
	/*
	 * Constructor de la clase. baseurl es del tipo http://localhost:8080
	 */
    public API(Activity _context) {
        // Lee de la memoria del telefono si se tenía el token (Inicio automatico)
        this.sharedPref = _context.getSharedPreferences("APPROF", Context.MODE_PRIVATE);
        this.token = sharedPref.getString("token", null);
	}

	/*
	 *  Lanza una petición a la url dada ( url hace referencia a tipo /api/login )
	 *  y devuelve un objeto JSON parseado
	 */
	public JSONObject get (String _url) throws APIexception
	{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        JSONObject jObject;
        boolean error = false;
        try {
            URL url = new URL(this.baseurl + _url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.setRequestMethod("GET");
            connection.setDoInput(true);

            // Si se disponía de token, se envía con la petición
            if (token != null) {
                connection.setRequestProperty("xtoken", this.token);
            }

            connection.connect();

            InputStream inputStream;

            try
            {
                inputStream = connection.getInputStream();
            } catch (IOException ex) {
                inputStream = connection.getErrorStream();
                error = true;
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null) {
                sb.append(line + "\n");
            }
            connection.disconnect();

            jObject = new JSONObject(sb.toString());

            if (error) {
                throw new APIexception(connection.getResponseCode(), jObject);
            }

        } catch (APIexception ex) { throw new APIexception(ex.code, ex.json); }
          catch (Exception ex)    { throw new APIexception(-1); }

        // Si se ha devuelto un token, se adopta como nuevo token de la app
        try
        {
        	this.token = jObject.getString("token");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("token", this.token);
            editor.commit();
        } catch (JSONException ex) {}
        return jObject;
	}

	/*
	 *  Lanza una petición a la url dada ( url hace referencia a tipo /api/login )
	 *  y devuelve un array JSON
	 */
	public JSONArray getArray (String _url) throws APIexception
	{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        JSONArray jObject;
        boolean error = false;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(this.baseurl + _url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);

            // Si se disponía de token, se envía con la petición
            if (token != null) {
                connection.setRequestProperty("xtoken", this.token);
            }

            connection.connect();
            InputStream inputStream;

            try
            {
                inputStream = connection.getInputStream();
            } catch (IOException ex) {
                inputStream = connection.getErrorStream();
                error = true;
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();

            String line;

            while ((line = rd.readLine()) != null) {
                sb.append(line + "\n");
            }
            connection.disconnect();

            jObject = new JSONArray(sb.toString());

            if (error) {
                throw new APIexception(connection.getResponseCode(), jObject);
            }
        } catch (Exception ex) {jObject = new JSONArray();}
        finally {
            connection.disconnect();
        }

        return jObject;
	}

	/*
	 * Manda una petición POST a la url /api/login. 
	 * <payload> contiene un json en formato string.
	 * Devuelve un objeto JSON
	 */
	public JSONObject post (String _url, JSONObject payload) throws APIexception
	{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        boolean error = false;
        JSONObject jObject;
        try {
            URL url = new URL(this.baseurl + _url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Si se disponía de token, se envía con la petición
            if (token != null) {
                connection.setRequestProperty("xtoken", this.token);
            }

            connection.connect();

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload.toString());
            writer.close();

            InputStream inputStream;
            try {
                inputStream = connection.getInputStream();
            } catch (IOException ex)
            {
                inputStream = connection.getErrorStream();
                error = true;
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null) {
                sb.append(line + "\n");
            }
            connection.disconnect();

            jObject = new JSONObject(sb.toString());

            if (error) {
                Log.e("API", "[" + connection.getResponseCode() + "] " + sb.toString());
                throw new APIexception(connection.getResponseCode(), jObject);
            }
        } catch (APIexception ex) { throw new APIexception(ex.code, ex.json); }
          catch (Exception ex)    { throw new APIexception(-1); }

        // Si se ha devuelto un token, se adopta como nuevo token de la app
        try
        {
            this.token = jObject.getString("token");
            int tipo = payload.getInt("tipo");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("token", this.token);
            editor.putInt("tipo", tipo);
            editor.commit();
        } catch (JSONException ex) {}

        return jObject;
	}

	/*
	 * Manda una petición POST a la url /api/login.
	 * <payload> contiene un json en formato string.
	 * Devuelve un array de JSON
	 */
	public JSONArray postArray (String _url, JSONObject payload) throws APIexception
	{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        JSONArray jObject;
        boolean error = false;
        try {
            URL url = new URL(this.baseurl + _url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Si se disponía de token, se envía con la petición
            if (token != null) {
                connection.setRequestProperty("xtoken", this.token);
            }

            connection.connect();

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload.toString());
            writer.close();

            InputStream inputStream;

            try
            {
                inputStream = connection.getInputStream();
            } catch (IOException ex) {
                inputStream = connection.getErrorStream();
                error = true;
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null) {
                sb.append(line + "\n");
            }
            connection.disconnect();

            jObject = new JSONArray(sb.toString());

            if (error) {
                throw new APIexception(connection.getResponseCode(), jObject);
            }
        } catch (Exception ex) {jObject = new JSONArray(); }

        return jObject;
	}
}