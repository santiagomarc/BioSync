package com.example.biosync.models

/**
 * Data class representing a user in BioSync
 * This maps directly to the Firestore document structure
 */
data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val gender: String = "",
    val activityLevel: String = "",
    val dailyWaterGoal: Int = 0,
    val dailyCalorieGoal: Int = 0,
    val currentWaterIntake: Int = 0,
    val currentCalorieIntake: Int = 0,
    val bmi: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor required for Firestore
    constructor() : this(uid = "")

    /**
     * Get BMI category based on BMI value
     */
    fun getBmiCategory(): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal"
            bmi < 30.0 -> "Overweight"
            else -> "Obese"
        }
    }

    /**
     * Get water intake progress as percentage (0-100)
     */
    fun getWaterProgress(): Int {
        if (dailyWaterGoal == 0) return 0
        return ((currentWaterIntake.toFloat() / dailyWaterGoal) * 100).toInt().coerceIn(0, 100)
    }

    /**
     * Get calorie intake progress as percentage (0-100)
     */
    fun getCalorieProgress(): Int {
        if (dailyCalorieGoal == 0) return 0
        return ((currentCalorieIntake.toFloat() / dailyCalorieGoal) * 100).toInt().coerceIn(0, 100)
    }

    /**
     * Convert to Map for Firestore
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "age" to age,
            "weight" to weight,
            "height" to height,
            "gender" to gender,
            "activityLevel" to activityLevel,
            "dailyWaterGoal" to dailyWaterGoal,
            "dailyCalorieGoal" to dailyCalorieGoal,
            "currentWaterIntake" to currentWaterIntake,
            "currentCalorieIntake" to currentCalorieIntake,
            "bmi" to bmi,
            "createdAt" to createdAt
        )
    }
}
