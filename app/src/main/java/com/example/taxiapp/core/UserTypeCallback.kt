package com.example.taxiapp.core

interface UserTypeCallback {
    fun onUserTypeFound(userType: UserType?)
    fun onUserTypeNotFound()
}