package com.bp.loginsmartworddemo.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class AddressTextSearchResponse{
    @SerializedName("results")
    @Expose
    var results: ArrayList<AddressTextSearchModel>? = null
}

class AddressTextSearchModel {
    @SerializedName("formatted_address")
    @Expose
    var formattedAddress : String? = null

    @SerializedName("geometry")
    @Expose
    var geometry : AddressGeometry? = null

    @SerializedName("name")
    @Expose
    var name : String? = null

    @SerializedName("place_id")
    @Expose
    var place_id : String? = null
}

class AddressGeometry {
    @SerializedName("location")
    @Expose
    var location : AddressLocation? = null
}

class AddressLocation {
    @SerializedName("lat")
    @Expose
    var lat : Float = 0f

    @SerializedName("lng")
    @Expose
    var lng : Float = 0f
}

