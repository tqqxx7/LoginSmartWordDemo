package com.bp.loginsmartworddemo.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Ebike: Serializable {
    @SerializedName("brand")
    @Expose
    var brand: String? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null
    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("price")
    @Expose
    var price: String? = null
    @SerializedName("photo")
    @Expose
    var photo: String? = null
    @SerializedName("rating")
    @Expose
    var rating: ArrayList<Rating>? = null
    @SerializedName("payment")
    @Expose
    var payment: Payment? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("address")
    @Expose
    var address: String? = null
    @SerializedName("phone")
    @Expose
    var phone: String? = null
    @SerializedName("booked")
    @Expose
    var booked: Boolean? = null

    var rate: Float = 0f

    constructor(id: Int?, name: String?,latitude: Double?, longitude: Double?, brand: String?, price: String?, address: String?, photo: String?, phone: String?, booked: Boolean?) {
        this.name = name
        this.brand = brand
        this.price = price
        this.address = address
        this.photo = photo
        this.id = id
        this.latitude = latitude
        this.longitude = longitude
        this.phone = phone
        this.booked = booked
    }


    class Rating {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("rate")
        @Expose
        var rate: Float = 0f

        @SerializedName("feedback")
        @Expose
        var feedback: String? = null

        @SerializedName("id_user")
        @Expose
        var id_user: String? = null

    }

    class Payment {
        @SerializedName("cash")
        @Expose
        var cash: Boolean? = null

        @SerializedName("momo")
        @Expose
        var momo: Boolean? = null

        @SerializedName("banking")
        @Expose
        var banking: Boolean? = null

    }

}