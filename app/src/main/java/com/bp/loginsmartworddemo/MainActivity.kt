package com.bp.loginsmartworddemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId


class MainActivity : AppCompatActivity(), SignInFragment.OnFragmentSignInListener, SignUpFragment.OnSignUpFragmentListener {

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val signInFragment = SignInFragment()
        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frameLayout, signInFragment)
        fragmentTransaction.addToBackStack("fragment_signin")
        fragmentTransaction.commit()

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d("TAG", token)
//                Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            })
    }



    /* Click Button Register in Fragment SignIn */
    override fun onFragmentClickRegister(content: String) {

        val signUpFragment = SignUpFragment()
        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        fragmentTransaction.add(R.id.frameLayout, signUpFragment, "sign_up_fragment")
        fragmentTransaction.commit()

    }



    /* Click Button Login in Fragment SignUp */
    override fun onFragmentClickLogin(content: String) {

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up_res, R.anim.slide_out_up_res)
        fragmentManager.findFragmentByTag("sign_up_fragment")?.let { fragmentTransaction.remove(it) }
        fragmentTransaction.commit()
        fragmentManager.popBackStack("fragment_signin", 0)

    }



    /* Click Back button in Fragment SignUp */
    override fun onBackPressed() {

        if(supportFragmentManager.backStackEntryCount>0){
           removeSignUpFragment()
        }else{
            super.onBackPressed()
        }

    }



    /* Remove Fragment Signup*/
    private fun removeSignUpFragment(){

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up_res, R.anim.slide_out_up_res)
        fragmentManager.findFragmentByTag("sign_up_fragment")?.let { fragmentTransaction.remove(it) }
        fragmentTransaction.commit()
        fragmentManager.popBackStack("fragment_signin", 0)

    }




}
