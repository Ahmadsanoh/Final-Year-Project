package com.example.myfyp2

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LowToHighActivity : AppCompatActivity() {
    private lateinit var buttons: List<Button>
    private val random = java.util.Random()
    private var hiddenNumbers = mutableListOf<Int>()
    private var currentButtonPressCount = 0
    private var repeatCount = 0
    private val maxRepeat = 9 // Number of times to repeat the activity
    private var totalCorrectPresses = 0
    private var totalWrongPresses = 0
    private var pressedButtonNumbers = mutableListOf<Int>()
    private var startTime: Long = 0 // Store the start time of button press

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_low_to_high)

        val title = SpannableString("Low to High Test")
        title.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        supportActionBar?.title = title

        buttons = listOf(
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5)
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                handleButtonClick(button)
            }
        }

        runActivity()
    }

    private fun runActivity() {
        buttons.forEach { button ->
            button.text = "" // Clear existing button texts
        }
        hiddenNumbers.clear()
        currentButtonPressCount = 0

        if (repeatCount > 0) {
            // Delay before displaying random numbers for the second and third round
            Handler().postDelayed({
                displayRandomNumbersOnButtons()
                startTime = System.currentTimeMillis() // Store start time for the current round
            }, 1000) // Delay of 1 seconds
        } else {
            // For the first round, display immediately
            displayRandomNumbersOnButtons()
            startTime = System.currentTimeMillis() // Store start time for the first round
        }
    }

    private fun displayRandomNumbersOnButtons() {
        val usedNumbers = mutableSetOf<Int>() // To track used numbers

        buttons.forEach { button ->
            var randomNumber = random.nextInt(10) // Generates numbers from 0 to 10
            while (usedNumbers.contains(randomNumber)) {
                randomNumber = random.nextInt(10)
            }
            usedNumbers.add(randomNumber)
            button.text = randomNumber.toString()

            // Hide the number after 3 seconds
            Handler().postDelayed({
                button.text = "" // Clear the text to hide the number
                hiddenNumbers.add(randomNumber)
            }, 1000)
        }
    }

    private fun handleButtonClick(button: Button) {
        val index = buttons.indexOf(button)
        val hiddenNumber = hiddenNumbers[index]

        if (button.text.isEmpty()) {
            button.text = hiddenNumber.toString()
            currentButtonPressCount++
            pressedButtonNumbers.add(hiddenNumber)

            if (currentButtonPressCount == buttons.size) {
                val reactionTime = System.currentTimeMillis() - startTime // Calculate reaction time

                // Checking correctness after all buttons pressed
                val isCorrect = isButtonSequenceCorrect()
                if (isCorrect) {
                    totalCorrectPresses++
                } else {
                    totalWrongPresses++
                }

                currentButtonPressCount = 0
                pressedButtonNumbers.clear()

                if (repeatCount < maxRepeat) {
                    repeatCount++
                    runActivity()
                } else {
                    val scoreTextView = findViewById<TextView>(R.id.scoreTextView)
                    scoreTextView.text = "Your score is: \nCorrect pressed = $totalCorrectPresses\nIncorrect pressed = $totalWrongPresses\nReaction Time: $reactionTime ms"
                }
            } else {
                // Store the time when the first button is pressed
                if (currentButtonPressCount == 1) {
                    startTime = System.currentTimeMillis()
                }
            }
        } else {
            showToast("Number on Button ${index + 1}: ${button.text}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isButtonSequenceCorrect(): Boolean {
        return pressedButtonNumbers == pressedButtonNumbers.sorted()
    }
}
