package com.example.myfyp2

import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import java.util.*

class TriangleMathActivity : AppCompatActivity() {
    private var totalResult = 0
    private lateinit var resultTextView: TextView
    private val random = Random()
    private val firstRowNumbers = mutableListOf<Int>()
    private var isToggling = true // Flag to control toggling
    private lateinit var handler: android.os.Handler
    private lateinit var toggleRunnable: Runnable
    private var secondRowSum = 0
    private lateinit var answers: MutableList<Int> // Initializing the answers list
    private var startTime: Long = 0
    private var buttonPressCount = 0 // Counter for thirdRowButton presses

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_triangle_math)

        val title = SpannableString("Low to High Test")
        title.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        supportActionBar?.title = title

        startGame()
    }

    private fun startGame() {
        // Generate random numbers for the first row buttons
        for (i in 0 until 5) {
            val randomNumber = random.nextInt(9) + 1 // Random number between 1 and 5
            firstRowNumbers.add(randomNumber)
            val buttonId = "box${i + 1}"
            val button = findViewById<Button>(resources.getIdentifier(buttonId, "id", packageName))
            button.text = randomNumber.toString()
        }

        // Perform calculations for the second row buttons based on the first row numbers
        val firstRowFirstSum = firstRowNumbers[0] + firstRowNumbers[1]
        val firstRowSecondSum = firstRowNumbers[1] + firstRowNumbers[2]

        val secondRowFirstButton = findViewById<Button>(R.id.box4)
        val secondRowSecondButton = findViewById<Button>(R.id.box5)
        secondRowFirstButton.text = firstRowFirstSum.toString()
        secondRowSecondButton.text = firstRowSecondSum.toString()

        // Calculate the sum of second row numbers
        val thirdRowButton = findViewById<Button>(R.id.box6)
        secondRowSum = firstRowFirstSum + firstRowSecondSum

        // Generate random answers for the third row button
        answers = mutableListOf<Int>()
        answers.add(secondRowSum)
        repeat(10) {
            val randomOffset = random.nextInt(10) + 1 // Random value between 1 and 5
            val wrongAnswer = secondRowSum + randomOffset
            answers.add(wrongAnswer)
        }
        answers.shuffle()

        // Toggle the display of the third row button text
        handler = android.os.Handler()
        toggleRunnable = object : Runnable {
            var currentIndex = 0
            override fun run() {
                if (isToggling) {
                    thirdRowButton.text = answers[currentIndex].toString()
                    currentIndex = (currentIndex + 1) % answers.size
                    startTime = SystemClock.elapsedRealtime() // Start measuring time
                    handler.postDelayed(this, 600) // Toggle every 0.6 second (600 milliseconds)
                }
            }
        }
        handler.post(toggleRunnable)

        // Set click listener for the third row button to verify user input and measure reaction time
        thirdRowButton.setOnClickListener {
            val buttonText = thirdRowButton.text.toString()
            val reactionTime = SystemClock.elapsedRealtime() - startTime
            buttonPressCount++

            // Update the TextView to display the button press count

            if (buttonText.toIntOrNull() == secondRowSum) {
                resultTextView = findViewById(R.id.resultTextView)
                resultTextView.textSize = 18f // Set the text size here (you can adjust the value as needed)
                resultTextView.text = "Correct Answer is: $secondRowSum\nReaction time: ${reactionTime}ms\nButton Press Count: $buttonPressCount"
                totalResult++
                isToggling = false // Stop toggling/shuffling
                handler.removeCallbacks(toggleRunnable) // Stop the toggling
            } else {
                val wrongPressCount = buttonPressCount - totalResult // Calculate wrong presses
                resultTextView = findViewById(R.id.resultTextView)
                resultTextView.textSize = 18f // Set the text size here (you can adjust the value as needed)
                resultTextView.text = "\nWrong Press Count: $wrongPressCount"
                showToast("Wrong!")

                // Don't stop toggling/shuffling on incorrect answer
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
