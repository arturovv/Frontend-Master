package com.example.proyecto.appproffrontend;

public class AlumnoVO extends PersonaVO{

    public AlumnoVO(String nombreUsuario, String password) {
        if (nombreUsuario != null) super.nombreUsuario = nombreUsuario;
        if (password != null) super.password = password;
    }

}
