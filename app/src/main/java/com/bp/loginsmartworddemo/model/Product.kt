package com.bp.loginsmartworddemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Product {
    @SerializedName("name")
    @Expose
    private var name: String? = null
    @SerializedName("description")
    @Expose
    private var description: String? = null
    @SerializedName("price")
    @Expose
    private var price: String? = null
    @SerializedName("address")
    @Expose
    private var address: String? = null
    @SerializedName("avatar")
    @Expose
    private var avatar: String? = null

    constructor(name: String?, description: String?, price: String?, address: String?, avatar: String?) {
        this.name = name
        this.description = description
        this.price = price
        this.address = address
        this.avatar = avatar
    }


    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun getPrice(): String? {
        return price
    }

    fun setPrice(price: String?) {
        this.price = price
    }

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?) {
        this.address = address
    }

    fun getAvatar(): String? {
        return avatar
    }

    fun setAvatar(avatar: String?) {
        this.avatar = avatar
    }

}