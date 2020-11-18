package com.bp.loginsmartworddemo.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bp.loginsmartworddemo.R
import com.bp.loginsmartworddemo.model.User

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
import kotlinx.android.synthetic.main.toolbar_edit_profile.view.*
import java.io.IOException
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EditProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var mListener: OnEditProfileFragmentListener? = null
    private var mUser: User? = null
    private var mPath: String? = null
    private val mREQUEST = 71
    private var mFilePath: Uri? = null
    private var mFirebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    var dialog : Dialog? = null
    private var mAddressComfirmed: String? = null

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
        val view : View = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        dialog = Dialog(requireActivity())

        view.img_back_edit_profile.setOnClickListener{
            mUser?.let { it1 -> onBackPressed(it1, "update_changed") }
        }
        view.img_edit_profile.setOnClickListener{
            imagePicker()
        }

        view.img_choose_address.setOnClickListener{
            onChooseAddressPressed("move to choose address")
        }

        mFirebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        mUser = arguments!!.getSerializable("user") as User?
        mAddressComfirmed = arguments!!.getString("address")

        loadInfoForEdit(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        if(!mAddressComfirmed.isNullOrEmpty()){
            btn_edit_profile.isEnabled = true
            btn_edit_profile.setBackgroundResource(R.drawable.rounded_gradient_button)
        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditProfileFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    fun onBackPressed(user: User, code: String) {
        mListener?.onBackPressed(user, code)
    }

    private fun onChooseAddressPressed(content: String){
        mListener?.onChooseAddressPressed(content)
    }
   
    
    interface OnEditProfileFragmentListener {
        fun onBackPressed(user: User, code: String)
        fun onChooseAddressPressed(content: String)
    }

    companion object;

    private fun loadInfoForEdit(view: View) {
        if(mUser!!.getAddress().equals("null")){
            view.edt_address_profile.text = "n/a"
        }else{
            view.edt_address_profile.text = mUser!!.getAddress()
        }
        view.edt_fullname_profile.setText(mUser!!.getFullName())
        view.edt_phone_profile.setText(mUser!!.getPhone())
        view.edt_email_profile.text = mUser!!.getEmail()

        if (!mAddressComfirmed.isNullOrEmpty()){
            view.edt_address_profile.text = mAddressComfirmed

        }
        Glide.with(requireActivity())
            .load(mUser?.getPhoto())
            .apply(RequestOptions.placeholderOf(R.drawable.img_placeholder1).error(R.drawable.img_placeholder))
            .into(view.img_edit_profile)

        view.btn_edit_profile.setOnClickListener{
            dialog?.let { it1 -> showDiaglogLoading(it1) }
            uploadImage()
        }
        view.btn_edit_profile.isEnabled = false
        view.btn_edit_profile.setBackgroundResource(R.drawable.rounded_disable)

        view.edt_fullname_profile.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                view.btn_edit_profile.isEnabled = true
                view.btn_edit_profile.setBackgroundResource(R.drawable.rounded1)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        view.edt_address_profile.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                    view.btn_edit_profile.isEnabled = true
                    view.btn_edit_profile.setBackgroundResource(R.drawable.rounded1)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        view.edt_phone_profile.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                view.btn_edit_profile.isEnabled = true
                view.btn_edit_profile.setBackgroundResource(R.drawable.rounded1)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
    }

    private fun eventUpdateInfo() {
        val fullName: String = edt_fullname_profile.text.toString()
        val phone: String = edt_phone_profile.text.toString()
        val address: String = edt_address_profile.text.toString()
        var pathPhoto : String
        if(fullName.isNotEmpty()){

            val auth = FirebaseAuth.getInstance()
            val ref = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser!!.uid)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    pathPhoto = if(mPath != null){
                        mPath.toString()
                    }else{
                        mUser?.getPhoto().toString()
                    }
                    dataSnapshot.ref.child("fullName").setValue(fullName)
                    dataSnapshot.ref.child("address").setValue(address)
                    dataSnapshot.ref.child("phone").setValue(phone)
                    dataSnapshot.ref.child("photo").setValue(pathPhoto)

                    mUser?.setFullName(fullName)
                    mUser?.setPhone(phone)
                    mUser?.setAddress(address)
                    mUser?.setPhoto(pathPhoto)

                    val latLng: LatLng? = getLocationFromAddress(address)
                    dataSnapshot.ref.child("latitude").setValue(latLng?.latitude)
                    dataSnapshot.ref.child("longitude").setValue(latLng?.longitude)

                    Toast.makeText(requireActivity(), "Updated", Toast.LENGTH_LONG).show()
                    val pre : SharedPreferences = activity!!.getSharedPreferences("user_email",
                        Context.MODE_PRIVATE
                    )
                    val edit : SharedPreferences.Editor = pre.edit()
                    edit.putString("fullname", fullName)
                    edit.putString("address", address)
                    edit.putString("phone", phone)
                    edit.putString("photo", pathPhoto)
                    edit.apply()
                    dialog?.dismiss()
                    mUser?.let { it1 -> onBackPressed(it1, "update_changed") }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("User", databaseError.message)
                }
            })
        }else{
            Toast.makeText(activity, R.string.plesefillinyourfullname, Toast.LENGTH_LONG).show()
        }
    }

    private fun showDiaglogLoading(dialog: Dialog) {
        dialog.setContentView(R.layout.update_loading_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun getLocationFromAddress(addressString: String): LatLng? {
        val coder = Geocoder(requireContext())
        val address: List<Address>
        val p1 :LatLng

        address = coder.getFromLocationName(addressString, 5)
        if(address == null){
            return null
        }
        val location: Address = address[0]
        p1 = LatLng(location.latitude, location.longitude)
        return p1
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mREQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            mFilePath = data.data
            try {
                btn_edit_profile.isEnabled = true
                btn_edit_profile.setBackgroundResource(R.drawable.rounded1)
                Glide.with(requireActivity()).load(mFilePath).into(img_edit_profile)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if(mFilePath != null){
            mPath = UUID.randomUUID().toString()
            val ref = storageReference?.child("uploads/" + mPath.toString())
            Log.d("realpath", mPath.toString())
            ref?.putFile(mFilePath!!)?.addOnSuccessListener { taskSnapshot ->
                val test = taskSnapshot.storage.downloadUrl
                test.addOnCompleteListener {
                    val downloadURL: String = "https://" + it.result!!.encodedAuthority + it.result!!.encodedPath + "?alt=media&token="+ it.result!!.getQueryParameters("token")[0]
                    Log.d("downloadurl", downloadURL)
                    mPath = downloadURL
                    if(mPath!= null){
                        eventUpdateInfo()
                    }

                }
            }?.addOnFailureListener { e ->
                Toast.makeText(activity, "Image Uploading Failed " + e.message, Toast.LENGTH_SHORT).show()
            }
        }else{
            eventUpdateInfo()
        }
    }

    private fun imagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), mREQUEST)
    }
}
