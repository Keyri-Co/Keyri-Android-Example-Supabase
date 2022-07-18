package com.keyri.examplesupabase.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.keyri.examplesupabase.BuildConfig
import com.keyri.examplesupabase.data.AuthResponse
import com.keyri.examplesupabase.databinding.ActivityMainBinding
import com.keyri.examplesupabase.ui.credentials.CredentialsActivity
import com.keyri.examplesupabase.ui.credentials.CredentialsActivity.Companion.EMAIL_EXTRA_KEY
import com.keyri.examplesupabase.ui.credentials.CredentialsActivity.Companion.PASSWORD_EXTRA_KEY
import com.keyrico.keyrisdk.ui.auth.AuthWithScannerActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.HttpException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val easyKeyriAuthLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val text = if (it.resultCode == RESULT_OK) "Authenticated" else "Failed to authenticate"

            binding.progress.isVisible = false

            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }

    private val getCredentialsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val email = it.data?.getStringExtra(EMAIL_EXTRA_KEY)
                val password = it.data?.getStringExtra(PASSWORD_EXTRA_KEY)

                binding.progress.isVisible = true

                lifecycleScope.launch {
                    mainViewModel.signup(
                        BuildConfig.API_KEY,
                        checkNotNull(email),
                        checkNotNull(password)
                    )
                        .catch { login(email, password) }
                        .onEach(::processPayload)
                        .collect()
                }
            }
        }

    private val mainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.bSupabaseAuth.setOnClickListener {
            authWithSupabase()
        }
    }

    private fun login(email: String, password: String) {
        lifecycleScope.launch {
            mainViewModel.login(BuildConfig.API_KEY, email, password)
                .handleErrors()
                .onEach(::processPayload)
                .collect()
        }
    }

    private fun authWithSupabase() {
        getCredentialsLauncher.launch(Intent(this, CredentialsActivity::class.java))
    }

    private fun processPayload(authResponse: AuthResponse) {
        val email = authResponse.user.email

        val payload = JSONObject().apply {
            put("refreshToken", authResponse.refreshToken)
        }.toString()

        keyriAuth(email, payload)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun <T> Flow<T>.handleErrors(): Flow<T> = catch { e ->
        binding.progress.isVisible = false

        val message = if (e is HttpException) {
            val errorBody = e.response()?.errorBody()

            errorBody?.string()?.let {
                JSONObject(it).getString("msg")
            } ?: e.message ?: "Something went wrong"
        } else {
            e.message.toString()
        }

        Log.e("Keyri example", message)

        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }

    private fun keyriAuth(publicUserId: String?, payload: String) {
        val intent = Intent(this, AuthWithScannerActivity::class.java).apply {
            putExtra(AuthWithScannerActivity.APP_KEY, "raB7SFWt27woKqkPhaUrmWAsCJIO8Moj")
            putExtra(AuthWithScannerActivity.PUBLIC_USER_ID, publicUserId)
            putExtra(AuthWithScannerActivity.PAYLOAD, payload)
        }

        easyKeyriAuthLauncher.launch(intent)
    }
}
