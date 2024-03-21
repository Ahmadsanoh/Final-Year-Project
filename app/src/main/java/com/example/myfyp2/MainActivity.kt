package com.example.myfyp2

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val title = SpannableString("BrainCoach Mobile App")
        title.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        supportActionBar?.title = title

        val btnQuickCalculationTest: Button = findViewById(R.id.btnQuickCalculationTest)
        val btnLowToHighTest: Button = findViewById(R.id.btnLowToHighTest)
        val btnTriangleMathTest: Button = findViewById(R.id.btnTriangleMathTest)

        btnQuickCalculationTest.setOnClickListener {
            showToast("Quick Calculation Test Clicked")
            val intent = Intent(this, QuickCalculationActivity::class.java)
            startActivity(intent)
        }

        btnLowToHighTest.setOnClickListener {
            showToast("Low to High Test Clicked")
            val intent = Intent(this, LowToHighActivity::class.java)
            startActivity(intent)
        }

        btnTriangleMathTest.setOnClickListener {
            showToast("Triangle Math Test Clicked")
            val intent = Intent(this, TriangleMathActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
