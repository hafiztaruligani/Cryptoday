package com.hafiztaruligani.cryptoday.util


sealed class Resource <T> (){
    companion object{
        const val NETWORK_RESTRICTED = "Server calls have been restricted, wait 1 minute"
        const val NETWORK_UNAVAILABLE = "Somethings wrong, couldn't reach server"
    }

    class Success<T>(val data: T): Resource<T>()
    class Error<T>(val message: String = "An unexpected error occurred"): Resource<T>()
    class Loading<T>(): Resource<T>()

}
