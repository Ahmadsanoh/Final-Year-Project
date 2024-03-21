package com.example.myfyp2

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class QuickCalculationActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var option1Button: Button
    private lateinit var option2Button: Button
    private lateinit var option3Button: Button
    private lateinit var option4Button: Button
    private lateinit var timerTextView: TextView

    private val numQuestions = 10
    private var currentQuestion = 0
    private var score = 0
    private lateinit var questions: MutableList<Pair<String, Int>>
    private lateinit var currentPair: Pair<String, Int>
    private lateinit var timer: CountDownTimer
    private val totalQuizTime: Long = 30000  // 30 seconds for the whole quiz
    private var reactionTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_calculation)

        val title = SpannableString("Quick Calculation Test")
        title.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        supportActionBar?.title = title

        questionTextView = findViewById(R.id.questionTextView)
        timerTextView = findViewById(R.id.timerTextView)
        option1Button = findViewById(R.id.option1Button)
        option2Button = findViewById(R.id.option2Button)
        option3Button = findViewById(R.id.option3Button)
        option4Button = findViewById(R.id.option4Button)

        initializeQuestions()

        setNewQuestion()

        option1Button.setOnClickListener { checkAnswer(option1Button.text.toString().toInt()) }
        option2Button.setOnClickListener { checkAnswer(option2Button.text.toString().toInt()) }
        option3Button.setOnClickListener { checkAnswer(option3Button.text.toString().toInt()) }
        option4Button.setOnClickListener { checkAnswer(option4Button.text.toString().toInt()) }
    }

    private fun initializeQuestions() {
        val operators = listOf("+", "-", "*", "/")

        questions = mutableListOf()

        repeat(numQuestions) {
            val operand1 = Random.nextInt(0, 10)
            val operand2 = Random.nextInt(0, 10)
            val operator = operators.random()

            val question = "What is $operand1 $operator $operand2?"
            val answer = calculateAnswer(operand1, operand2, operator)

            questions.add(question to answer)
        }
    }

    private fun calculateAnswer(operand1: Int, operand2: Int, operator: String): Int {
        return when (operator) {
            "+" -> operand1 + operand2
            "-" -> operand1 - operand2
            "*" -> operand1 * operand2
            "/" -> operand1 / operand2
            else -> throw IllegalArgumentException("Invalid operator")
        }
    }

    private fun setNewQuestion() {
        if (currentQuestion < numQuestions) {
            currentPair = questions[currentQuestion]
            val currentQuestionText = currentPair.first
            val options = generateOptions(currentPair.second)

            questionTextView.text = currentQuestionText
            setOptionTexts(options)

            startTimer()
        } else {
            // Quiz finished, display score or perform other actions
            val reactionTimeInMilliseconds = reactionTime
            questionTextView.text = "Quiz finished \nScore: $score / $numQuestions\nReaction Time: ${reactionTimeInMilliseconds}ms"
            disableOptions()
            timer.cancel() // Reset the timer when the quiz finishes
            timerTextView.text = ""
        }
    }

    private fun generateOptions(correctAnswer: Int): List<Int> {
        val options = mutableListOf(correctAnswer)

        repeat(3) {
            var wrongAnswer = Random.nextInt(0, 100)
            while (options.contains(wrongAnswer)) {
                wrongAnswer = Random.nextInt(0, 100)
            }
            options.add(wrongAnswer)
        }
        return options.shuffled()
    }

    private fun setOptionTexts(options: List<Int>) {
        option1Button.text = options[0].toString()
        option2Button.text = options[1].toString()
        option3Button.text = options[2].toString()
        option4Button.text = options[3].toString()
    }

    private fun startTimer() {
        reactionTime = System.currentTimeMillis() // Store the current time when the question starts
        timer = object : CountDownTimer(totalQuizTime, 2000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 2000
                timerTextView.text = "Time Left: $secondsRemaining seconds"
            }

            override fun onFinish() {
                currentQuestion++
                setNewQuestion()
            }
        }.start()
    }

    private fun checkAnswer(selectedAnswer: Int) {
        val correctAnswer = currentPair.second

        val currentTime = System.currentTimeMillis() // Get the current time when the answer is selected
        reactionTime = currentTime - reactionTime // Calculate the reaction time

        if (selectedAnswer == correctAnswer) {
            score++
        }

        currentQuestion++
        setNewQuestion()
    }

    private fun disableOptions() {
        option1Button.isEnabled = false
        option2Button.isEnabled = false
        option3Button.isEnabled = false
        option4Button.isEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel() // Cancel the timer on activity destroy to prevent memory leaks
    }
}
