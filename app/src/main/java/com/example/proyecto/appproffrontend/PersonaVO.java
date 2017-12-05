package com.example.proyecto.appproffrontend;

/**
 * Created by android on 7/05/17.
 */

public class PersonaVO {

    protected String nombreUsuario = null;
    protected String password = null;

    public PersonaVO() {

    }

    public PersonaVO(String nombreUsuario, String password) {

        if (nombreUsuario != null) this.nombreUsuario = nombreUsuario;
        if (password != null) this.password = password;

    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String telefono) {
        this.password = password;
    }

}
