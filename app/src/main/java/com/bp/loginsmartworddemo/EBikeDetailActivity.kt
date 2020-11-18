package com.bp.loginsmartworddemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bp.loginsmartworddemo.fragment.MapFragment
import com.bp.loginsmartworddemo.fragment.RatingFragment
import com.bp.loginsmartworddemo.model.Ebike
import com.bumptech.glide.Glide

class EBikeDetailActivity : AppCompatActivity(), MapFragment.OnMapFragmentListener, RatingFragment.OnRatingFragmentListener {

    private var mEbike: Ebike? = null



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ebike_detail)
        mEbike = intent.getSerializableExtra("ebike") as Ebike
        initView()

    }




    private fun initView() {

        val tvNameDetail = findViewById<TextView>(R.id.tv_name_detail)
        val tvAddressDetail = findViewById<TextView>(R.id.tv_address_detail)
        val tvBrandDetail = findViewById<TextView>(R.id.tv_brand_detail)
        val tvPriceDetail = findViewById<TextView>(R.id.tv_price_detail)
        val tvPhoneDetail = findViewById<TextView>(R.id.tv_phone_detail)
        val ivDetail = findViewById<ImageView>(R.id.iv_detail)
        val btnShowOnMap = findViewById<Button>(R.id.btn_show_on_map_detail)
        val ivBackDetail = findViewById<ImageView>(R.id.iv_back_detail)

        tvNameDetail.text = mEbike?.name
        tvAddressDetail.text = mEbike?.address
        tvBrandDetail.text = mEbike?.brand
        tvPriceDetail.text = mEbike?.price
        tvPhoneDetail.text = mEbike?.phone

        Glide.with(this).load(mEbike?.photo).into(ivDetail)

        btnShowOnMap.setOnClickListener{
            eventShowOnMap()
        }

        ivBackDetail.setOnClickListener {
            onBackPressed()
        }
    }




    override fun onMapBackPressed(content: String) {

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_up2, R.anim.slide_out_up_res)
            val mapFragment = fragmentManager.findFragmentByTag("map_fragment")
            if(mapFragment != null) {
                fragmentTransaction.remove(mapFragment)
                fragmentTransaction.commit()
            }

    }

    override fun onCompleteRenting(idEBike: String, idUser: String, name: String, photo: String) {

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        fragmentTransaction
            .replace(R.id.frame_map_layout, RatingFragment.newInstance(idEBike, idUser, name, photo), "rating_fragment")
            .commit()

    }






    private fun eventShowOnMap() {

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        val isDetailActivity = true
        fragmentTransaction
            .add(R.id.frame_map_layout, MapFragment.newInstance(mEbike, isDetailActivity), "map_fragment")
            .commit()

    }

    override fun onBackPressed(content: String) {

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up2, R.anim.slide_out_up_res)
        val mapFragment = fragmentManager.findFragmentByTag("rating_fragment")
        if(mapFragment != null) {
            fragmentTransaction.remove(mapFragment)
            fragmentTransaction.commit()

        }

    }


}
