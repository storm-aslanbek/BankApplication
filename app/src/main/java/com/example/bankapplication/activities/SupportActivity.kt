package com.example.bankapplication.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bankapplication.RetrofitClient
import com.example.bankapplication.databinding.ActivitySupportBinding
import com.example.bankapplication.retrofit.MainAPI
import com.example.bankapplication.retrofit.SupportPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SupportActivity : AppCompatActivity() {
    private lateinit var mainApi: MainAPI
    private lateinit var binding: ActivitySupportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainApi = RetrofitClient.createService(MainAPI::class.java)

        binding.sendQuestion.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val resp = mainApi.support(
                    SupportPost(
                        binding.userQuestion.text.toString(),
                    )
                )

                withContext(Dispatchers.Main) {
                    if (resp.isSuccessful) {
                        resp.body()?.let { response ->
                            val ai_answer = response.response
                            binding.supportResponse.text = ai_answer.toString()
                        }
                    }
                }
            }
        }

        val homeButton = binding.homeButtonSup
        homeButton.setOnClickListener {
            val intent = Intent(this@SupportActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val messagesButton = binding.messagesButtonSupport
        messagesButton.setOnClickListener {
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }
    }
}
