package com.bp.loginsmartworddemo

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bp.loginsmartworddemo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.fragment_sign_in.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SignInFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var mlistener: OnFragmentSignInListener? = null
    private val mAuth = FirebaseAuth.getInstance()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_sign_in, container, false)
        view.tv_register_now.setOnClickListener{
            mlistener?.onFragmentClickRegister("Move to register")
        }
        view.btn_Login.setOnClickListener{
            signInEvent()
        }
        return view
    }




    override fun onStart() {
        super.onStart()
        val pre : SharedPreferences = activity!!.getSharedPreferences("user_email", Context.MODE_PRIVATE)
        val emailGet = pre.getString("email","unknown")
        val phoneGet = pre.getString("phone","unknown")
        val photoGet = pre.getString("photo","unknown")
        val fullNameGet = pre.getString("fullname","unknown")
        val addressGet = pre.getString("address", "unknown")

        if(!emailGet.equals("unknown")){
            val intent = Intent(activity, HomeActivity::class.java)
            intent.putExtra("email", emailGet)
            intent.putExtra("user", User(fullNameGet!!, emailGet!!, phoneGet!!,photoGet!!, addressGet!!))
            startActivity(intent)
            activity!!.finish()
        }
    }




    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentSignInListener) {
            mlistener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }




    override fun onDetach() {
        super.onDetach()
        mlistener = null
    }




    interface OnFragmentSignInListener {
        fun onFragmentClickRegister(content : String)
    }




    /* Sign In Event */
    private fun signInEvent() {
        val dialog = activity?.let { Dialog(it) }
        showDiaglogLoading(dialog!!)
        val email = edt_email_login?.text.toString()
        val password = edt_password_login?.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            activity?.let {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(it){ task ->
                        if(task.isSuccessful){
                            val intent = Intent(activity, HomeActivity::class.java)
                            intent.putExtra("email", email)
                            intent.putExtra("current_user", FirebaseAuth.getInstance().currentUser)
                            val ref = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.currentUser!!.uid)
                            ref.addListenerForSingleValueEvent(object: ValueEventListener {

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val fullName = snapshot.child("fullName").value.toString()
                                    val photo = snapshot.child("photo").value.toString()
                                    val phone = snapshot.child("phone").value.toString()
                                    val address  = snapshot.child("address").value.toString()
                                    val user : User
                                    user = User(fullName, email, phone, photo, address)
                                    val pre : SharedPreferences = activity!!.getSharedPreferences("user_email",
                                        Context.MODE_PRIVATE)
                                    val edit : SharedPreferences.Editor = pre.edit()
                                    edit.putString("email", email)
                                    edit.putString("fullname", fullName)
                                    edit.putString("phone", phone)
                                    edit.putString("photo", photo)
                                    edit.putString("address", address)
                                    edit.apply()
                                    intent.putExtra("user", user)
                                    startActivity(intent)
                                    activity!!.finish()

                                }

                                override fun onCancelled(p0: DatabaseError) {}
                            })
                        }else{
                            dissmisDialogLoading(dialog)
                            Toast.makeText(activity, R.string.wrongemailorpassword, Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }else{
            dissmisDialogLoading(dialog)
            Toast.makeText(activity, R.string.pleasefilloutthisform, Toast.LENGTH_LONG).show()
        }
    }




    /* Show Dialog Loading when Click SignIn */
    private fun showDiaglogLoading(dialog: Dialog) {
        dialog.setContentView(R.layout.signin_loading_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }




    /* Dissmis Dialog */
    private fun dissmisDialogLoading(dialog: Dialog) {
        dialog.dismiss()
    }




}
