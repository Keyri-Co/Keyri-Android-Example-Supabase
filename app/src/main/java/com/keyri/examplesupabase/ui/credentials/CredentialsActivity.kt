package com.keyri.examplesupabase.ui.credentials

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.keyri.examplesupabase.databinding.ActivityCredentialsBinding

class CredentialsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCredentialsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityCredentialsBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        with(binding) {
            bPassCredentials.setOnClickListener {
                val email = etEmail.getNotEmptyText()
                val password = etPassword.getNotEmptyText()

                if (email != null && password != null) {
                    val intent = Intent().apply {
                        putExtra(EMAIL_EXTRA_KEY, email)
                        putExtra(PASSWORD_EXTRA_KEY, password)
                    }

                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@CredentialsActivity,
                        "Email and password must not be empty",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun EditText.getNotEmptyText(): String? {
        return text?.takeIf { it.isNotEmpty() }?.toString()
    }

    companion object {
        const val EMAIL_EXTRA_KEY = "EMAIL_EXTRA_KEY"
        const val PASSWORD_EXTRA_KEY = "PASSWORD_EXTRA_KEY"
    }
}
