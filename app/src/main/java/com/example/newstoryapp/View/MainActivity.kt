package com.example.newstoryapp.View

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.newstoryapp.Api.APISettings
import com.example.newstoryapp.R
import com.example.newstoryapp.databinding.ActivityMainBinding
import com.example.newstoryapp.response.ResponRegister
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        playAnimation()
        getApiRegist()
    }


    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val edtUser = ObjectAnimator.ofFloat(binding.edtUser, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val edtPass = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val btnSign = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(500)
        val textSign = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(500)
        val btnLog = ObjectAnimator.ofFloat(binding.btnSignin, View.ALPHA, 1f).setDuration(500)


        val together = AnimatorSet().apply {
            playTogether(btnLog, textSign)
        }


        AnimatorSet().apply {
            playSequentially(edtUser, edtEmail, edtPass, btnSign, together)
            start()
        }
    }


    private fun getApiRegist() {
        binding.btnSignup.setOnClickListener {
            val user = binding.edtUser.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            when {
                user.isEmpty() ->{
                    binding.edtUser.error = "Masukkan username"
                }
                email.isEmpty() -> {
                    binding.edtEmail.error = "Masukkan email"
                }
                // Validasi input password
                password.isEmpty() -> {
                    binding.edtPassword.error = "Masukkan password"
                }
                else -> {
                    showLoading(true) // Menampilkan loading
                    val client = APISettings.getApiService().getRegist(user,email,password)
                    client.enqueue(object : Callback<ResponRegister> {
                        override fun onResponse(
                            call: Call<ResponRegister>,
                            response: Response<ResponRegister>
                        ) {
                            showLoading(false)
                            if (response.isSuccessful) {
                                AlertDialog.Builder(this@MainActivity).apply {
                                    setTitle("Yeah!")
                                    setMessage(resources.getString(R.string.message_regist))
                                    setPositiveButton(resources.getString(R.string.message_btn)) { _, _ ->
                                        val intent = Intent(this@MainActivity, EnterActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            } else {
                                showLoading(false) // Menyembunyikan loading
                                Toast.makeText(this@MainActivity, resources.getString(R.string.toast_regist), Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponRegister>, t: Throwable) {
                            Toast.makeText(this@MainActivity, resources.getString(R.string.toast_error), Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
        binding.btnSignin.setOnClickListener{
            val intent = Intent(this@MainActivity, EnterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

