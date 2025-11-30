package com.example.biosync.utils

/**
 * Utility class for health-related calculations
 * Uses standard formulas for BMI, BMR, and water intake
 */
object HealthCalculator {

    // Activity level multipliers for TDEE calculation
    private val ACTIVITY_MULTIPLIERS = mapOf(
        "Sedentary" to 1.2,
        "Light" to 1.375,
        "Moderate" to 1.55,
        "Active" to 1.725
    )

    /**
     * Calculate BMI (Body Mass Index)
     * Formula: weight(kg) / (height(m))^2
     *
     * @param weightKg Weight in kilograms
     * @param heightCm Height in centimeters
     * @return BMI value rounded to 1 decimal place
     */
    fun calculateBMI(weightKg: Double, heightCm: Double): Double {
        if (weightKg <= 0 || heightCm <= 0) return 0.0
        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)
        return Math.round(bmi * 10.0) / 10.0
    }

    /**
     * Get BMI category based on BMI value
     *
     * @param bmi BMI value
     * @return Category string (Underweight, Normal, Overweight, Obese)
     */
    fun getBMICategory(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal"
            bmi < 30.0 -> "Overweight"
            else -> "Obese"
        }
    }

    /**
     * Calculate daily water goal
     * Formula: weight(kg) * 35ml
     *
     * @param weightKg Weight in kilograms
     * @return Daily water goal in milliliters
     */
    fun calculateWaterGoal(weightKg: Double): Int {
        if (weightKg <= 0) return 0
        return (weightKg * 35).toInt()
    }

    /**
     * Calculate BMR (Basal Metabolic Rate) using Mifflin-St Jeor Equation
     *
     * Men: (10 × weight) + (6.25 × height) − (5 × age) + 5
     * Women: (10 × weight) + (6.25 × height) − (5 × age) − 161
     *
     * @param weightKg Weight in kilograms
     * @param heightCm Height in centimeters
     * @param age Age in years
     * @param gender "Male" or "Female"
     * @return BMR in calories
     */
    fun calculateBMR(weightKg: Double, heightCm: Double, age: Int, gender: String): Double {
        if (weightKg <= 0 || heightCm <= 0 || age <= 0) return 0.0

        val baseBMR = (10 * weightKg) + (6.25 * heightCm) - (5 * age)

        return when (gender.lowercase()) {
            "male" -> baseBMR + 5
            "female" -> baseBMR - 161
            else -> baseBMR // Default to male formula without adjustment
        }
    }

    /**
     * Calculate daily calorie goal (TDEE - Total Daily Energy Expenditure)
     * Formula: BMR × Activity Multiplier
     *
     * @param bmr Basal Metabolic Rate
     * @param activityLevel "Sedentary", "Light", "Moderate", or "Active"
     * @return Daily calorie goal
     */
    fun calculateCalorieGoal(bmr: Double, activityLevel: String): Int {
        val multiplier = ACTIVITY_MULTIPLIERS[activityLevel] ?: 1.2
        return (bmr * multiplier).toInt()
    }

    /**
     * Calculate daily calorie goal (TDEE - Total Daily Energy Expenditure)
     * Formula: BMR × Activity Multiplier
     *
     * @param weightKg Weight in kilograms
     * @param heightCm Height in centimeters
     * @param age Age in years
     * @param gender "Male" or "Female"
     * @param activityLevel "Sedentary", "Light", "Moderate", or "Active"
     * @return Daily calorie goal
     */
    fun calculateCalorieGoalFull(
        weightKg: Double,
        heightCm: Double,
        age: Int,
        gender: String,
        activityLevel: String
    ): Int {
        val bmr = calculateBMR(weightKg, heightCm, age, gender)
        return calculateCalorieGoal(bmr, activityLevel)
    }

    /**
     * Validate age input
     *
     * @param age Age to validate
     * @return true if valid (1-120), false otherwise
     */
    fun isValidAge(age: Int): Boolean {
        return age in 1..120
    }

    /**
     * Validate weight input
     *
     * @param weight Weight to validate
     * @return true if valid (20-500 kg), false otherwise
     */
    fun isValidWeight(weight: Double): Boolean {
        return weight in 20.0..500.0
    }

    /**
     * Validate height input
     *
     * @param height Height to validate
     * @return true if valid (50-300 cm), false otherwise
     */
    fun isValidHeight(height: Double): Boolean {
        return height in 50.0..300.0
    }
}
