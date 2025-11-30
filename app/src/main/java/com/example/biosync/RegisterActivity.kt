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
import com.example.biosync.models.User
import com.example.biosync.utils.HealthCalculator
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Text Input Layouts for error display
    private lateinit var tilFullname: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var tilAge: TextInputLayout
    private lateinit var tilWeight: TextInputLayout
    private lateinit var tilHeight: TextInputLayout

    // Edit Texts
    private lateinit var editTextFullname: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var editTextConfirmPassword: TextInputEditText
    private lateinit var editTextAge: TextInputEditText
    private lateinit var editTextWeight: TextInputEditText
    private lateinit var editTextHeight: TextInputEditText

    // Chip Groups
    private lateinit var chipGroupGender: ChipGroup
    private lateinit var chipGroupActivity: ChipGroup

    // Buttons
    private lateinit var buttonRegister: MaterialButton
    private lateinit var textViewLoginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        // Text Input Layouts
        tilFullname = findViewById(R.id.til_fullname)
        tilEmail = findViewById(R.id.til_email)
        tilPassword = findViewById(R.id.til_password)
        tilConfirmPassword = findViewById(R.id.til_confirm_password)
        tilAge = findViewById(R.id.til_age)
        tilWeight = findViewById(R.id.til_weight)
        tilHeight = findViewById(R.id.til_height)

        // Edit Texts
        editTextFullname = findViewById(R.id.editText_fullname)
        editTextEmail = findViewById(R.id.editText_email)
        editTextPassword = findViewById(R.id.editText_password)
        editTextConfirmPassword = findViewById(R.id.editText_confirm_password)
        editTextAge = findViewById(R.id.editText_age)
        editTextWeight = findViewById(R.id.editText_weight)
        editTextHeight = findViewById(R.id.editText_height)

        // Chip Groups
        chipGroupGender = findViewById(R.id.chipGroup_gender)
        chipGroupActivity = findViewById(R.id.chipGroup_activity)

        // Buttons
        buttonRegister = findViewById(R.id.button_register)
        textViewLoginLink = findViewById(R.id.textView_login_link)
    }

    private fun setupClickListeners() {
        buttonRegister.setOnClickListener {
            if (validateInputs()) {
                handleRegister()
            }
        }

        textViewLoginLink.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Clear previous errors
        tilFullname.error = null
        tilEmail.error = null
        tilPassword.error = null
        tilConfirmPassword.error = null
        tilAge.error = null
        tilWeight.error = null
        tilHeight.error = null

        // Validate Full Name
        val fullname = editTextFullname.text.toString().trim()
        if (fullname.isEmpty()) {
            tilFullname.error = getString(R.string.error_empty_fullname)
            isValid = false
        }

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
        } else if (password.length < 6) {
            tilPassword.error = getString(R.string.error_password_too_short)
            isValid = false
        }

        // Validate Confirm Password
        val confirmPassword = editTextConfirmPassword.text.toString()
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = getString(R.string.error_empty_confirm_password)
            isValid = false
        } else if (password != confirmPassword) {
            tilConfirmPassword.error = getString(R.string.error_password_mismatch)
            isValid = false
        }

        // Validate Age
        val ageText = editTextAge.text.toString()
        if (ageText.isEmpty()) {
            tilAge.error = getString(R.string.error_empty_age)
            isValid = false
        } else {
            val age = ageText.toIntOrNull()
            if (age == null || !HealthCalculator.isValidAge(age)) {
                tilAge.error = getString(R.string.error_invalid_age)
                isValid = false
            }
        }

        // Validate Weight
        val weightText = editTextWeight.text.toString()
        if (weightText.isEmpty()) {
            tilWeight.error = getString(R.string.error_empty_weight)
            isValid = false
        } else {
            val weight = weightText.toDoubleOrNull()
            if (weight == null || !HealthCalculator.isValidWeight(weight)) {
                tilWeight.error = getString(R.string.error_invalid_weight)
                isValid = false
            }
        }

        // Validate Height
        val heightText = editTextHeight.text.toString()
        if (heightText.isEmpty()) {
            tilHeight.error = getString(R.string.error_empty_height)
            isValid = false
        } else {
            val height = heightText.toDoubleOrNull()
            if (height == null || !HealthCalculator.isValidHeight(height)) {
                tilHeight.error = getString(R.string.error_invalid_height)
                isValid = false
            }
        }

        // Validate Gender
        if (chipGroupGender.checkedChipId == -1) {
            Toast.makeText(this, getString(R.string.error_select_gender), Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validate Activity Level
        if (chipGroupActivity.checkedChipId == -1) {
            Toast.makeText(this, getString(R.string.error_select_activity), Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun handleRegister() {
        // Get all input values
        val fullname = editTextFullname.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString()
        val age = editTextAge.text.toString().toInt()
        val weight = editTextWeight.text.toString().toDouble()
        val height = editTextHeight.text.toString().toDouble()

        // Get gender from chip group
        val gender = when (chipGroupGender.checkedChipId) {
            R.id.chip_male -> "male"
            R.id.chip_female -> "female"
            else -> "male"
        }

        // Get activity level from chip group
        val activityLevel = when (chipGroupActivity.checkedChipId) {
            R.id.chip_sedentary -> "sedentary"
            R.id.chip_light -> "light"
            R.id.chip_moderate -> "moderate"
            R.id.chip_active -> "active"
            else -> "sedentary"
        }

        // Disable button and show loading
        buttonRegister.isEnabled = false
        buttonRegister.text = "Creating Account..."

        // Create user with Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        // Calculate health metrics
                        val bmi = HealthCalculator.calculateBMI(weight, height)
                        val bmr = HealthCalculator.calculateBMR(weight, height, age, gender)
                        val waterGoal = HealthCalculator.calculateWaterGoal(weight)
                        val calorieGoal = HealthCalculator.calculateCalorieGoal(bmr, activityLevel)

                        // Create User object
                        val user = User(
                            uid = firebaseUser.uid,
                            fullName = fullname,
                            email = email,
                            age = age,
                            weight = weight,
                            height = height,
                            gender = gender,
                            activityLevel = activityLevel,
                            dailyWaterGoal = waterGoal,
                            dailyCalorieGoal = calorieGoal,
                            bmi = bmi,
                            createdAt = System.currentTimeMillis()
                        )

                        // Save to Firestore
                        saveUserToFirestore(user)
                    }
                } else {
                    // Registration failed
                    buttonRegister.isEnabled = true
                    buttonRegister.text = getString(R.string.register_button_text)
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveUserToFirestore(user: User) {
        db.collection("users").document(user.uid)
            .set(user.toMap())
            .addOnSuccessListener {
                showSuccessDialog()
            }
            .addOnFailureListener { e ->
                buttonRegister.isEnabled = true
                buttonRegister.text = getString(R.string.register_button_text)
                Toast.makeText(this, "Error saving profile: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.register_success_title))
            .setMessage(getString(R.string.register_success_message))
            .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                navigateToHome()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
