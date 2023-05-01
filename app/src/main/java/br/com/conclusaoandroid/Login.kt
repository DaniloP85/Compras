package br.com.conclusaoandroid

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import br.com.conclusaoandroid.common.Utils
import br.com.conclusaoandroid.databinding.ActivityLoginBinding
import com.example.mobcomponents.customtoast.CustomToast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)

        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        setUpListener()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            startMainPage()
        }
    }

    private fun setUpListener() {
        binding.registerNow.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            binding.progressBarLogin.visibility = View.VISIBLE
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                binding.progressBarLogin.visibility = View.GONE
                CustomToast.warning( this, getString(R.string.login_validate_fields) )
                return@setOnClickListener
            }

            if(!Utils.emailValidator(email)) {
                binding.progressBarLogin.visibility = View.GONE
                CustomToast.error( this, getString(R.string.email_validate) )
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    CustomToast.success( this, "Successful Authentication :)" )
                    startMainPage()

                } else {
                    binding.progressBarLogin.visibility = View.GONE
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    CustomToast.error(this, "Authentication failed :(")
                }
            }
        }
    }

    private fun startMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}