package com.example.bankapplication.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bankapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

//        Получение запроса из SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val phone_number = sharedPreferences.getString("phone_number", null)
        val surname = sharedPreferences.getString("last_name", null)
        val name = sharedPreferences.getString("first_name", null)
        val fullname = "$surname $name"

// Загрузка имени пользователя сохраненный после сеанса
        if (phone_number == null) {
            Toast.makeText(
                this@MainActivity, "Сіз жүйеге кірмегенсіз!", Toast.LENGTH_LONG
            ).show()
        } else {
            binding.profile.text = fullname
        }

        val myBank = binding.myBank
        myBank.setOnClickListener {
            val intent = Intent(this, MyBankActivity::class.java)
            startActivity(intent)
        }

        val transactions = binding.transactionsMain
        transactions.setOnClickListener {
            val intent = Intent(this, TransactionsActivity::class.java)
            startActivity(intent)
        }

//        Ссылки профиля, авторизации
        val linkToAuth = binding.profile
        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        val imageToAuth = binding.profilePhoto
        imageToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

// Выход из аккаунта, удаляем данные из sharedPreferences
        val exit = binding.exitButton
        exit.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.remove("phone_number")
            editor.remove("last_name")
            editor.apply()
            finishAffinity()
        }

        val support = binding.supportButton
        support.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

        val faq = binding.faqButton
        faq.setOnClickListener {
            val intent = Intent(this, FaqActivity::class.java)
            startActivity(intent)
        }

        val messages_but = binding.messagesButtonMain
        messages_but.setOnClickListener {
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }
    }
}
