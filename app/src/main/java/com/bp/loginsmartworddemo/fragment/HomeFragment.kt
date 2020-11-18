package com.bp.loginsmartworddemo.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bp.loginsmartworddemo.R
import com.bp.loginsmartworddemo.model.User
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_home.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var mlistener: OnHomeFragmentListener? = null
    private var mUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            mUser = it.getSerializable("user") as User?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view : View = inflater.inflate(R.layout.fragment_home, container, false)
        mUser = arguments!!.getSerializable("user") as User?
        loadUserInfo(view)

        view.btnNext.setOnClickListener{
            onButtonNextPressed("next")
        }
        view.ln_map.setOnClickListener{
            onLinearMapPressed("map")
        }
        view.ln_info_home.setOnClickListener{
            onProfilePressed("profile")
        }

        return view
    }



    private fun onButtonNextPressed(content: String) {
        mlistener?.onNextClickListener(content)
    }
    private fun onLinearMapPressed(content: String) {
        mlistener?.onMapClickListener(content)
    }

    private fun onProfilePressed(content: String) {
        mlistener?.onProfileClickListener(content)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHomeFragmentListener) {
            mlistener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mlistener = null
    }




    interface OnHomeFragmentListener {

        fun onNextClickListener(content: String)
        fun onMapClickListener(content: String)
        fun onProfileClickListener(content: String)
    }





    companion object;


    private fun loadUserInfo(view : View) {
        view.tv_name_user.text = mUser!!.getFullName()
        view.tv_email_user.text = mUser!!.getEmail()
        Glide.with(requireContext())
            .load(mUser!!.getPhoto())
            .apply(RequestOptions.placeholderOf(R.drawable.img_placeholder1).error(R.drawable.img_placeholder))
            .into(view.img_profile_user)
    }
}
