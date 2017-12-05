package com.example.proyecto.appproffrontend;

import org.json.JSONArray;
import org.json.JSONObject;

public class APIexception extends Exception {

    public int code;
    public JSONObject json;
    public JSONArray jsonArray;

    public APIexception (int _code, JSONObject _json)
    {
        this.code = _code;
        this.json = _json;
        this.jsonArray = null;
    }

    public APIexception (int _code, JSONArray _json)
    {
        this.code = _code;
        this.json = null;
        this.jsonArray = _json;
    }

    public APIexception (int _code)
    {
        this.code = _code;
        this.json = null;
        this.jsonArray = null;
    }
}
