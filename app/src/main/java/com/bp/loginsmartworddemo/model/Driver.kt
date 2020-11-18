package com.bp.loginsmartworddemo.model

import java.io.Serializable

class Driver ( name: String, latitude: Float, longitude: Float) : Serializable {

    private var name : String = name
    private var latitude : Float = latitude
    private var longitude : Float = longitude



    fun setLatitude (Latitude : Float){
        this.latitude = Latitude
    }

    fun getLatitude() : Float ? {
        return latitude
    }

    fun setLongitude (Longitude : Float){
        this.longitude = Longitude
    }

    fun getLongitude() : Float ? {
        return longitude
    }

    fun setName (Name : String){
        this.name = Name
    }

    fun getName() : String ? {
        return name
    }




}