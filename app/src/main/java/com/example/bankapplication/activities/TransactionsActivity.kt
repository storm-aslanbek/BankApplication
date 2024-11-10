package com.example.bankapplication.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bankapplication.RetrofitClient
import com.example.bankapplication.databinding.ActivityTransactionsBinding
import com.example.bankapplication.retrofit.AuthPost
import com.example.bankapplication.retrofit.MainAPI
import com.example.bankapplication.retrofit.SearchPost
import com.example.bankapplication.retrofit.TransferPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionsActivity : AppCompatActivity() {
    private lateinit var mainApi: MainAPI
    private lateinit var binding: ActivityTransactionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainApi = RetrofitClient.createService(MainAPI::class.java)

        // Информация о профиле
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val phone_number = sharedPreferences.getString("phone_number", null)
        val password = sharedPreferences.getString("password", null)
        val surname = sharedPreferences.getString("last_name", null)
        val name = sharedPreferences.getString("first_name", null)
        val fullname = surname + " " + name

        if (phone_number == null) {
            Toast.makeText(this@TransactionsActivity, "Сіз жүйеге кірмегенсіз!", Toast.LENGTH_LONG).show()
        } else {
            // Получение информации о балансе
            CoroutineScope(Dispatchers.IO).launch {
                val response = mainApi.auth(
                    AuthPost(phone_number.toString(), password.toString())
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { authResponse ->
                            Toast.makeText(this@TransactionsActivity, "Жүйеге кірдіңіз!", Toast.LENGTH_SHORT).show()
                            val real_balance = authResponse.balance
                            binding.currentBalance.text = "$real_balance ₸"
                        }
                    } else {
                        Toast.makeText(this@TransactionsActivity, "Жүйеге кіре алмадыңыз!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Проверка пользователя из базы при нажатии кнопки
            binding.checkUserButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val check = mainApi.user_search(
                        SearchPost(binding.reciverPhone.text.toString())
                    )

                    withContext(Dispatchers.Main) {
                        if (check.isSuccessful) {
                            check.body()?.let { searchResponse ->
                                binding.userStatus.text = "${searchResponse.last_name} ${searchResponse.first_name}"
                            }
                        } else {
                            binding.userStatus.text = "Қолданушы табылмады"
                        }
                    }
                }
            }

            // Перевод денег пользователю
            binding.confirmTransferButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val resp = mainApi.transfer(
                        TransferPost(
                            binding.reciverPhone.text.toString(),
                            binding.transferSumm.text.toString().toInt(),
                            phone_number.toString()
                        )
                    )

                    withContext(Dispatchers.Main) {
                        if (resp.isSuccessful) {
                            resp.body()?.let {
                                Toast.makeText(this@TransactionsActivity, "Транзакция орындалды!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@TransactionsActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else if (resp.code() == 400) {
                            Toast.makeText(this@TransactionsActivity, "Қаражатыңыз жеткіліксіз!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@TransactionsActivity, "Жүйеде тіркелмеген!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            val homeButton: LinearLayout = binding.homeButtonTransactions
            homeButton.setOnClickListener {
                val intent = Intent(this@TransactionsActivity, MainActivity::class.java)
                startActivity(intent)
            }

            val messagesButton: LinearLayout = binding.messagesButtonTransactions
            messagesButton.setOnClickListener {
                val intent = Intent(this, MessagesActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
