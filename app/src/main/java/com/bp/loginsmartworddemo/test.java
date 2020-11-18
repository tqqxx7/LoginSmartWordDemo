package com.bp.loginsmartworddemo;

public class test {
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
}
