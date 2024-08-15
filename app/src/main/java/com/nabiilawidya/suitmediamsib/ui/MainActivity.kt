package com.nabiilawidya.suitmediamsib.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.nabiilawidya.suitmediamsib.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        supportActionBar?.hide()

        binding.checkButton.setOnClickListener {
            val sentence = binding.palindromeInput.text.toString()
            val isPalindrome = isPalindrome(sentence)
            if (isPalindrome) {
                Toast.makeText(this, "Palindrome", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Not Palindrome", Toast.LENGTH_SHORT).show()
            }
        }

        binding.nextButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("user_name", name)
            startActivity(intent)
        }
    }

    private fun isPalindrome(sentence: String): Boolean {
        val palindrome = sentence.replace("\\s".toRegex(), "").lowercase()
        return palindrome == palindrome.reversed()
    }
}