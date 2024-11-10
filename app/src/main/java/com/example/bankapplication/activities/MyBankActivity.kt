package com.example.bankapplication.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bankapplication.RetrofitClient
import com.example.bankapplication.databinding.ActivityMyBankBinding
import com.example.bankapplication.retrofit.AuthPost
import com.example.bankapplication.retrofit.MainAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyBankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyBankBinding
    private lateinit var mainApi: MainAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBankBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val phoneNumber = sharedPreferences.getString("phone_number", null)
        val password = sharedPreferences.getString("password", null)
        val surname = sharedPreferences.getString("last_name", null)
        val name = sharedPreferences.getString("first_name", null)
        val fullname = "$surname $name"

        mainApi = RetrofitClient.createService(MainAPI::class.java)

        if (phoneNumber == null) {
            Toast.makeText(
                this@MyBankActivity, "Сіз жүйеге кірмегенсіз!", Toast.LENGTH_LONG
            ).show()
        } else {
//            Загрузка данных пользователя из shared preferences
            binding.profile.text = fullname
            binding.phoneNumberText.text = phoneNumber
//            Получение информации о балансе из сервера
            CoroutineScope(Dispatchers.IO).launch {
                val response = mainApi.auth(
                    AuthPost(
                        phoneNumber.toString(),
                        password.toString()
                    )
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { authResponse ->
                            Toast.makeText(
                                this@MyBankActivity,
                                "Жүйеге кірдіңіз!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val realBalance = authResponse.balance
                            binding.balanceText.text = "$realBalance₸"
                        }
                    } else {
                        Toast.makeText(
                            this@MyBankActivity,
                            "Жүйеге кіре алмадыңыз!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        //        Ссылки профиля
        binding.homeButtonBank.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.transactionsMyBank.setOnClickListener {
            startActivity(Intent(this, TransactionsActivity::class.java))
        }

        binding.profile.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        binding.profilePhoto.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        binding.messagesButtonMybank.setOnClickListener {
            startActivity(Intent(this, MessagesActivity::class.java))
        }

        binding.exitButton.setOnClickListener {
            sharedPreferences.edit().apply {
                remove("phone_number")
                remove("last_name")
                apply()
            }
            finishAffinity()
        }
    }
}
