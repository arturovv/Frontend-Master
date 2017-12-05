package com.example.proyecto.appproffrontend;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class Pagar_Profesor extends AppCompatActivity {

    private static final ArrayList<String> mesCadList = new ArrayList<String>() {{
        add("---");
        add("01");
        add("02");
        add("03");
        add("04");
        add("05");
        add("06");
        add("07");
        add("08");
        add("09");
        add("10");
        add("11");
        add("12");
    }};
    private static final ArrayList<String> anyoCadList = new ArrayList<String>() {{
        add("---");
        add("2016");
        add("2017");
        add("2018");
        add("2019");
        add("2020");
        add("2021");
        add("2022");
        add("2023");
        add("2024");
        add("2025");
        add("2026");
        add("2027");
    }};
    private TextView user;
    private EditText tarjeta;
    private EditText digito;
    private EditText titular;
    private Button pagarButton = null;
    private Spinner mesCad = null;
    private Spinner anyoCad = null;
    private Facade facade;
    private API api;
    private InfoSesion info;
    private ProfesorVO profesor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagar__profesor);

        info = new InfoSesion(this);
        api = new API(this);
        facade = new Facade(api);
        try {
            profesor = facade.perfilProfesor(info.getUsername());
            populateFields();
        } catch (APIexception ex) {}

        final com.example.proyecto.appproffrontend.Pagar_Profesor local = this;
        pagarButton = (Button) findViewById(R.id.pagarProfesor);

        pagarButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(tarjeta.getText().toString().equals("") ||
                   digito.getText().toString().equals("") ||
                   titular.getText().toString().equals("") ||
                   mesCad.getSelectedItemPosition() == 0 ||
                   anyoCad.getSelectedItemPosition() == 0)
                {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(local);

                    dlgAlert.setMessage("Es necesario añadir todos los campos");
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
                    try {
                        facade.profesorPagar();
                    } catch (APIexception ex) { System.out.println(ex.getMessage());}
                    finish();
                }
            }
        });
    }

    private void populateFields() {

        tarjeta = (EditText) findViewById(R.id.tar_credito);
        digito = (EditText) findViewById(R.id.digito);
        titular = (EditText) findViewById(R.id.propietario);

        user = (TextView) findViewById(R.id.usuarioProfesorPagar);
        user.setText(profesor.getNombreUsuario());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.row_spinner, mesCadList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mesCad = (Spinner) findViewById(R.id.caducidad_mes);
        mesCad.setAdapter(adapter);

        adapter = new ArrayAdapter<>(
                this, R.layout.row_spinner, anyoCadList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        anyoCad = (Spinner) findViewById(R.id.caducidad_anyo);
        anyoCad.setAdapter(adapter);
    }
}
