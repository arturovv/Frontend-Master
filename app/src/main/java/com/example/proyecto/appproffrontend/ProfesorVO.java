package com.example.proyecto.appproffrontend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfesorVO extends PersonaVO implements Comparable<ProfesorVO>{

    private String telefono = null;
    private String mail = null;
    private String ciudad = null;
    // anyadido
    private String longitud=null;
    private String latitud=null;
    //acaba anyadido
    private ArrayList<String> horarios = null;
    private ArrayList<String> cursos = null;
    private ArrayList<String> asignaturas = null;
    private Double valoracion = -1.00;
    private String experiencia = null;
    private String modalidad = null;
    private String id = null;
    private int DiasPromocionRestante = 0;

    public ProfesorVO() { }
    public ProfesorVO (String nombreUsuario, String password, String telefono, String mail, String ciudad,
                       ArrayList<String> horarios, ArrayList<String> cursos,
                       ArrayList<String> asignaturas, Double valoracion, String experiencia,
                       String modalidad,String longitud,String latitud) {

        if (nombreUsuario != null) super.nombreUsuario = nombreUsuario;
        if (password != null) super.password = password;
        if (telefono != null) this.telefono = telefono;
        if (mail != null) this.mail = mail;
        if (ciudad != null) this.ciudad = ciudad;
        if (horarios != null) this.horarios = horarios;
        if (cursos != null) this.cursos = cursos;
        if (asignaturas != null) this.asignaturas = asignaturas;
        if (valoracion != -1.0f) this.valoracion = valoracion;
        if (experiencia != null) this.experiencia = experiencia;
        if (modalidad != null) this.modalidad = modalidad;
        if (longitud != null) this.longitud = longitud;
        if (latitud != null) this.latitud = latitud;

    }

    public ProfesorVO (String id, String nombreUsuario, String password, String telefono, String mail, String ciudad,
                       ArrayList<String> horarios, ArrayList<String> cursos,
                       ArrayList<String> asignaturas, Double valoracion, String experiencia,
                       String modalidad,String longitud,String latitud) {

        if (id != null) this.id = id;
        if (nombreUsuario != null) super.nombreUsuario = nombreUsuario;
        if (password != null) super.password = password;
        if (telefono != null) this.telefono = telefono;
        if (mail != null) this.mail = mail;
        if (ciudad != null) this.ciudad = ciudad;
        if (horarios != null) this.horarios = horarios;
        if (cursos != null) this.cursos = cursos;
        if (asignaturas != null) this.asignaturas = asignaturas;
        if (valoracion != -1.0f) this.valoracion = valoracion;
        if (experiencia != null) this.experiencia = experiencia;
        if (modalidad != null) this.modalidad = modalidad;
        if (longitud != null) this.longitud = longitud;
        if (latitud != null) this.latitud = latitud;

    }

    public ProfesorVO (JSONObject jsonObject) {
        try {
            if (jsonObject.has("_id")) this.id = jsonObject.getString("_id");
            if (jsonObject.has("userName")) super.nombreUsuario = jsonObject.getString("userName");
            if (jsonObject.has("password")) super.password = jsonObject.getString("password");
            if (jsonObject.has("telefono")) this.telefono = jsonObject.getString("telefono");
            if (jsonObject.has("mail")) this.mail = jsonObject.getString("mail");
            if (jsonObject.has("ciudad")) this.ciudad = jsonObject.getString("ciudad");
            if (jsonObject.has("horarios")) {
                horarios = new ArrayList<>();
                JSONArray jArray = jsonObject.getJSONArray("horarios");
                for (int i = 0; i < jArray.length(); i++) {
                    horarios.add(jArray.getString(i));
                }
            }
            if (jsonObject.has("cursos")) {
                cursos = new ArrayList<>();
                JSONArray jArray = jsonObject.getJSONArray("cursos");
                for (int i = 0; i < jArray.length(); i++) {
                    cursos.add(jArray.getString(i));
                }
            }
            if (jsonObject.has("asignaturas")) {
                asignaturas = new ArrayList<>();
                JSONArray jArray = jsonObject.getJSONArray("asignaturas");
                for (int i = 0; i < jArray.length(); i++) {
                    asignaturas.add(jArray.getJSONObject(i).getString("nombre"));
                }
            }
            if (jsonObject.has("valoracionMedia")) this.valoracion = jsonObject.getDouble("valoracionMedia");
            if (jsonObject.has("experiencia")) this.experiencia = jsonObject.getString("experiencia");
            if (jsonObject.has("modalidad")) this.modalidad = jsonObject.getString("modalidad");
            if (jsonObject.has("diasPromocionRestantes")) this.DiasPromocionRestante =
                    jsonObject.getInt("diasPromocionRestantes");


        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public int compareTo(ProfesorVO prof) {
        if(this.DiasPromocionRestante > prof.DiasPromocionRestante)
            return -1;
        else if(this.DiasPromocionRestante < prof.DiasPromocionRestante)
            return 1;
        else if(this.getValoracion() > prof.getValoracion())
            return -1;
        else if(this.getValoracion() < prof.getValoracion())
            return 1;
        else
            return 0;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public ArrayList<String> getHorarios() {
        return horarios;
    }
    public void setHorarios(ArrayList<String> horarios) {
        this.horarios = horarios;
    }

    public ArrayList<String> getCursos() {
        return cursos;
    }
    public void setCursos(ArrayList<String> cursos) {
        this.cursos = cursos;
    }

    public ArrayList<String> getAsignaturas() {
        return asignaturas;
    }
    public void setAsignaturas(ArrayList<String> asignaturas) {
        this.asignaturas = asignaturas;
    }

    public Double getValoracion() {
        return valoracion;
    }
    public void setValoracion(Double valoracion) {
        this.valoracion = valoracion;
    }

    public String getExperiencia() {
        return experiencia;
    }
    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public String getModalidad() {
        return modalidad;
    }
    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getLongitud() {
        return longitud;
    }
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud(){return latitud;};

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public int getDiasPromocionRestante() {
        return DiasPromocionRestante;
    }
    public void setDiasPromocionRestante(int diasPromocionRestante) {
        DiasPromocionRestante = diasPromocionRestante;
    }
}
