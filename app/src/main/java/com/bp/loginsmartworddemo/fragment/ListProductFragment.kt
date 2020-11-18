package com.bp.loginsmartworddemo.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bp.loginsmartworddemo.EBikeDetailActivity
import com.bp.loginsmartworddemo.R
import com.bp.loginsmartworddemo.adapter.ListProductAdapter
import com.bp.loginsmartworddemo.model.Ebike
import com.bp.loginsmartworddemo.services.APIUtils
import com.bp.loginsmartworddemo.services.DataClient
import kotlinx.android.synthetic.main.fragment_list_product.view.*
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ListProductFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var mListener: OnListProductFragmentListener? = null
    private lateinit var mResultAdapter: ListProductAdapter
    private var mResultEBikeLoad: ArrayList<Ebike>? = null
    internal var textLength = 0
    private var mIsViewDestroyed = false
    private var mEBikeListTemp = ArrayList<Ebike>()
    private var mEBikeListResult = ArrayList<Ebike>()




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
        mIsViewDestroyed = false
        val view : View = inflater.inflate(R.layout.fragment_list_product, container, false)
        view.img_back_arrow.setOnClickListener{
            onBackArrowPressed("back to home")
        }
        initView(view)
        loadEBike(view)
        eventClick(view)
        eventTouchRC(view)
        return view
    }




    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListProductFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }




    override fun onDetach() {
        super.onDetach()
        mListener = null
    }



    override fun onDestroyView() {
        super.onDestroyView()
        mIsViewDestroyed = true

    }




    private fun initView(view: View) {
        val swLoadEbike = view.findViewById<SwipeRefreshLayout>(R.id.sw_list_ebike)
            swLoadEbike.setColorSchemeResources(R.color.bg_signin)
            swLoadEbike.setOnRefreshListener {
                mResultEBikeLoad = ArrayList()
                mEBikeListResult = ArrayList()
                loadEBike(view)
                Handler().postDelayed({ swLoadEbike.isRefreshing = false },1500)
            }
    }




    private fun onBackArrowPressed(content: String) {
        mListener?.onBackArrowPressed(content)
    }




    interface OnListProductFragmentListener {
        fun onBackArrowPressed(content: String)
    }




    companion object;




    private fun eventTouchRC(view: View) {
        view.rc_list_product.setOnTouchListener { v, _ ->
            val imm: InputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        }
    }




    private fun eventClick(view: View) {
        rcClick(view)
    }




    private fun rcClick(view: View) {
        view.rc_list_product!!.addOnItemTouchListener(
            RecyclerTouchListener(
                this.context!!,
                view.rc_list_product!!,
                object : ClickListener {

                    override fun onClick(view: View, position: Int) {
                        val intent = Intent(requireActivity(), EBikeDetailActivity::class.java)
                        intent.putExtra("ebike", mResultEBikeLoad?.get(position))
                        startActivity(intent)
                    }

                    override fun onLongClick(view: View?, position: Int) {

                    }
                }
            )
        )

    }




    private fun searchProduct(view: View) {

        view.edt_search!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textLength = view.edt_search!!.text.length
                mResultEBikeLoad!!.clear()
                for (i in mEBikeListResult.indices) {
                    if (textLength <= mEBikeListResult[i].brand!!.length) {
                        Log.d("ertyyy", mEBikeListResult[i].brand!!.toLowerCase().trim())
                        if (mEBikeListResult[i].brand!!.toLowerCase().trim().contains(
                                view.edt_search!!.text.toString().toLowerCase().trim { it <= ' ' })
                        ) {
                            mResultEBikeLoad!!.add(mEBikeListResult[i])
                        }
                    }
                }

                view.rc_list_product.layoutManager = LinearLayoutManager(activity)
                mResultAdapter = activity?.let { ListProductAdapter(it, mResultEBikeLoad) }!!
                view.rc_list_product.adapter = mResultAdapter
            }
        })
    }




    private fun populateList(): ArrayList<Ebike> {

        val listProductTemp = ArrayList<Ebike>()
        for(i in mEBikeListTemp.indices){
            listProductTemp.add(mEBikeListTemp[i])
        }
        return listProductTemp

    }




    private fun loadEBike(view: View) {

        mResultEBikeLoad = ArrayList()
        val dataClient: DataClient = APIUtils.getDataEbike()

        if (mEBikeListResult.isNullOrEmpty()){
            val callback: Call<List<Ebike>> = dataClient.loadEBike()
            callback.enqueue(object : Callback<List<Ebike>> {

                override fun onResponse(call: Call<List<Ebike>>, response: Response<List<Ebike>>) {
                    if (mIsViewDestroyed) return
                    val eBikeList: ArrayList<Ebike> = response.body() as ArrayList<Ebike>

                    for (i in eBikeList.indices) {
                        val eBike = Ebike(eBikeList[i].id,
                                    eBikeList[i].name,
                                    eBikeList[i].latitude,
                                    eBikeList[i].longitude,
                                    eBikeList[i].brand,
                                    eBikeList[i].price,
                                    eBikeList[i].address,
                                    eBikeList[i].photo,
                                    eBikeList[i].phone,
                                    eBikeList[i].booked)
                        mResultEBikeLoad?.add(eBike)
                    }

                    mEBikeListTemp = mResultEBikeLoad as ArrayList<Ebike>
                    mEBikeListResult = populateList()
                    view.findViewById<GifImageView>(R.id.img_loading_list).visibility = View.GONE
                    view.rc_list_product.visibility = View.VISIBLE
                    view.rc_list_product.layoutManager = LinearLayoutManager(activity)
                    view.rc_list_product.itemAnimator = DefaultItemAnimator()

                    mResultAdapter = activity?.let { ListProductAdapter(it, mEBikeListResult) }!!
                    view.rc_list_product.adapter = mResultAdapter

                    mResultEBikeLoad = populateList()
                    searchProduct(view)
                }

                override fun onFailure(call: Call<List<Ebike>>, t: Throwable) {
                    Log.d("fail", t.message)
                }
            })
        } else {
            view.findViewById<GifImageView>(R.id.img_loading_list).visibility = View.GONE
            view.rc_list_product.visibility = View.VISIBLE
            view.rc_list_product.layoutManager = LinearLayoutManager(activity)
            view.rc_list_product.itemAnimator = DefaultItemAnimator()
            mResultAdapter = activity?.let { ListProductAdapter(it, mEBikeListResult) }!!
            view.rc_list_product.adapter = mResultAdapter
            mResultEBikeLoad = populateList()
            searchProduct(view)
        }

    }




    interface ClickListener {
        fun onClick(view: View, position: Int)
        fun onLongClick(view: View?, position: Int)
    }




    internal class RecyclerTouchListener(
        context: Context,
        recyclerView: RecyclerView,
        private val clickListener: ClickListener?
    ) : RecyclerView.OnItemTouchListener {

        private val gestureDetector: GestureDetector
        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child))
                    }
                }
            })
        }


        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            val child = rv.findChildViewUnder(e.x, e.y)
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child))
            }
            return false        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }




}
