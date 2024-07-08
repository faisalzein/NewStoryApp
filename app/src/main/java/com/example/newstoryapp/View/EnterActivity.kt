package com.example.newstoryapp.View

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.newstoryapp.Api.APISettings
import com.example.newstoryapp.R
import com.example.newstoryapp.databinding.ActivityEnterBinding
import com.example.newstoryapp.response.Responenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EnterActivity : AppCompatActivity() {


    private lateinit var binding: ActivityEnterBinding
    private lateinit var preferences: SharedPreferences


    companion object {
        const val PREFS_NAME = "user_pref"
        const val NAME = "name"
        const val ID = "userId"
        const val TOKEN = "token"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        checkLogin()
        playAnimation()
        getApiLogin()
    }


    private fun checkLogin() {
        val token = preferences.getString(TOKEN, null)
        if (token != null) {
            val intent = Intent(this@EnterActivity, ListStoryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveLoginSession(userId: String, name: String, token: String) {
        val editor = preferences.edit()
        editor.putString(NAME, name)
        editor.putString(ID, userId)
        editor.putString(TOKEN, token)
        editor.apply()
    }


    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()


        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val pleaseinsert = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val surel = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val katasandi = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val edtPass = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val btnLog = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val btnSign = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(title, pleaseinsert, surel, edtEmail, katasandi, edtPass, btnLog, btnSign)
            start()
        }
    }


    private fun getApiLogin() {
        binding.btnSignup.setOnClickListener {
            val intent = Intent(this@EnterActivity, MainActivity::class.java)
            startActivity(intent)
        }
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditText.error = "Masukkan Email"
                }
                password.isEmpty() -> {
                    binding.passwordEditText.error = "Masukkan Password"
                }
                else -> {
                    showLoading(true)
                    val client = APISettings.getApiService().getLogin(email, password)
                    client.enqueue(object : Callback<Responenter> {
                        override fun onResponse(
                            call: Call<Responenter>,
                            response: Response<Responenter>
                        ) {
                            showLoading(false)
                            if (response.isSuccessful) {
                                response.body()?.loginResult?.apply {
                                    saveLoginSession(userId, name, token) // Menyimpan data login
                                    AlertDialog.Builder(this@EnterActivity).apply {
                                        setTitle("Yeah!")
                                        setMessage(resources.getString(R.string.message_login))
                                        setPositiveButton(resources.getString(R.string.message_btn)) { _, _ ->
                                            val intent = Intent(
                                                this@EnterActivity,
                                                ListStoryActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        }
                                        create()
                                        show()
                                    }
                                }
                            } else {
                                showLoading(false) // Menyembunyikan loading
                                Toast.makeText(
                                    this@EnterActivity,
                                    resources.getString(R.string.toast_not),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<Responenter>, t: Throwable) {
                            Toast.makeText(
                                this@EnterActivity,
                                resources.getString(R.string.toast_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
