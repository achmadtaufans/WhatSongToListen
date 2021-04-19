import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.project.whatsongtolisten.common.repository.MusicRepository
import com.project.whatsongtolisten.common.repository.MusicService
import com.project.whatsongtolisten.ui.main.MainViewModel
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}

private val repositoryModue = module {
    single {
        createService<MusicService>(createConfigOkHttpClient())
    }
    single { MusicRepository(get()) }
}

private val appModule = listOf(repositoryModue, viewModelModule)

lateinit var koin: Koin

fun initDependencyInjection(ctx: Context) {
    koin = startKoin {
        androidLogger(Level.DEBUG)
        androidContext(ctx)
        modules(appModule)
    }.koin
}

inline fun <reified T : Any> inject() = lazy { get<T>() }
inline fun <reified T : Any> get(): T = koin.get()

inline fun <reified T> createService(okHttpClient: OkHttpClient): T {
    val gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .serializeNulls()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
        .setPrettyPrinting()
        .setVersion(1.0)
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    return retrofit.create(T::class.java)
}

fun createConfigOkHttpClient(): OkHttpClient {
    Interceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .addHeader("Cache-control", "no-cache")
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build()

        chain.proceed(request)
    }

    return OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .cache(null)
        .build()
}
