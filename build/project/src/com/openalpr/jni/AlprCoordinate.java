package com.openalpr.jni;


import net.yasir.json.JSONException;
import net.yasir.json.JSONObject;

public class AlprCoordinate {
    private final int x;
    private final int y;

    AlprCoordinate(JSONObject coordinateObj) throws JSONException
    {
        x = coordinateObj.getInt("x");
        y = coordinateObj.getInt("y");
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
