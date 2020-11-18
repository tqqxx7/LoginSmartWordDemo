package com.bp.loginsmartworddemo

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import com.bp.loginsmartworddemo.model.User
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@Suppress("DEPRECATION", "NAME_SHADOWING")
class SignUpFragment : Fragment() {
    private val mpickImageRequest = 71
    private var mFilePath: Uri? = null
    private var mFirebaseStore: FirebaseStorage? = null
    private var mStorageReference: StorageReference? = null
    private var param1: String? = null
    private var param2: String? = null
    private var mListener: OnSignUpFragmentListener? = null
    private var mPath: String? = null
    private var mDialog : Dialog? = null
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
        val view : View = inflater.inflate(R.layout.fragment_sign_up, container, false)

        mPath = UUID.randomUUID().toString()
        mFirebaseStore = FirebaseStorage.getInstance()
        mStorageReference = FirebaseStorage.getInstance().reference
        mDialog = Dialog(requireActivity())

        view.btn_login_now.setOnClickListener{
            mListener?.onFragmentClickLogin("move to Login")
        }
        view.btn_register.setOnClickListener{ uploadImage() }
        view.img_avatar.setOnClickListener { imagePicker() }

        return view
    }





    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSignUpFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }




    override fun onDetach() {
        super.onDetach()
        mListener = null
    }




    interface OnSignUpFragmentListener {
        fun onFragmentClickLogin(content: String)
    }




    /* Event SignUp event */
    private fun signUpEvent() {
        val email = edt_email_register.text.toString()
        val phone = edt_phone_register.text.toString()
        val fullName = edt_fullname_register.text.toString()
        val password = edt_password_register.text.toString()
        val confirmPassword = edt_confirmpassword_register.text.toString()
        var pathPhoto : String

        if(validateEmail(email) && validatePassword(password)){
            if(password == confirmPassword){
                activity?.let {
                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(it){ task ->
                            if(task.isSuccessful){
                                if(mPath.toString() != null){
                                     pathPhoto = mPath.toString()
                                }else{
                                     pathPhoto = "unknown"
                                }
                                val user = User(fullName, email, phone, pathPhoto, "n/a")
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .setValue(user).addOnCompleteListener(it){ task ->
                                        if(task.isSuccessful){
                                            showDialogSuccess()
                                            mDialog?.let { it1 -> dissmisDialogLoading(it1) }
                                        }
                                    }
                            }
                        }
                }
            }else{
                mDialog?.let { dissmisDialogLoading(it) }
                Toast.makeText(activity, R.string.message_error_confirm_password, Toast.LENGTH_LONG).show()
            }
        }else{
            mDialog?.let { dissmisDialogLoading(it) }
        }

    }




    /* Validate Email: require Google Mail */
    private fun validateEmail(email : String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@gmail.com"
        return if (email.isEmpty()) {
            Toast.makeText(activity, R.string.pleasefilloutthisform, Toast.LENGTH_LONG).show()
            false
        } else if (!email.matches(emailPattern.toRegex())) {
            Toast.makeText(activity, R.string.emailinvalid, Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }




    /* Validate Password: Not Empty and > 8 Characters */
    private fun validatePassword(password : String): Boolean {
        return when {
            password.isEmpty() -> {
                Toast.makeText(activity, R.string.pleasefilloutthisform, Toast.LENGTH_LONG).show()
                false
            }
            password.length < 8 -> {
                Toast.makeText(activity, R.string.passwordatleast8characters, Toast.LENGTH_LONG).show()

                false
            }
            else -> {
                true
            }
        }
    }




    /* Show Dialog Success Register */
    private fun showDialogSuccess() {
        val dialog = activity?.let { Dialog(it) }
        dialog?.setContentView(R.layout.signup_success_dialog)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val handler = Handler()
        val runnable = Runnable {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }

        handler.postDelayed(runnable, 2500)
        dialog.setOnDismissListener(
            DialogInterface.OnDismissListener {
                handler.removeCallbacks(runnable)
                startActivity(Intent(activity, MainActivity::class.java))
            }
        )
    }




    /* Show Dialog Loading */
    private fun showDiaglogLoading(dialog: Dialog) {
        dialog.setContentView(R.layout.signup_loading_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }




    /* Dismiss Dialog Loading */
    private fun dissmisDialogLoading(dialog: Dialog) {
        dialog.dismiss()
    }




    /* Pick Image */
    private fun imagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), mpickImageRequest)
    }




    /* Get RealPath and upload image to database  */
    private fun uploadImage(){
        if(mFilePath != null){
            mDialog?.let { showDiaglogLoading(it) }
            val ref = mStorageReference?.child("uploads/" + mPath.toString())
            ref?.putFile(mFilePath!!)?.addOnSuccessListener { taskSnapshot ->
                val test = taskSnapshot.storage.downloadUrl
                test.addOnCompleteListener {
                    val downloadURL: String = "https://" + it.result!!.encodedAuthority + it.result!!.encodedPath + "?alt=media&token="+ it.result!!.getQueryParameters("token")[0]
                    mPath = downloadURL
                    if(mPath!= null){
                        signUpEvent()
                    }
                }
            }?.addOnFailureListener { e ->
                Toast.makeText(activity, "Image Uploading Failed " + e.message, Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(activity, "Please Select an Image", Toast.LENGTH_SHORT).show()
        }
    }




    /* Load Image to CircleView after Pick image */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mpickImageRequest && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            mFilePath = data.data
            try {
                Glide.with(requireActivity()).load(mFilePath).into(img_avatar)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }




}
