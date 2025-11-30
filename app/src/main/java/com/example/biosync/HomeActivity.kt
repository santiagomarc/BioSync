package com.example.biosync

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.biosync.models.User
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Views
    private lateinit var toolbar: MaterialToolbar
    private lateinit var textViewUserName: TextView
    private lateinit var textViewDate: TextView
    private lateinit var textViewCalorieValue: TextView
    private lateinit var textViewActivityLevel: TextView
    private lateinit var textViewBmiValue: TextView
    private lateinit var textViewBmiCategory: TextView
    private lateinit var textViewWeightValue: TextView
    private lateinit var textViewHeightValue: TextView
    private lateinit var textViewWaterProgress: TextView
    private lateinit var progressIndicatorWater: LinearProgressIndicator
    private lateinit var textViewWaterPercentage: TextView
    private lateinit var buttonAddWaterSmall: MaterialButton
    private lateinit var buttonAddWaterLarge: MaterialButton

    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appBarLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()
        setupToolbar()
        setupClickListeners()
        loadUserData()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        textViewUserName = findViewById(R.id.textView_user_name)
        textViewDate = findViewById(R.id.textView_date)
        textViewCalorieValue = findViewById(R.id.textView_calorie_value)
        textViewActivityLevel = findViewById(R.id.textView_activity_level)
        textViewBmiValue = findViewById(R.id.textView_bmi_value)
        textViewBmiCategory = findViewById(R.id.textView_bmi_category)
        textViewWeightValue = findViewById(R.id.textView_weight_value)
        textViewHeightValue = findViewById(R.id.textView_height_value)
        textViewWaterProgress = findViewById(R.id.textView_water_progress_text)
        progressIndicatorWater = findViewById(R.id.progressIndicator_water)
        textViewWaterPercentage = findViewById(R.id.textView_water_percentage)
        buttonAddWaterSmall = findViewById(R.id.button_add_water_small)
        buttonAddWaterLarge = findViewById(R.id.button_add_water_large)

        // Set current date
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        textViewDate.text = dateFormat.format(Date())
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                loadUserData()
                true
            }
            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupClickListeners() {
        buttonAddWaterSmall.setOnClickListener {
            addWater(250)
        }

        buttonAddWaterLarge.setOnClickListener {
            addWater(500)
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error loading data: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    currentUser = snapshot.toObject(User::class.java)
                    currentUser?.let { user ->
                        updateUI(user)
                    }
                }
            }
    }

    private fun updateUI(user: User) {
        // Update user name
        val firstName = user.fullName.split(" ").firstOrNull() ?: user.fullName
        textViewUserName.text = firstName

        // Update Daily Calorie Goal (display recommendation only)
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        textViewCalorieValue.text = numberFormat.format(user.dailyCalorieGoal)

        // Update Activity Level badge
        val activityEmoji = when (user.activityLevel.lowercase()) {
            "sedentary" -> "🛋️"
            "lightly active" -> "🚶"
            "moderately active" -> "🔥"
            "very active" -> "🏃"
            "extremely active" -> "💪"
            else -> "🔥"
        }
        textViewActivityLevel.text = "$activityEmoji ${user.activityLevel}"

        // Update BMI
        textViewBmiValue.text = String.format("%.1f", user.bmi)
        textViewBmiCategory.text = user.getBmiCategory()
        
        // Set BMI category color
        val bmiColor = when {
            user.bmi < 18.5 -> getColor(R.color.warning)
            user.bmi < 25 -> getColor(R.color.bmi_green)
            user.bmi < 30 -> getColor(R.color.calorie_orange)
            else -> getColor(R.color.error)
        }
        textViewBmiCategory.setTextColor(bmiColor)

        // Update Weight and Height
        textViewWeightValue.text = user.weight.toInt().toString()
        textViewHeightValue.text = user.height.toInt().toString()

        // Update Water
        textViewWaterProgress.text = "${numberFormat.format(user.currentWaterIntake)} / ${numberFormat.format(user.dailyWaterGoal)} ml"
        val waterProgress = user.getWaterProgress().toInt().coerceIn(0, 100)
        progressIndicatorWater.progress = waterProgress
        textViewWaterPercentage.text = "${waterProgress}%"
    }

    private fun addWater(amount: Int) {
        val userId = auth.currentUser?.uid ?: return
        val newIntake = (currentUser?.currentWaterIntake ?: 0) + amount

        db.collection("users").document(userId)
            .update("currentWaterIntake", newIntake)
            .addOnSuccessListener {
                Toast.makeText(this, "+${amount}ml added! 💧", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
