package com.psssum.testsdk
interface OnFailListener {
    fun onError(error : String)
}
object Constants {
    val TOKEN = "TOKEN"
    val DEVICE_ID = "DEVICE_ID"
    val API_KEY = "API_KEY"
    val USER_ID = "USER_ID"
    var onFailListener : OnFailListener? = null
}