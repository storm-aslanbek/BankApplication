package com.example.bankapplication.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bankapplication.R
import com.example.bankapplication.RetrofitClient
import com.example.bankapplication.databinding.ActivityAuthBinding
import com.example.bankapplication.retrofit.AuthPost
import com.example.bankapplication.retrofit.AuthResponse
import com.example.bankapplication.retrofit.MainAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    private lateinit var mainApi: MainAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
//        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainApi = RetrofitClient.createService(MainAPI::class.java)

// Метод для хранения состояния. Нужен для хранения состояния входа в аккаунт
        fun saveUserData(authResponse: AuthResponse) {
            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("phone_number", authResponse.phone_number)
            editor.putString("password", authResponse.password)
            editor.putString("last_name", authResponse.last_name)
            editor.putString("first_name", authResponse.first_name)
            editor.apply()
        }

// Обмен данных сервером
        binding.authButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val response = mainApi.auth(
                    AuthPost(
                        binding.phoneAuthInput.text.toString(),
                        binding.passwordAuthInput.text.toString()
                    )
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { authResponse ->
                            saveUserData(authResponse) // Сохраняем данные пользователя
                            Toast.makeText(this@AuthActivity, "Жүйеге кірдіңіз!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@AuthActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@AuthActivity, "Жүйеге кіре алмадыңыз!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }




// Ссылки на страницы
        val linkToReg: TextView = findViewById(R.id.linkRegText)
        linkToReg.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }

        val homeButton: LinearLayout = findViewById(R.id.homeButtonAuth)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val messages_but: LinearLayout = findViewById(R.id.messages_button_auth)
        messages_but.setOnClickListener {
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }
    }
}