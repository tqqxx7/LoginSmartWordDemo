package com.bp.loginsmartworddemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserId {
    @SerializedName("userId")
    @Expose
    private var userId: String? = null

    constructor(userId: String?) {
        this.userId = userId
    }


    fun getUserId(): String? {
        return userId
    }
}