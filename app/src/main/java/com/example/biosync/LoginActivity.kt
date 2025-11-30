package com.example.biosync

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: MaterialButton
    private lateinit var buttonFacebook: MaterialButton
    private lateinit var buttonGoogle: MaterialButton
    private lateinit var buttonGithub: MaterialButton
    private lateinit var textViewCreateAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        tilEmail = findViewById(R.id.til_email)
        tilPassword = findViewById(R.id.til_password)
        editTextEmail = findViewById(R.id.editText_email)
        editTextPassword = findViewById(R.id.editText_password)
        buttonLogin = findViewById(R.id.button_login)
        buttonFacebook = findViewById(R.id.button_facebook)
        buttonGoogle = findViewById(R.id.button_google)
        buttonGithub = findViewById(R.id.button_github)
        textViewCreateAccount = findViewById(R.id.textView_create_account)
    }

    private fun setupClickListeners() {
        buttonLogin.setOnClickListener {
            if (validateInputs()) {
                handleLogin()
            }
        }

        buttonFacebook.setOnClickListener {
            showSocialLoginDialog()
        }

        buttonGoogle.setOnClickListener {
            showSocialLoginDialog()
        }

        buttonGithub.setOnClickListener {
            showSocialLoginDialog()
        }

        textViewCreateAccount.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Clear previous errors
        tilEmail.error = null
        tilPassword.error = null

        // Validate Email
        val email = editTextEmail.text.toString().trim()
        if (email.isEmpty()) {
            tilEmail.error = getString(R.string.error_empty_email)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        // Validate Password
        val password = editTextPassword.text.toString()
        if (password.isEmpty()) {
            tilPassword.error = getString(R.string.error_empty_password)
            isValid = false
        }

        return isValid
    }

    private fun handleLogin() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString()

        // Disable button and show loading
        buttonLogin.isEnabled = false
        buttonLogin.text = "Signing In..."

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    showSuccessDialog()
                } else {
                    // Login failed
                    buttonLogin.isEnabled = true
                    buttonLogin.text = getString(R.string.login_button_text)
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.login_success_title))
            .setMessage(getString(R.string.login_success_message))
            .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                navigateToHome()
            }
            .setCancelable(false)
            .show()
    }

    private fun showSocialLoginDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.social_login_title))
            .setMessage(getString(R.string.social_login_message))
            .setPositiveButton(getString(R.string.dialog_ok), null)
            .show()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
