package com.uoko.frame.common
import retrofit2.*
import retrofit2.CallAdapter.Factory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class UKCallAdapterFactory : Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != UKCall::class.java) {
            return null
        }
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = getRawType(observableType)
//        if (rawObservableType != ApiResponse::class.java) {
//            throw IllegalArgumentException("type must be a resource")
//        }
//        if (observableType !is ParameterizedType) {
//            throw IllegalArgumentException("resource must be parameterized")
//        }
        val bodyType = getParameterUpperBound(0, returnType)
        return UKCallAdapter<Any>(bodyType)
    }


    class UKCallAdapter<R>(var type:Type):CallAdapter<R, UKCall<R>>{
        override fun adapt(call: Call<R>?): UKCall<R> {
            val ukCall =  UKCall<R>()

            return  ukCall.apply {
                this.call = call
            }
        }
        override fun responseType(): Type {
            return type
        }
    }

}