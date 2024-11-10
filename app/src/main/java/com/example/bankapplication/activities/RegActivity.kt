package com.example.bankapplication.activities

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
import com.example.bankapplication.databinding.ActivityRegBinding
import com.example.bankapplication.retrofit.MainAPI
import com.example.bankapplication.retrofit.RegPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegBinding
    private lateinit var mainApi: MainAPI

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
//        setContentView(R.layout.activity_reg)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainApi = RetrofitClient.createService(MainAPI::class.java)

        val startBalance = 50000

//        Регистрация, отправляем данные в сервер
        binding.registerButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val user = mainApi.register(
                    RegPost(
                        binding.lastNameInput.text.toString(),
                        binding.firstNameInput.text.toString(),
                        binding.phoneInput.text.toString(),
                        binding.passwordInput.text.toString(),
                        startBalance
                    )
                )

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegActivity, "Жүйеге тіркелдіңіз! " +
                                "Деректерді қайта еңгңзіп, жүйеге кіріңіз", Toast.LENGTH_LONG
                    ).show()
                }
                val intent = Intent(this@RegActivity, AuthActivity::class.java)
                startActivity(intent)
//                runOnUiThread {
//                    binding.apply {
//                        statusText.text = user.last_name
//                    }
//                }
            }
        }


        val linkToAuth: TextView = binding.linkAuthText
        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        val homeButton: LinearLayout = binding.homeButtonReg
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val messages_but: LinearLayout = findViewById(R.id.messages_button_reg)
        messages_but.setOnClickListener {
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }
    }

}