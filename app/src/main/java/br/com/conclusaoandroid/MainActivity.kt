package br.com.conclusaoandroid

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.conclusaoandroid.adapter.ShoppingAdapter
import br.com.conclusaoandroid.databinding.ActivityMainBinding
import br.com.conclusaoandroid.model.Shopping
import com.example.mobcomponents.customtoast.CustomToast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var shoppingAdapter: ShoppingAdapter
    private lateinit var userId: String
    private lateinit var documentIdUpdate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)

        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            gotoLogin()
        }

        binding.logMenu.logout.setOnClickListener {
            auth.signOut()
            gotoLogin()
        }

        val shoppingQuery = Firebase.firestore
            .collection("shopping")
            .whereEqualTo("userId", auth.uid)
            .limit(50)

        userId = auth.uid.toString()

        shoppingAdapter = object : ShoppingAdapter(
            shoppingQuery,
            { shopping -> adapterOnClick(shopping) },
            { shopping -> adapterOnClickEditShopping(shopping) }) {
            override fun onDataChanged() {

                if (itemCount == 0) {
                    binding.rltHome.visibility = View.GONE
                    binding.rltEmptyState.visibility = View.VISIBLE
                } else {
                    binding.rltHome.visibility = View.VISIBLE
                    binding.rltEmptyState.visibility = View.GONE
                    binding.recyclerShopping.adapter = shoppingAdapter
                }
            }
        }

        setupListener()
    }

    private fun adapterOnClickEditShopping(shopping: Shopping) {
        documentIdUpdate = shopping.documentId.toString()
        alert(shopping.marketplace.toString(), getString(R.string.update_register), getString(R.string.edit)) { name ->
            updateShopping(name)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun adapterOnClick(shopping: Shopping) {
        val intent = Intent(this, AddEditListShoppingActivity::class.java)
        intent.putExtra("documentId", "${shopping.documentId}")
        intent.putExtra("marketPlace", "${shopping.marketplace}")

        val pattern = "dd/MM/yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = shopping.date?.toDate()?.let { simpleDateFormat.format(it) }
        intent.putExtra("marketDate", "$date")

        startActivity(intent)
        finish()
    }

    private fun alert(textInputAlert: String, title: String, labelButton: String, exec: (String) -> Unit){

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle(title)
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_with_edittext, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)

        if (textInputAlert.isNotBlank()) {
            editText.setText(textInputAlert)
        }

        builder.setView(dialogLayout)
        builder.setPositiveButton(labelButton) { _, _ ->
            if (editText.text.isNotBlank()) {
                exec(editText.text.toString())
            }
        }

        builder.setNegativeButton(R.string.cancel) {
                dialog, _ -> dialog.cancel()
        }

        builder.show()
    }

    private fun setupListener() {
        binding.addShopping.setOnClickListener{
            alert("", getString(R.string.enter_market_name), getString(R.string.add_new)) { name ->
                addShopping(name)
            }
        }
    }

    private fun updateShopping(nameMarkerPlace: String){
        Firebase.firestore
            .collection("shopping")
            .document(documentIdUpdate)
            .update("marketplace", nameMarkerPlace)
            .addOnSuccessListener {
                Log.d(TAG,":)")
                CustomToast.success( this, getString(R.string.registered_successfully) )
            }.addOnFailureListener { e ->  Log.d(TAG,":( :: $e") }
    }

    private fun addShopping(marketplace:String){

        val shopping = hashMapOf(
            "userId" to userId,
            "marketplace" to marketplace,
            "date" to Timestamp.now(),
            "total" to 0
        )

        Firebase
            .firestore
            .collection("shopping")
            .add(shopping)
            .addOnSuccessListener { documentReference ->
                CustomToast.success( this, getString(R.string.registered_successfully) )
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }
    }

    private fun gotoLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

    public override fun onStart() {
        super.onStart()
        shoppingAdapter.startListening()
    }

    public override fun onStop() {
        super.onStop()
        shoppingAdapter.stopListening()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}