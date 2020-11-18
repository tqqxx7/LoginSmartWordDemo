package com.bp.loginsmartworddemo.model

import java.io.Serializable

class User ( fullName: String, email: String, phone: String, photo: String, address: String) : Serializable {

    private var email : String = email
    private var fullName : String = fullName
    private var phone : String = phone
    private var photo : String = photo
    private var address : String = address



    fun setEmail (Email : String){
        this.email = Email
    }

    fun getEmail() : String ? {
        return email
    }

    fun setFullName (FullName : String){
        this.fullName = FullName
    }

    fun getFullName() : String ? {
        return fullName
    }


//    fun setPassword (Password : String){
//        this.password = Password
//    }
//
//    fun getPassword() : String ? {
//        return password
//    }

    fun setPhone (Phone : String){
        this.phone = Phone
    }

    fun getPhone() : String ? {
        return phone
    }

    fun setPhoto (Photo : String){
        this.photo = Photo
    }

    fun getPhoto() : String ? {
        return photo
    }

    fun setAddress(Address: String) {
        this.address = Address
    }

    fun getAddress() : String ? {
        return address
    }


}