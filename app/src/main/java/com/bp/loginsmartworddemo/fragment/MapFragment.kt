package com.bp.loginsmartworddemo.fragment

import ListMapAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.media.RingtoneManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.bp.loginsmartworddemo.HomeActivity
import com.bp.loginsmartworddemo.MainActivity
import com.bp.loginsmartworddemo.MapUtils
import com.bp.loginsmartworddemo.R
import com.bp.loginsmartworddemo.model.*
import com.bp.loginsmartworddemo.services.APIUtils
import com.bp.loginsmartworddemo.services.DataClient
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MapFragment : Fragment(), OnMapReadyCallback {
    private var param1: String? = null
    private var param2: String? = null
    private var mListener: OnMapFragmentListener? = null
    private lateinit var mMap: GoogleMap
    private var mAddressSelected: LatLng = LatLng(0.2, 0.2)
    private var mMarkerOption: MarkerOptions? = null
    private var mBikeMaker: BitmapDescriptor? = null
    private var mListBike = ArrayList<Ebike>()
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var mCustomInfoWindowLayout: View? = null
    private var mListMarkerEBike: ArrayList<Marker>? = null
    private var mEbikeCurrent: Int? = null
    private var mEbike: Ebike? = null
    private var mIsDetailActivity: Boolean = false
    private var mIsConfirmed: Boolean = false
    private var mUser: User? = null
    private var mRenting: Boolean = false
    private lateinit var mPre : SharedPreferences
    private lateinit var mTest: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            mUser = it.getSerializable("user") as User?
        }
    }




    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        mBikeMaker = MapUtils().getMarkerIconFromDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_ebike))
        mCustomInfoWindowLayout = layoutInflater.inflate(R.layout.layout_bike_info_window, null)
        mBottomSheetBehavior = BottomSheetBehavior.from(view.bottom_sheet_layout)
        searchMap(view)

        view.img_map_back_arrow.setOnClickListener {
            if(mEbikeCurrent==null){
                onButtonBackPressed("back_to_home")
            }else{
                showBackConfirmDialog(mEbikeCurrent)
            }
        }


        return view

    }




    @SuppressLint("MissingPermission", "CommitPrefEdits")
    override fun onStart() {
        super.onStart()
        if (!checkPermissions())  requestPermissions()
        mPre = activity!!.getSharedPreferences("test",
            Context.MODE_PRIVATE)
        mTest = mPre.edit()
        if(mPre.getString("saveRent","false").equals("true")){
            val idEBike: String = mPre.getString("saveIdEBike","unknown")!!
            val name: String = mPre.getString("saveName","unknown")!!
            val photo: String = mPre.getString("savePhoto","unknown")!!
            checkCompleteRenting(idEBike, name, photo)
        }

    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMapFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }




    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        if(mRenting){
//
//            mTest.apply()
//        }
//    }





    private fun showBackConfirmDialog(idEBike: Int?) {

        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.back_booking_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btnBackBook = dialog.findViewById<Button>(R.id.btn_back_booking)
        btnBackBook.setOnClickListener{
            updateBookingCancel(idEBike)
            updateRentingCancel(idEBike)
            dialog.dismiss()
            onButtonBackPressed("canceled")
        }

        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel_back_booking)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

    }

    



    private fun onButtonBackPressed(string: String) {
        mListener?.onMapBackPressed(string)
    }

    private fun onCompleteRenting(idEBike: String, idUser: String, name: String, photo: String) {
        mListener?.onCompleteRenting(idEBike, idUser, name, photo)
    }




    interface OnMapFragmentListener {
        fun onMapBackPressed(content: String)
        fun onCompleteRenting(idEBike: String, idUser: String, name: String, photo: String)
    }




    companion object {
        private const val TAG1 = "LocationProvider"
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        var mapFragment: SupportMapFragment? = null
        val TAG: String = MapFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(ebike: Ebike?, isDetailActivity: Boolean) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }

                this.mEbike = ebike
                this.mIsDetailActivity = isDetailActivity
            }

    }




    private fun updateBookingCancel(idEBike: Int?) {

        val ref = FirebaseDatabase.getInstance().getReference("Ebike").child(idEBike.toString())

        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.ref.child("booked").setValue(false)
            }
        })

    }

    private fun updateRentingCancel(idEBike: Int?) {

        val ref = FirebaseDatabase.getInstance().getReference("Renting").child(idEBike.toString())

        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.ref.child("id_user").setValue(-1)
                dataSnapshot.ref.child("complete").setValue(false)
            }
        })

    }




    @SuppressLint("PrivateResource")
    private fun searchMap(view: View) {

        val adapter = ListMapAdapter(requireContext())
        view.act_search.setAdapter(adapter)
        view.act_search.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->

            val selectedItem = parent.getItemAtPosition(position)
            val addressClick: AddressTextSearchModel = selectedItem as AddressTextSearchModel
            val addressGeometry: AddressGeometry? = addressClick.geometry
            val addressLocation: AddressLocation? = addressGeometry?.location

            view.findViewById<AutoCompleteTextView>(R.id.act_search).setText(addressClick.name)
            mAddressSelected = LatLng(addressLocation!!.lat.toDouble(), addressLocation.lng.toDouble())
            val cameraPosition: CameraPosition = CameraPosition.Builder().target(mAddressSelected).zoom(15F).build()

            mMarkerOption = MarkerOptions().position(mAddressSelected).title("Marker in: " + addressClick.name)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        }

    }





    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.context, R.raw.map_style))
        Handler().postDelayed({
            loadAllEBike()
        }, 1000)


        Log.d("mListBike", mListBike.size.toString())
        if (checkPermissions()) {
            mMap.isMyLocationEnabled = true
        }

        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = false

        mMap.setOnMarkerClickListener { marker ->

            val idMarker: Int = marker?.tag.toString().toInt()

            mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                @SuppressLint("SetTextI18n", "InflateParams")
                override fun getInfoContents(maker: Marker?): View {
                    val tvName = mCustomInfoWindowLayout!!.findViewById<AppCompatTextView>(R.id.tv_name_driver)
                    tvName?.text = mListBike[idMarker].name
                    val tvBrand = mCustomInfoWindowLayout?.findViewById<AppCompatTextView>(R.id.tv_brand_bkie)
                    tvBrand?.text = "Brand: " + mListBike[idMarker].brand
                    return mCustomInfoWindowLayout as View
                }

                override fun getInfoWindow(p0: Marker?): View? {
                    return null
                }
            })



            marker?.showInfoWindow()
            tv_name_bt_sheet?.text = mListBike[idMarker].name
            tv_address_bt_sheet.text = mListBike[idMarker].address
            tv_brand_bt_sheet.text = mListBike[idMarker].brand
            tv_price_bt_sheet.text = mListBike[idMarker].price
            rt_bt_sheet.rating = mListBike[idMarker].rate

            var payment = ""
            if(mListBike[idMarker].payment?.cash!!){ payment += "cash, "}
            if(mListBike[idMarker].payment?.momo!!){ payment += "momo, "}
            if(mListBike[idMarker].payment?.banking!!){ payment += "banking"}


            tv_payment_bt_sheet.text = payment
            btn_call_bt_sheet.setOnClickListener{
                startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mListBike[idMarker].phone, null)))
            }


            btn_book_btsheet.setOnClickListener{

                val dialog = Dialog(requireActivity())
                dialog.setContentView(R.layout.comfirm_dialog)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()


                val ivEbikeConfirm = dialog.findViewById<ImageView>(R.id.iv_bike_confirm)
                Glide.with(requireActivity()).load(mListBike[idMarker].photo).into(ivEbikeConfirm)
                val tvBrandConfirm = dialog.findViewById<TextView>(R.id.tv_brand_confirm)
                tvBrandConfirm.text = mListBike[idMarker].brand
                val tvPriceConfirm = dialog.findViewById<TextView>(R.id.tv_price_confirm)
                tvPriceConfirm.text = mListBike[idMarker].price.toString()
                val ivCashConfirm = dialog.findViewById<ImageView>(R.id.img_cash_confirm)
                val ivMomoConfirm = dialog.findViewById<ImageView>(R.id.img_momo_confirm)
                val ivBankingConfirm = dialog.findViewById<ImageView>(R.id.img_banking_confirm)
                val ivRightmarkCash = dialog.findViewById<ImageView>(R.id.img_rightmark_cash)
                val ivRightmarkMomo = dialog.findViewById<ImageView>(R.id.img_rightmark_momo)
                val ivRightmarkBanking = dialog.findViewById<ImageView>(R.id.img_rightmark_banking)
                val tvCashConfirm = dialog.findViewById<TextView>(R.id.tv_cash_confirm)
                val tvMomoConfirm = dialog.findViewById<TextView>(R.id.tv_momo_confirm)
                val tvBankingConfirm = dialog.findViewById<TextView>(R.id.tv_banking_confirm)

                if(mListBike[idMarker].payment?.cash == false){
                    ivCashConfirm.setImageResource(R.drawable.ic_cash_hide)
                    ivRightmarkCash.setImageResource(R.drawable.ic_round_hind)
                    ivRightmarkCash.visibility = View.GONE
                    tvCashConfirm.setTextColor(ContextCompat.getColor(requireContext(), R.color.hide))
                }
                if(mListBike[idMarker].payment?.momo == false){
                    ivMomoConfirm.setImageResource(R.drawable.ic_momo_hind)
                    ivRightmarkMomo.setImageResource(R.drawable.ic_round_hind)
                    ivRightmarkMomo.visibility = View.GONE
                    tvMomoConfirm.setTextColor(ContextCompat.getColor(requireContext(), R.color.hide))
                }
                if(mListBike[idMarker].payment?.banking == false){
                    ivBankingConfirm.setImageResource(R.drawable.ic_banking_hide)
                    ivRightmarkBanking.setImageResource(R.drawable.ic_round_hind)
                    ivRightmarkBanking.visibility = View.GONE
                    tvBankingConfirm.setTextColor(ContextCompat.getColor(requireContext(), R.color.hide))
                }




                val btnConfirmBooking = dialog.findViewById<Button>(R.id.btn_confirm_booking)
                btnConfirmBooking.setOnClickListener{
                    val startLocation = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
                    val endLocation = LatLng(mListBike[idMarker].latitude!!, mListBike[idMarker].longitude!!)
                    val url = getDirectionURL(startLocation, endLocation)
                    Log.d("GoogleMap", "URL : $url")
                    GetDirection(url).execute()
                    updateBookingStatus(mListBike[idMarker].id.toString())
                    updateRenting(mListBike[idMarker].id.toString())
                    checkCompleteRenting(mListBike[idMarker].id.toString(), mListBike[idMarker].name!!,  mListBike[idMarker].photo!!)
                    mEbikeCurrent = mListBike[idMarker].id
                    removeMarker(idMarker)
                    mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                    mIsConfirmed = true
                    mRenting = true
                    dialog.dismiss()

                    val auth = FirebaseAuth.getInstance()
                    mTest.putString("saveIdEBike", mListBike[idMarker].id.toString() )
                    mTest.putString("saveName", mListBike[idMarker].name!!)
                    mTest.putString("savePhoto", mListBike[idMarker].photo!!)
                    mTest.putInt("saveIdMarker", idMarker)
                    mTest.putString("saveIdUser", auth.currentUser!!.uid)
                    mTest.putString("saveRent","true")
                    mTest.putString("saveUrl", url)
                    mTest.apply()

                }




                ivRightmarkCash.setOnClickListener {
                    ivRightmarkCash.setImageResource(R.drawable.ic_right)
                    ivRightmarkMomo.setImageResource(R.drawable.ic_round)
                    ivRightmarkBanking.setImageResource(R.drawable.ic_round)
                }

                ivRightmarkMomo.setOnClickListener {
                    ivRightmarkCash.setImageResource(R.drawable.ic_round)
                    ivRightmarkMomo.setImageResource(R.drawable.ic_right)
                    ivRightmarkBanking.setImageResource(R.drawable.ic_round)
                }
                ivRightmarkBanking.setOnClickListener {
                    ivRightmarkCash.setImageResource(R.drawable.ic_round)
                    ivRightmarkMomo.setImageResource(R.drawable.ic_round)
                    ivRightmarkBanking.setImageResource(R.drawable.ic_right)

                }

            }


            Glide.with(requireActivity())
                .load(mListBike[idMarker].photo)
                .apply(RequestOptions.placeholderOf(R.drawable.img_placeholder1).error(R.drawable.img_placeholder))
                .into(iv_bt_sheet)


           if(mIsConfirmed == false){
               mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
           }

            true
        }


        if(!mIsDetailActivity){
            getLastLocation()
        }else{
            getEBikeLocation()
            getLastLocation()
        }

    }






    private fun removeMarker(idMarker: Int) {
        for(index in mListMarkerEBike?.indices!!){
            if(mListBike[index].id != idMarker){
                mListMarkerEBike?.get(index)?.remove()
            }
        }
    }




    private fun updateBookingStatus(idEBike: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Ebike").child(idEBike)
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("error_update_booking",databaseError.toString() )
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.ref.child("booked").setValue(true)
            }

        })
    }

    private fun updateRenting(idEBike: String) {
        val auth = FirebaseAuth.getInstance()
        val ref = FirebaseDatabase.getInstance().getReference("Renting").child(idEBike)
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("error_update_renting",databaseError.toString() )
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.ref.child("id_user").setValue(auth.currentUser!!.uid)
            }

        })
    }

    private fun checkCompleteRenting(idEBike: String, name: String, photo: String) {
        val auth = FirebaseAuth.getInstance()
        val ref = FirebaseDatabase.getInstance().getReference("Renting").child(idEBike)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.child("complete").getValue() == true){
                    mRenting = false
                    if(mPre.getString("saveRent","false").equals("true")){
                        val idEBikeSave: String = mPre.getString("saveIdEBike","unknown")!!
                        val nameSave: String = mPre.getString("saveName","unknown")!!
                        val photoSave: String = mPre.getString("savePhoto","unknown")!!
                        val idUser: String = mPre.getString("saveIdUser","unknown")!!
                        onCompleteRenting(idEBikeSave, idUser, nameSave, photoSave)
                        updateBookingCancel(idEBikeSave.toInt())
                        updateRentingCancel(idEBikeSave.toInt())

                    }else{
                        onCompleteRenting(idEBike, auth.currentUser!!.uid, name, photo)
                        updateBookingCancel(idEBike.toInt())
                        updateRentingCancel(idEBike.toInt())

                    }

                    mTest.putString("saveRent", "false")
                    mTest.apply()
                }
            }

        })
    }




    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }




    private fun startLocationPermissionRequest() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }




    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (shouldProvideRationale) {
            Log.i(TAG1, "Displaying permission rationale to provide additional context.")
            startLocationPermissionRequest()
        } else {
            Log.i(TAG1, "Requesting permission")

            startLocationPermissionRequest()
        }
    }




    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG1, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> {
                    Log.i(TAG, "User interaction was cancelled.")
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    mMap.isMyLocationEnabled = true
                    // Permission granted.
                    getLastLocation()
                }
                else -> {
                    showSnackbar(R.string.permission_denied_explanation)
                }
            }
        }
    }




    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful && task.result != null) {
                    mLastLocation = task.result
                    Log.d(
                        "location",
                        mLastLocation!!.latitude.toString() + " - " + mLastLocation!!.longitude.toString()
                    )
                    if(!mIsDetailActivity){
                        val myLocation = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
                        val cameraPosition: CameraPosition = CameraPosition.Builder().target(myLocation).zoom(17F).build()
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    }
                } else {
                    Log.w(TAG, "getLastLocation:exception", task.exception)

                }
            }
    }




    private fun getEBikeLocation() {
        val eBikeLocation = LatLng(mEbike?.latitude!!, mEbike?.longitude!!)
        val cameraPosition: CameraPosition = CameraPosition.Builder().target(eBikeLocation).zoom(17F).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }




    private fun showSnackbar(mainTextStringId: Int) {
        Toast.makeText(requireActivity(), getString(mainTextStringId), Toast.LENGTH_LONG).show()
    }




    private fun loadAllEBike() {

        mListMarkerEBike = ArrayList()
        val dataClient: DataClient = APIUtils.getDataEbike()
        val callback: Call<List<Ebike>> = dataClient.loadEBike()
        callback.enqueue(object : Callback<List<Ebike>> {
            override fun onResponse(call: Call<List<Ebike>>, response: Response<List<Ebike>>) {

                val eBikeList: ArrayList<Ebike> = response.body() as ArrayList<Ebike>
                mListBike.addAll(eBikeList)

                for (index in eBikeList.indices) {
                    ratingCal(mListBike[index].id)
                    val latitude: Double = eBikeList[index].latitude as Double
                    val longitude: Double = eBikeList[index].longitude as Double
                    val bikePosition = LatLng(latitude, longitude)
                    val markerOptionBike = MarkerOptions().position(bikePosition)
                        .title(eBikeList[index].name)
                        .icon(mBikeMaker)
                    if(mPre.getString("saveRent","false").equals("true")){
                        val idMarker: Int = mPre.getInt("saveIdMarker", -1)
                        if(eBikeList[index].id==idMarker){
                            val marker = mMap.addMarker(markerOptionBike)
                            marker.tag = eBikeList[index].id.toString()
                            mListMarkerEBike?.add(marker)
                        }
                        val urlSave = mPre.getString("saveUrl", "unknown")
                        GetDirection(urlSave!!).execute()
                    }else{

                        if(!eBikeList[index].booked!!){
                            val marker = mMap.addMarker(markerOptionBike)
                            marker.tag = eBikeList[index].id.toString()
                            mListMarkerEBike?.add(marker)
                            if(eBikeList[index].id == mEbike?.id){
                                marker.title = eBikeList[index].address
                                marker.showInfoWindow()
                            }
                        }

                    }

                }
            }

            override fun onFailure(call: Call<List<Ebike>>, t: Throwable) {
                Toast.makeText(requireActivity(), t.message, Toast.LENGTH_LONG).show()
                Log.d("errotest", t.message)
            }
        })

    }

    private fun ratingCal(id: Int?) {
        var rating: Long = 0
        var maxId = 0
        var finalRating: Long
        val ref = FirebaseDatabase.getInstance().getReference("Ebike").child(id.toString()).child("rating")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                maxId = dataSnapshot.childrenCount.toInt()
                for(index in 1..maxId){
                    val ref2 = FirebaseDatabase.getInstance().getReference("Ebike").child(id.toString()).child("rating").child(index.toString()).child("rate")
                    ref2.addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            rating += dataSnapshot.getValue() as Long
                            Log.d("ahihi", rating.toString())

                        }

                    })

                }


            }

        })



////        var finalRating = rating/maxId

//        return 0f
        Handler().postDelayed({
            finalRating = rating/maxId
            mListBike[id!!].rate = finalRating.toFloat()
            Log.d("ahihi", id.toString() + "-" + finalRating)
        },3000)



    }


    private fun getDirectionURL(origin:LatLng, dest:LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving&key=AIzaSyBU10WQMbL2hr8a-YzD0CxSot_1DVCAWlI"
    }




    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){

        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            val result =  ArrayList<List<LatLng>>()

            try{
                val respObj = Gson().fromJson(data,GoogleMapDTO::class.java)
                val path =  ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }

            return result

        }

        override fun onPostExecute(result: List<List<LatLng>>) {

            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(18f)
                lineoption.color(ContextCompat.getColor(requireContext(), R.color.bg_signin))
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }

    }




     fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }



}
