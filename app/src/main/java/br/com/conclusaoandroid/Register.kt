package br.com.conclusaoandroid

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.conclusaoandroid.common.Utils
import br.com.conclusaoandroid.databinding.ActivityRegisterBinding
import com.example.mobcomponents.customtoast.CustomToast
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)

        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        setUpListener()
    }

    private fun setUpListener() {
        binding.btnRegister.setOnClickListener {
            binding.progressBarRegister.visibility = View.VISIBLE
            val email = binding.emailRegister.text.toString()
            val password = binding.passwordRegister.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                binding.progressBarRegister.visibility = View.GONE
                CustomToast.warning( this, getString(R.string.register_validate) )
                return@setOnClickListener
            }

            if(!Utils.emailValidator(email)) {
                binding.progressBarRegister.visibility = View.GONE
                CustomToast.error( this, getString(R.string.email_validate) )
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.progressBarRegister.visibility = View.GONE

                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        CustomToast.success( this, getString(R.string.account_created) )
                        startLoginPage()
                    } else {
                        binding.progressBarRegister.visibility = View.GONE
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        CustomToast.error(this, getString(R.string.account_created_error))
                    }
                }
        }

        binding.loginNow.setOnClickListener {
            startLoginPage()
        }
    }

    private fun startLoginPage() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}