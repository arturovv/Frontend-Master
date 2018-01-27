package com.example.proyecto.appproffrontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
//import android.app.AlertDialog.Builder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Modificar_Perfil_1 extends AppCompatActivity implements MultiSpinner.MultiSpinnerListener {

    private Facade facade = null;
    private ProfesorVO profesor = null;
    private API api;
    private TextView user;
    private EditText telefono;
    private EditText email;
    private EditText ciudad;
    private MultiSpinner horarios = null;
    private MultiSpinner asignaturas = null;
    private Button siguienteButton = null;
    private JSONObject respuesta;
    private InfoSesion sesion = null;

    // codigo de adrian
    private FirebaseAuth mAuth;

    //google maps code
    private FusedLocationProviderClient mFusedLocationClient;
    private int LOCATION_REQUEST_CODE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar__perfil_1);

        // codigo anyadido por adrian
        mAuth = FirebaseAuth.getInstance();
        //stop anyadido

        siguienteButton = (Button) findViewById(R.id.siguienteMod);

        sesion = new InfoSesion(this);
        api = new API(this);
        facade = new Facade(api);
        try {
            profesor = facade.perfilProfesor(sesion.getUsername());
            populateFields();
        } catch (APIexception ex) { respuesta = ex.json; }

        populateFields();
        final Intent i = new Intent(this, Modificar_Perfil_2.class);
        siguienteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                final int code = guardarEnBdProf(i);
                if (code == -1) startActivity(i);
                else error(code);
            }
        });

        //google maps code
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ((Button) findViewById(R.id.btnLocation)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(Modificar_Perfil_1.this, "Searching...",
                        Toast.LENGTH_SHORT).show();
                ciudad.setText("");
                getLocation();
            }
        });

    }

    private void populateFields() {

        telefono = (EditText) findViewById(R.id.tlfnoProfesorMod);
        telefono.setText(profesor.getTelefono());

        email = (EditText) findViewById(R.id.emailProfesorMod);
        email.setText(profesor.getMail());

        ciudad = (EditText) findViewById(R.id.ciudadProfesorMod);
        ciudad.setText(profesor.getCiudad());

        // AQUI IRA LA INFORMACION DEL MAPA PARA MODIFICAR

        user = (TextView) findViewById(R.id.usuarioProfesorMod);
        user.setText(profesor.getNombreUsuario());

        horarios = (MultiSpinner) findViewById(R.id.horariosProfesorMod);
        horarios.setItems(facade.getHorariosDisponibles(),facade.getHorariosDisponibles(),
                profesor.getHorarios(),"", this);

        asignaturas = (MultiSpinner) findViewById(R.id.asignaturasProfesorMod);
        asignaturas.setItems(facade.getAsignaturasDisponibles(),facade.getAsignaturasDisponibles(),
                profesor.getAsignaturas(),"", this);

    }

    private int guardarEnBdProf(final Intent i) {
        String mail = email.getText().toString();
        String phone = telefono.getText().toString();
        String city = ciudad.getText().toString();
        ArrayList<String> horariosProf = horarios.getValues();
        ArrayList<String> asignaturasProf = asignaturas.getValues();
        if (mail.isEmpty()) return 4;
        //if (!mail.matches("[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+")) return 5;
        else if (phone.isEmpty()) return 6;
        else if (!phone.matches("[0-9]{9}")) return 7;
        else if (city.isEmpty()) return 9;
        else if (horariosProf.isEmpty()) return 8;
        else if (asignaturasProf.isEmpty()) return 10;

        i.putExtra("profesor_tlf", phone);
        i.putExtra("profesor_mail", mail);
        i.putExtra("profesor_ciu", city);
        i.putExtra("profesor_hor", horariosProf);
        i.putExtra("profesor_asig", asignaturasProf);
        // aqui ira la localizacion, algo asi:
        //i.putExtra("profesor_long", asignaturasProf);
        //i.putExtra("profesor_lat", asignaturasProf);
        return -1;
    }

    private void error(int code) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        switch (code) {
            case 4:
                dlgAlert.setMessage("Rellene el campo de e-mail");
                break;
            case 5:
                dlgAlert.setMessage("Introduzca un e-mail valido");
                break;
            case 6:
                dlgAlert.setMessage("Rellene el campo de telefono");
                break;
            case 7:
                dlgAlert.setMessage("Introduzca un telefono valido");
                break;
            case 8:
                dlgAlert.setMessage("Debe seleccionar al menos un horario");
                break;
            case 9:
                dlgAlert.setMessage("Rellene el campo de Ciudad");
            case 10:
                dlgAlert.setMessage("Debe seleccionar al menos una asignatura");
        }
        dlgAlert.setTitle("Error...");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
    }
    public void onItemsSelected(boolean[] selected) {
        //Esto hay que ponerlo pero a saber para que... seguiremos investigando
    }


    //code for google maps
    private void getLocation() {
        try {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(Modificar_Perfil_1.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object

                                    double lon = location.getLongitude();
                                    double lat = location.getLatitude();

                                    Geocoder gcd = new Geocoder(Modificar_Perfil_1.this, Locale.getDefault());
                                    try {
                                        List<Address> addresses = gcd.getFromLocation(lat, lon, 1);
                                        if (addresses != null && addresses.size() > 0) {
                                            ciudad.setText(addresses.get(0).getLocality());
                                        } else {
                                            Toast.makeText(Modificar_Perfil_1.this, "No addresses found. Try again.", Toast.LENGTH_LONG).show();
                                        }

                                    }catch (Exception e) {
                                        Toast.makeText(Modificar_Perfil_1.this, "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                                    }


                                } else {
                                    Toast.makeText(Modificar_Perfil_1.this, "Error. Active Location Services and try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }

        } catch (SecurityException e) {
            Toast.makeText(Modificar_Perfil_1.this, "Permission error. Active Location Services and try again.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(Modificar_Perfil_1.this, "Permission error. Active Location Services and try again.", Toast.LENGTH_LONG).show();
        }

        ((Button) findViewById(R.id.btnLocation)).setText("TRY AGAIN");
    }
}
