package com.bp.loginsmartworddemo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bp.loginsmartworddemo.fragment.*
import com.bp.loginsmartworddemo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService


class HomeActivity : AppCompatActivity(), HomeFragment.OnHomeFragmentListener, ListProductFragment.OnListProductFragmentListener, MapFragment.OnMapFragmentListener, ProfileFragment.OnProfileFragmentManager, EditProfileFragment.OnEditProfileFragmentListener, ChooseAddressFragment.OnMapFragmentListener, RatingFragment.OnRatingFragmentListener {

    var user : User? = null
    var bundle : Bundle? = null
    private var mCancel = false




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
            val bundle = Bundle()
            user = intent.getSerializableExtra("user") as User?
            bundle.putSerializable("user",user)
            val homeFragment  = HomeFragment()
            homeFragment.arguments = bundle
            val fragmentManager : FragmentManager = supportFragmentManager
            val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_home_layout, homeFragment, "fragment_home")
                .addToBackStack("fragment_home")
                .commit()

    }

    override fun onStart() {
        super.onStart()
        val pre : SharedPreferences = this.getSharedPreferences("test", Context.MODE_PRIVATE)
        val rent = pre.getString("saveRent","false")
        if(rent.equals("true")){
        eventClickMap()
        }
    }

    override fun onSignoutClickListener(content: String) {
        eventClickSignOut()
    }

    override fun onNextClickListener(content: String) {
        eventClickNext()
    }

    override fun onMapClickListener(content: String) {
        eventClickMap()
    }

    override fun onProfileClickListener(content: String) {
        eventClickProfile()
    }




    private fun eventClickProfile() {

        val bundle1 = Bundle()
        bundle1.putSerializable("user",user)
        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        if (fragmentManager.findFragmentByTag("profile_fragment") == null){
            val profileFragment = ProfileFragment()
            profileFragment.arguments = bundle1
            fragmentTransaction
                .replace(R.id.frame_home_layout,profileFragment , "profile_fragment")
                .addToBackStack("profile_fragment")
                .commit()
        } else {
            val profileFragment = fragmentManager.findFragmentByTag("profile_fragment")
            if(profileFragment != null) {
                fragmentTransaction.replace(R.id.frame_home_layout, profileFragment, "profile_fragment")
                fragmentTransaction.commit()
            }
        }

    }




    private fun eventClickMap() {

        val bundleMap = Bundle()
        bundleMap.putSerializable("user",user)
        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
                val mapFragment = MapFragment()
                mapFragment.arguments = bundleMap
                fragmentTransaction
                    .replace(R.id.frame_home_layout, mapFragment, "map_fragment")
                    .commit()

    }




    override fun onMapBackPressed(content: String) {

        mCancel = content == "canceled"
        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in_up2, R.anim.slide_out_up_res)

        val homeFragment = fragmentManager.findFragmentByTag("fragment_home")
        if(homeFragment != null) {
            fragmentTransaction
                .replace(R.id.frame_home_layout, homeFragment, "fragment_home")
                .commit()
        }

    }

    override fun onCompleteRenting(idEBike: String, idUser: String, name: String, photo: String) {

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        fragmentTransaction
            .replace(R.id.frame_home_layout, RatingFragment.newInstance(idEBike, idUser, name, photo), "rating_fragment")
            .commit()

    }




    private fun eventClickNext() {

            val fragmentManager : FragmentManager = supportFragmentManager
            val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)

            if (fragmentManager.findFragmentByTag("list_product_fragment") == null){
                    fragmentTransaction
                        .replace(R.id.frame_home_layout, ListProductFragment(), "list_product_fragment")
                        .addToBackStack("list_product_fragment")
                        .commit()
            } else {
                    val listProductFragment = fragmentManager.findFragmentByTag("list_product_fragment")
                    if(listProductFragment != null) {
                        fragmentTransaction
                            .replace(R.id.frame_home_layout, listProductFragment, "list_product_fragment")
                            .commit()
                    }
            }

    }




    override fun onBackArrowPressed(content: String) {
        backToHome()
    }




    private fun backToHome() {

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_up2, R.anim.slide_out_up_res)
        val homeFragment = fragmentManager.findFragmentByTag("fragment_home")
        if(homeFragment != null) {
            fragmentTransaction.replace(R.id.frame_home_layout, homeFragment, "fragment_home")
            fragmentTransaction.commit()
        }

    }





    private fun eventClickSignOut() {
        FirebaseAuth.getInstance().signOut()
        val pre : SharedPreferences = getSharedPreferences("user_email", Context.MODE_PRIVATE)
        val edit : SharedPreferences.Editor = pre.edit()
        edit.clear()
        edit.apply()
        finish()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email_sign_out", "unknown")
        startActivity(Intent(this, MainActivity::class.java))
    }





    override fun onProfileBackPressed(content: String) {
        backToHome()
    }





    override fun onEditProfilePressed(content: String) {
        val bundle2 = Bundle()
        bundle2.putSerializable("user",user)
        val fragmentManager : FragmentManager = supportFragmentManager
        val editProfileFragment = EditProfileFragment()
        editProfileFragment.arguments = bundle2
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        fragmentTransaction.replace(R.id.frame_home_layout, editProfileFragment, "edit_profile_fragment")
                           .addToBackStack("edit_profile_fragment")
        fragmentTransaction.commit()
    }





    override fun onBackPressed(user: User, code: String) {
        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up2, R.anim.slide_out_up_res)
            val profileFragment = fragmentManager.findFragmentByTag("profile_fragment")
                if(code == "update_changed"){
                    val bundleUpdate = Bundle()
                    bundleUpdate.putSerializable("user",user)
                }
                fragmentTransaction.replace(R.id.frame_home_layout, profileFragment!!, "profile_fragment")
                fragmentTransaction.commit()
    }





    override fun onChooseAddressPressed(content: String) {
        val bundle2 = Bundle()
        bundle2.putSerializable("user",user)
        val fragmentManager : FragmentManager = supportFragmentManager
        val chooseAddressFragment = ChooseAddressFragment()
        chooseAddressFragment.arguments = bundle2
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        fragmentTransaction.replace(R.id.frame_home_layout, chooseAddressFragment, "choose_address_fragment")
        fragmentTransaction.commit()
    }





    override fun onAddressBackPressed(content: String) {

        val bundle3 = Bundle()
        bundle3.putSerializable("user",user)
        val fragmentManager : FragmentManager = supportFragmentManager
        val editProfileFragment = fragmentManager.findFragmentByTag("edit_profile_fragment") as EditProfileFragment
        editProfileFragment.arguments = bundle3
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up2, R.anim.slide_out_up_res)
        fragmentTransaction.replace(R.id.frame_home_layout, editProfileFragment, "edit_profile_fragment")
        fragmentTransaction.commit()
    }





    override fun onComfirmPressed(address: String) {
        val bundle2 = Bundle()
        bundle2.putSerializable("user",user)
        bundle2.putSerializable("address",address)
        val fragmentManager : FragmentManager = supportFragmentManager
        val editProfileFragment = fragmentManager.findFragmentByTag("edit_profile_fragment") as EditProfileFragment
        editProfileFragment.arguments = bundle2
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up2, R.anim.slide_out_up_res)
        fragmentTransaction.replace(R.id.frame_home_layout, editProfileFragment, "edit_profile_fragment")
        fragmentTransaction.commit()
    }




    override fun onBackPressed(content: String) {
        backToHome()
    }


}
