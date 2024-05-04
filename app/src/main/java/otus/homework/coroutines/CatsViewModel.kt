package otus.homework.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CatsViewModel(
    private val catsService: CatsService,
    private val catImagesService: CatImagesService
) : ViewModel() {

    private val handler = CoroutineExceptionHandler { _, exception ->
        _resultLiveData.value = Result.Error(exception.message ?: "Возникла ошибка")
        CrashMonitor.trackWarning()
    }

    private val _resultLiveData = MutableLiveData<Result>()
    val resultLiveData: LiveData<Result> = _resultLiveData

    fun onInitComplete() {
        viewModelScope.launch(handler) {
            val factDeferred = async { catsService.getCatFact() }
            val catImageDeferred = async { catImagesService.getCatImage() }

            val fact = factDeferred.await()
            val catImages = catImageDeferred.await()

            val imageUrl = catImages.firstOrNull()?.url ?: ""

            val catUi = CatUi(
                fact = fact.fact,
                imageUrl = imageUrl
            )

            _resultLiveData.value = Result.Success(catUi)
        }
    }
}

class CatsViewModelFactory(
    private val catsService: CatsService,
    private val catImagesService: CatImagesService
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CatsViewModel(catsService, catImagesService) as T
}

sealed class Result {
    data class Success<T>(val value: T) : Result()
    data class Error(val message: String) : Result()
}
