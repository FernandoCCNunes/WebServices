package com.nando.webservices

import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import org.json.JSONObject

open class WebServiceResponse {
    val isSuccessful: Boolean
    val statusCode: Int
    var data: JSONObject? = null
    var cause: Throwable? = null
    var message: String? = null

    constructor(networkResponse: NetworkResponse?) {
        this.isSuccessful = true
        this.statusCode = networkResponse?.statusCode?: -1
    }

    constructor(volleyError: VolleyError?) {
        this.isSuccessful = false
        this.statusCode = volleyError?.networkResponse?.statusCode?: -1
    }
}