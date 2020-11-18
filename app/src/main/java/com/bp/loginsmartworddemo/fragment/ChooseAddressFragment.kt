package com.bp.loginsmartworddemo.fragment

import ListMapAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import com.bp.loginsmartworddemo.R
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bp.loginsmartworddemo.model.AddressGeometry
import com.bp.loginsmartworddemo.model.AddressLocation
import com.bp.loginsmartworddemo.model.AddressTextSearchModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_choose_address.*
import kotlinx.android.synthetic.main.fragment_choose_address.view.*



private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ChooseAddressFragment : Fragment(), OnMapReadyCallback {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnMapFragmentListener? = null
    private lateinit var mMap: GoogleMap
    private var mAddressSelected: LatLng = LatLng(0.2, 0.2)
    private var mMarker: Marker? = null
    private var mMarkerOption: MarkerOptions? = null
    private var mMarkerPoint: Marker? = null
    private var mMarkerPointOption: MarkerOptions? = null
    private var mAddressConfirm: String? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null




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
        val view: View = inflater.inflate(R.layout.fragment_choose_address, container, false)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mapFragment = childFragmentManager.findFragmentById(R.id.mapViewAddress) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        searchMap(view)

        view.btn_confirm_address.setOnClickListener{
            onConfirmAddress(mAddressConfirm!!)
        }
        view.img_address_back_arrow.setOnClickListener{
            onButtonBackPressed("back to edit")
        }

        return view

    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()

        if (checkPermissions()) {
            getLastLocation()
        } else {
            requestPermissions()
        }

    }




    private fun onButtonBackPressed(string: String) {
        listener?.onAddressBackPressed(string)
    }




    private fun onConfirmAddress(address: String) {
        listener?.onComfirmPressed(address)
    }




    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnMapFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }

    }




    override fun onDetach() {
        super.onDetach()
        listener = null
    }




    interface OnMapFragmentListener {
        fun onAddressBackPressed(content: String)
        fun onComfirmPressed(address: String)
    }




    companion object {
        private const val TAG1 = "LocationProvider"
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        var mapFragment: SupportMapFragment? = null
        val TAG: String = MapFragment::class.java.simpleName
    }




    @SuppressLint("PrivateResource")
    private fun searchMap(view: View) {

        val adapter = ListMapAdapter(requireContext())
        view.act_search_address.setAdapter(adapter)
        view.act_search_address.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->

            val selectedItem = parent.getItemAtPosition(position)
            val addressClick: AddressTextSearchModel = selectedItem as AddressTextSearchModel
            view.findViewById<AutoCompleteTextView>(R.id.act_search_address).setText(addressClick.formattedAddress)
            mAddressConfirm = addressClick.formattedAddress

            val addressGeometry: AddressGeometry? = addressClick.geometry
            val addressLocation: AddressLocation? = addressGeometry?.location

            mAddressSelected = LatLng(addressLocation!!.lat.toDouble(), addressLocation.lng.toDouble())
            val cameraPosition: CameraPosition = CameraPosition.Builder().target(mAddressSelected).zoom(15F).build()
            mMarkerOption = MarkerOptions().position(mAddressSelected).title(addressClick.name)
            mMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gas_station))
            mMarker = mMap.addMarker(mMarkerOption)
            mMarker?.showInfoWindow()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        }

    }





    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {

        mMap = googleMap!!
        if(checkPermissions()){
            mMap.isMyLocationEnabled = true
        }
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.setOnMapLongClickListener {
            mMarkerPoint?.remove()
            val address = getAddressFromLocation(it)
            mMarkerPointOption = MarkerOptions().position(it).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            mMarkerPoint = mMap.addMarker(mMarkerPointOption)
            mMarkerPoint?.showInfoWindow()
            act_search_address.setText(address)
            val cameraPosition: CameraPosition = CameraPosition.Builder().target(it).zoom(15F).build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            mAddressConfirm = address
        }

    }




    private fun checkPermissions(): Boolean {

        val permissionState = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED

    }




    // When in AppCompatActivity, you should use ActivityCompat.requestPermissions;
    // When in android.support.v4.app.Fragment, you should use simply requestPermissions (this is an instance method of android.support.v4.app.Fragment)
    // If you call ActivityCompat.requestPermissions in a fragment, the onRequestPermissionsResult callback is called on the activity and not the fragment.




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

                    val myLocation = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
                    val address = getAddressFromLocation(myLocation)
                    val cameraPosition: CameraPosition = CameraPosition.Builder().target(myLocation).zoom(17F).build()
                    act_search_address.setText(address)
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    mAddressConfirm = address
                } else {
                    Log.w(TAG, "getLastLocation:exception", task.exception)

                }
            }

    }




    private fun showSnackbar(mainTextStringId: Int) {
        Toast.makeText(requireActivity(), getString(mainTextStringId), Toast.LENGTH_LONG).show()
    }




    private fun getAddressFromLocation(location: LatLng): String? {
        val coder = Geocoder(requireContext())
        val addresses: List<Address>
        addresses = coder.getFromLocation(location.latitude, location.longitude, 1)
        return addresses[0].getAddressLine(0)
    }




}
