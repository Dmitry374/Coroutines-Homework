package otus.homework.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CatsPresenter(
    private val catsService: CatsService,
    private val catImagesService: CatImagesService
) {

    private var _catsView: ICatsView? = null

    private val presenterScope = CoroutineScope(Dispatchers.Main + CoroutineName("CatsCoroutine"))

    fun onInitComplete() {
        presenterScope.launch {
            try {
                val factDeferred = async { catsService.getCatFact() }
                val catImageDeferred = async { catImagesService.getCatImage() }

                val fact = factDeferred.await()
                val catImages = catImageDeferred.await()

                val imageUrl = catImages.firstOrNull()?.url ?: ""

                val catUi = CatUi(
                    fact = fact.fact,
                    imageUrl = imageUrl
                )

                _catsView?.populate(catUi)
            } catch (exception: Exception) {
                when (exception) {
                    is CancellationException -> {
                        throw exception
                    }

                    is java.net.SocketTimeoutException -> {
                        _catsView?.showToast("Не удалось получить ответ от сервера")
                    }

                    else -> {
                        CrashMonitor.trackWarning()
                        _catsView?.showToast(exception.message ?: "Возникла ошибка")
                    }
                }
            }
        }
    }

    fun attachView(catsView: ICatsView) {
        _catsView = catsView
    }

    fun detachView() {
        presenterScope.cancel()
        _catsView = null
    }
}