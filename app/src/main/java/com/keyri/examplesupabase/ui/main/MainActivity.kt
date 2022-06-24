package com.keyri.examplesupabase.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.keyri.examplesupabase.BuildConfig
import com.keyri.examplesupabase.databinding.ActivityMainBinding
import com.keyri.examplesupabase.ui.credentials.CredentialsActivity
import com.keyri.examplesupabase.ui.credentials.CredentialsActivity.Companion.EMAIL_EXTRA_KEY
import com.keyri.examplesupabase.ui.credentials.CredentialsActivity.Companion.PASSWORD_EXTRA_KEY
import com.keyrico.keyrisdk.Keyri
import com.keyrico.keyrisdk.ui.auth.AuthWithScannerActivity
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val easyKeyriAuthLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val text = if (it.resultCode == RESULT_OK) "Authenticated" else "Failed to authenticate"

            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }

    private val getCredentialsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val email = it.data?.getStringExtra(EMAIL_EXTRA_KEY)
                val password = it.data?.getStringExtra(PASSWORD_EXTRA_KEY)

                mainViewModel.authorize(
                    BuildConfig.API_KEY,
                    checkNotNull(email),
                    checkNotNull(password)
                )
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

        observeViewModel()
    }

    private fun authWithSupabase() {
        getCredentialsLauncher.launch(Intent(this, CredentialsActivity::class.java))
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.authResponseFlow.collect {
                    it?.let { authResponse ->
                        val email = authResponse.user.email
                        val keyri = Keyri()

                        val tokenData = JSONObject().apply {
                            put("accessToken", authResponse.accessToken)
                            put("tokenType", authResponse.tokenType)
                            put("refreshToken", authResponse.refreshToken)
                            put("expiresIn", authResponse.expiresIn)
                        }

                        val userProfileData = JSONObject().apply {
                            put("email", authResponse.user.email)
                            put("id", authResponse.user.id)
                            put("phone", authResponse.user.phone)
                        }

                        val data = JSONObject().apply {
                            put("token", tokenData)
                            put("userProfile", userProfileData)
                        }

                        val signingData = JSONObject().apply {
                            put("timestamp", System.currentTimeMillis())
                            put("email", email)
                            put("uid", authResponse.user.id)
                        }.toString()

                        val signature = keyri.getUserSignature(email, signingData)

                        val payload = JSONObject().apply {
                            put("data", data)
                            put("signingData", signingData)
                            put("userSignature", signature) // Optional
                            put("associationKey", keyri.getAssociationKey(email)) // Optional
                        }.toString()

                        mainViewModel.clear()

                        keyriAuth(email, payload)
                    }
                }
            }
        }
    }

    private fun keyriAuth(publicUserId: String?, payload: String) {
        val intent = Intent(this, AuthWithScannerActivity::class.java).apply {
            putExtra(AuthWithScannerActivity.APP_KEY, "IT7VrTQ0r4InzsvCNJpRCRpi1qzfgpaj")
            putExtra(AuthWithScannerActivity.PUBLIC_USER_ID, publicUserId)
            putExtra(AuthWithScannerActivity.PAYLOAD, payload)
        }

        easyKeyriAuthLauncher.launch(intent)
    }
}
