package otus.homework.coroutines

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DiContainer {

    private val retrofit by lazy {
        getRetrofit("https://catfact.ninja/")
    }

    private val imageRetrofit by lazy {
        getRetrofit("https://api.thecatapi.com/")
    }

    val service: CatsService by lazy { retrofit.create(CatsService::class.java) }

    val imageService: CatImagesService by lazy { imageRetrofit.create(CatImagesService::class.java) }

    private fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}