package otus.homework.coroutines

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val diContainer = DiContainer()

    private val catsViewModel by viewModels<CatsViewModel> {
        CatsViewModelFactory(
            diContainer.service,
            diContainer.imageService
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        setContentView(view)

        view.viewModel = catsViewModel

        catsViewModel.onInitComplete()

        catsViewModel.resultLiveData.observe(this) { result ->
            when (result) {
                is Result.Success<*> -> {
                    val catUi = result.value as? CatUi
                    if (catUi != null) {
                        view.populate(catUi)
                    }
                }

                is Result.Error -> {
                    view.showToast(result.message)
                }
            }
        }
    }
}