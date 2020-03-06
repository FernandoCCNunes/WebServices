package com.nando.webservices

import android.content.Context
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

open class WebServiceRequest {

    private val context: Context
    private var method: Int = Request.Method.GET
    private var urlPrefix: String = ""
    private var url: String = ""
    private var params = JSONObject()
    private var headers: MutableMap<String, String> = mutableMapOf()
    private var request: JsonObjectRequest? = null
    private var response: WebServiceResponse? = null
    private var onSuccessListener: ((response: WebServiceResponse) -> Unit)? = null
    private var onFailureListener: ((response: WebServiceResponse) -> Unit)? = null
    private var onCompleteListener: ((response: WebServiceResponse) -> Unit)? = null
    private var onCancelListener: (() -> Unit)? = null
    private var data: JSONObject? = null

    constructor(context: Context) {
        this.context = context
    }

    constructor(context: Context, url: String) {
        this.context = context
        this.url = url
    }

    constructor(context: Context, url: String, params: JSONObject) {
        this.context = context
        this.url = url
        this.params = params
    }

    fun get(): WebServiceRequest {
        method = Request.Method.GET
        return this
    }

    fun get(url: String): WebServiceRequest {
        method = Request.Method.GET
        this.url = url
        return this
    }

    fun get(url: String, params: JSONObject): WebServiceRequest {
        method = Request.Method.GET
        this.url = url
        this.params = params
        return this
    }

    fun post(): WebServiceRequest {
        method = Request.Method.POST
        return this
    }

    fun post(url: String): WebServiceRequest {
        this.method = Request.Method.POST
        this.url = url
        return this
    }

    fun post(url: String, params: JSONObject): WebServiceRequest {
        method = Request.Method.POST
        this.url = url
        this.params = params
        return this
    }

    fun put(): WebServiceRequest {
        method = Request.Method.PUT
        return this
    }

    fun put(url: String): WebServiceRequest {
        method = Request.Method.PUT
        this.url = url
        return this
    }

    fun put(url: String, params: JSONObject): WebServiceRequest {
        method = Request.Method.PUT
        this.url = url
        this.params = params
        return this
    }

    fun patch(): WebServiceRequest {
        method = Request.Method.PATCH
        return this
    }

    fun patch(url: String): WebServiceRequest {
        method = Request.Method.PATCH
        this.url = url
        return this
    }

    fun patch(url: String, params: JSONObject): WebServiceRequest {
        method = Request.Method.PATCH
        this.url = url
        this.params = params
        return this
    }

    fun delete(): WebServiceRequest {
        method = Request.Method.DELETE
        return this
    }

    fun delete(url: String): WebServiceRequest {
        method = Request.Method.DELETE
        this.url = url
        return this
    }

    fun delete(url: String, params: JSONObject): WebServiceRequest {
        method = Request.Method.DELETE
        this.url = url
        this.params = params
        return this
    }

    fun execute(): WebServiceRequest {
        makeUrlWithParams()
        request = object: JsonObjectRequest(this.method, "${this.urlPrefix}${this.url}", this.params,
            Response.Listener {
                this.data = it
                if (onSuccessListener != null ) {
                    this.onSuccessListener!!(this.response!!)
                }
                if (this.onCompleteListener != null) {
                    this.onCompleteListener!!(this.response!!)
                }
            },
            Response.ErrorListener {
                if (this.onCompleteListener != null) {
                    this.onCompleteListener!!(this.response!!)
                }
                if (this.onFailureListener != null) {
                    this.onFailureListener!!(this.response!!)
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return this@WebServiceRequest.getHeaders()
            }

            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                this@WebServiceRequest.response = WebServiceResponse(response)
                return super.parseNetworkResponse(response)
            }

            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                this@WebServiceRequest.response = WebServiceResponse(volleyError)
                return super.parseNetworkError(volleyError)
            }
        }
        request!!.retryPolicy = DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        Volley.newRequestQueue(context).add(request)

        return this
    }

    private fun makeUrlWithParams() {
        if (this.method == Request.Method.GET || this.method == Request.Method.DELETE) {
            val keys = params.keys()
            keys.forEach {
                addParamToUrl(it, this.params.get(it))
            }
        }
    }

    private fun addParamToUrl(key: String, value: Any?) {
        if (!this.url.contains("?")) {
            url += "?"
        } else if (this.url.last().toString() != "&") {
            url += "&"
        }
        url += "$key=$value"
    }


    fun addParams(params: JSONObject): WebServiceRequest {
        val keys = params.keys()
        keys.forEach {
            addParam(it, params.get(it))
        }
        return this
    }

    fun addParam(key: String, value: Any?): WebServiceRequest {
        this.params.put(key, value)
        return this
    }

    fun addHeaders(headers: MutableMap<String, String>): WebServiceRequest {
        for (header in headers) {
            addHeader(header.key, header.value)
        }
        return this
    }

    fun addHeader(key: String, value: String): WebServiceRequest {
        this.headers[key] = value
        return this
    }

    fun cancel() {
        if (request != null) {
            request!!.cancel()
            if (this.onCancelListener != null) {
                this.onCancelListener!!()
            }
        }
    }

    open fun getUrlPrefix(): String {
        return this.urlPrefix
    }

    open fun getUrl(): String {
        return this.url
    }

    open fun getHeaders(): MutableMap<String, String> {
        return this.headers
    }

    open fun getParams(): JSONObject {
        return this.params
    }

    open fun setOnSuccessListener(listener: (response: WebServiceResponse) -> Unit): WebServiceRequest {
        this.onSuccessListener = listener
        return this
    }

    open fun setOnFailureListener(listener: (response: WebServiceResponse) -> Unit): WebServiceRequest {
        this.onFailureListener = listener
        return this
    }

    open fun setOnCompleteListener(listener: (response: WebServiceResponse) -> Unit): WebServiceRequest {
        this.onCompleteListener = listener
        return this
    }

    open fun setOnCancelListener(listener: () -> Unit): WebServiceRequest {
        this.onCancelListener = listener
        return this
    }

}