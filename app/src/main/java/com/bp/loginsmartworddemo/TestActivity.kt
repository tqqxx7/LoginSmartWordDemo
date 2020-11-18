package com.bp.loginsmartworddemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bp.loginsmartworddemo.model.Product
import com.bp.loginsmartworddemo.services.APIUtils
import com.bp.loginsmartworddemo.services.DataClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_test.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        loadUser()

//        val database = FirebaseDatabase.getInstance()
//        val myRef = database.getReference("DriverTest")
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//
//
//                for (data in dataSnapshot.children) {
//                    val key = data.key
//
//                    val value = data.value.toString()
//                    tv_test.setText(key + "\n" + value)
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w("dasd", "loadPost:onCancelled", databaseError.toException())
//            }
//        })
    }

    private fun loadUser() {
//        val dataClient: DataClient = APIUtils.getDataFirebase()
//            val callback: Call<List<Product>> = dataClient.loadProductTest()
//            callback.enqueue(object : Callback<List<Product>> {
//
//                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
//                    val productList: ArrayList<Product> = response.body() as ArrayList<Product>
//                    Toast.makeText(this@TestActivity, response.body().toString(), Toast.LENGTH_LONG).show()
//                    tv_test.setText(productList[0].getName())
//                }
//
//                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
//                    Toast.makeText(this@TestActivity, t.message, Toast.LENGTH_LONG).show()
//                    Log.d("errotest", t.message)
//                }
//
//            })

    }
}
