package otus.homework.coroutines

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CatsPresenter(
    private val catsService: CatsService
) {

    private var _catsView: ICatsView? = null

    private val presenterScope = CoroutineScope(Dispatchers.Main + CoroutineName("CatsCoroutine"))

    private var getCatFactsJob: Job? = null

    fun onInitComplete() {
        getCatFactsJob = presenterScope.launch {
            try {
                val result = catsService.getCatFact()
                _catsView?.populate(result)
            } catch (exception: Exception) {
                when (exception) {
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
        getCatFactsJob?.cancel()
        _catsView = null
    }
}