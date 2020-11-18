package com.bp.loginsmartworddemo.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.bp.loginsmartworddemo.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"


class RatingFragment : Fragment() {
    private var mIdBike: String? = null
    private var mIdUser: String? = null
    private var mListener: OnRatingFragmentListener? = null
    private var mRating: Int? = null
    private var mMaxId: Long? = null
    private var mFeedback: String? = null
    private var edtFeedback: EditText? = null
    private var mName: String? = null
    private var mPhoto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mIdBike = it.getString(ARG_PARAM1)
            mIdUser = it.getString(ARG_PARAM2)
            mName = it.getString(ARG_PARAM3)
            mPhoto = it.getString(ARG_PARAM4)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_rating, container, false)
        val ivBack = view.findViewById<ImageView>(R.id.img_back_rating)
        val tvSkipFeedback = view.findViewById<TextView>(R.id.TextViewSkipFeedback)
        ivBack.setOnClickListener {
            onButtonBackPressed("back to home")
        }

        tvSkipFeedback.setOnClickListener{
            onButtonBackPressed("back to home")
        }

        val btnSubmitFeedback = view.findViewById<TextView>(R.id.btn_submit_feedback)
        btnSubmitFeedback.setOnClickListener {
            updateRating()
        }

        initView(view)

        return view
    }

    private fun updateRating() {
        val auth = FirebaseAuth.getInstance()
        val ref = mIdBike?.let { FirebaseDatabase.getInstance().getReference("Ebike").child(it).child("rating") }
        ref?.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("error_update_renting",databaseError.toString() )
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mMaxId = dataSnapshot.childrenCount
                mFeedback = edtFeedback?.text.toString()
                dataSnapshot.ref.child((mMaxId!! +1).toString()).child("rate").setValue(mRating)
                dataSnapshot.ref.child((mMaxId!! +1).toString()).child("id_user").setValue(auth.currentUser!!.uid)
                dataSnapshot.ref.child((mMaxId!! +1).toString()).child("feedback").setValue(mFeedback)
                showDialogSuccess()
                onButtonBackPressed("back to home")
            }

        })
    }


    fun onButtonBackPressed(content: String) {
        mListener?.onBackPressed(content)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRatingFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    interface OnRatingFragmentListener {
        fun onBackPressed(content: String)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String, param4: String) =
            RatingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                    putString(ARG_PARAM4, param4)
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun initView(view: View) {
        val iv1Star = view.findViewById<ImageView>(R.id.img_1_star)
        val iv2Star = view.findViewById<ImageView>(R.id.img_2_star)
        val iv3Star = view.findViewById<ImageView>(R.id.img_3_star)
        val iv4Star = view.findViewById<ImageView>(R.id.img_4_star)
        val iv5Star = view.findViewById<ImageView>(R.id.img_5_star)

        iv1Star.setOnClickListener {
            iv1Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv2Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.textColor))
            iv3Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.textColor))
            iv4Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.textColor))
            iv5Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.textColor))
            mRating = 1
        }

        iv2Star.setOnClickListener {
            iv1Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv2Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv3Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.textColor))
            iv4Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.textColor))
            iv5Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.textColor))
            mRating = 2
        }

        iv3Star.setOnClickListener {
            iv1Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv2Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv3Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv4Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.textColor))
            iv5Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.textColor))
            mRating = 3
        }

        iv4Star.setOnClickListener {
            iv1Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv2Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv3Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv4Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv5Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.textColor))
            mRating = 4
        }

        iv5Star.setOnClickListener {
            iv1Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv2Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv3Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv4Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            iv5Star.setColorFilter(ContextCompat.getColor(requireContext(),R.color.star))
            mRating = 5
        }

        edtFeedback = view.findViewById(R.id.edt_feedback)

        val tvRateName = view.findViewById<TextView>(R.id.tv_rate_name)
        tvRateName.text = "Rate your last rent with $mName"
        val ivEbikePhoto = view.findViewById<CircleImageView>(R.id.img_ebike_photo)
        Glide.with(requireActivity())
            .load(mPhoto)
            .apply(RequestOptions.placeholderOf(R.drawable.img_placeholder1).error(R.drawable.img_placeholder))
            .into(ivEbikePhoto)


    }




    private fun showDialogSuccess() {
        val dialog = activity?.let { Dialog(it) }
        dialog?.setContentView(R.layout.feedback_success)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val handler = Handler()
        val runnable = Runnable {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }

        handler.postDelayed(runnable, 2000)
        dialog.setOnDismissListener(
            DialogInterface.OnDismissListener {
                handler.removeCallbacks(runnable)

            }
        )
    }
}
