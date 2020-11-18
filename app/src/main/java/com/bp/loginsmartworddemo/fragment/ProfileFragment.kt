package com.bp.loginsmartworddemo.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bp.loginsmartworddemo.R
import com.bp.loginsmartworddemo.model.User
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.toolbar_profile.view.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnProfileFragmentManager? = null
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            user = it.getSerializable("user") as User?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        view.img_logout_profile.setOnClickListener{
            onButtonSignOutPressed("sign out")
        }
        view.img_back_profile.setOnClickListener{
            onBackPressed("back to home")
        }
        view.btn_edit_profile.setOnClickListener{
            onEditPressed("move to edit")
        }
        user = arguments!!.getSerializable("user") as User?
        loadUserInfo(view)
        return view
    }


    private fun onButtonSignOutPressed(content: String) {
        listener?.onSignoutClickListener(content)
    }

    private fun onBackPressed(content: String) {
        listener?.onProfileBackPressed(content)
    }
    private fun onEditPressed(content: String) {
        listener?.onEditProfilePressed(content)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnProfileFragmentManager) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnProfileFragmentManager {
        fun onSignoutClickListener(content: String)
        fun onProfileBackPressed(content: String)
        fun onEditProfilePressed(content: String)
    }

    companion object;

    private fun loadUserInfo(view: View) {
        view.tv_email_profile.text = user!!.getEmail()
        view.tv_name_profile.text = user!!.getFullName()
        view.tv_phone_profile.text = user!!.getPhone()
        if(user!!.getAddress().equals("null")){
            view.tv_address_profile.text = "n/a"
        }else{
            view.tv_address_profile.text = user!!.getAddress()
        }
        Glide.with(requireActivity())
            .load(user!!.getPhoto())
            .apply(RequestOptions.placeholderOf(R.drawable.img_placeholder1).error(R.drawable.img_placeholder))
            .into(view.img_profile)
    }
}
