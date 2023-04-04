package br.com.conclusaoandroid

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import br.com.conclusaoandroid.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding;

    lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);

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
            val intent = Intent(this, Register::class.java);
            startActivity(intent);
            finish();
        }

        binding.btnLogin.setOnClickListener {
            binding.progressBarLogin.visibility = View.VISIBLE;
            val email = binding.email.text.toString();
            val password = binding.password.text.toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.login_validate_fields), Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")

                        Toast.makeText(baseContext, "Successful Authentication.",
                            Toast.LENGTH_SHORT).show()

                        startMainPage()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    private fun startMainPage() {
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent);
        finish();
    }

}