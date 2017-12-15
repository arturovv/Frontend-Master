package com.example.proyecto.appproffrontend;

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
    public void setPassword(String password) {
        this.password = password;
    }

}
