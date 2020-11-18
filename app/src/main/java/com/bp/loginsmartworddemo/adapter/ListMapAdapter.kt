import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.bp.loginsmartworddemo.R
import com.bp.loginsmartworddemo.model.AddressTextSearchModel
import com.bp.loginsmartworddemo.model.AddressTextSearchResponse
import com.bp.loginsmartworddemo.services.APIUtils
import com.bp.loginsmartworddemo.services.DataClient
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class ListMapAdapter(context: Context) :
    ArrayAdapter<AddressTextSearchModel?>(context, R.layout.item_autocomplete, R.id.tv_name_map) {
    var mData = ArrayList<AddressTextSearchModel?>()
    var searchSubject: PublishSubject<String> = PublishSubject.create()
    private var searchDisposable: Disposable? = null
    private var API_KEY: String = "AIzaSyBU10WQMbL2hr8a-YzD0CxSot_1DVCAWlI"

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        if (convertView == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_autocomplete, parent, false)
        }

        val item: AddressTextSearchModel? = mData[position]
        if (item != null) {
            val tvMapName = view?.findViewById<View>(R.id.tv_name_map) as TextView
            tvMapName.text = item.formattedAddress
        }
        return view!!
    }

    override fun getItem(position: Int): AddressTextSearchModel? {
        return mData[position]
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(searchContent: CharSequence?): FilterResults {
                val results = FilterResults()
                val filteredData = ArrayList<AddressTextSearchModel?>()
                if (!searchContent.isNullOrEmpty()) {
                    searchSubject.onNext(searchContent.toString())
                }
                results.values = filteredData
                results.count = filteredData.size
                return results
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {}

        }
    }

    init {
        searchDisposable = searchSubject
            .debounce(300, TimeUnit.MILLISECONDS)
            .map { it.trim() }
            .switchMap { searchValue ->
                return@switchMap Observable.create<AddressTextSearchResponse> { addressResponse ->
                    if (searchValue.isNotEmpty()) {
                        val dataClient: DataClient = APIUtils.getDataMap()
                        val callback: Call<AddressTextSearchResponse> =
                            dataClient.loadAddressResponse(searchValue, API_KEY)
                        callback.enqueue(object : Callback<AddressTextSearchResponse> {
                            override fun onResponse(
                                call: Call<AddressTextSearchResponse>,
                                response: Response<AddressTextSearchResponse>
                            ) {
                                addressResponse.onNext(response.body() as AddressTextSearchResponse)
                                Log.d("response", response.body().toString())
                            }

                            override fun onFailure(call: Call<AddressTextSearchResponse>, t: Throwable) {
                                Log.d("fail", t.message ?: "")
                            }

                        })
                    } else {
                        addressResponse.onNext(AddressTextSearchResponse())
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ textSearchResponse ->
                if ((textSearchResponse.results?.size ?: 0) > 0) {
                    mData.clear()
                    textSearchResponse.results?.forEach {
                        mData.add(it)
                    }
                    notifyDataSetChanged()
                } else {
                    mData.clear()
                    notifyDataSetChanged()
                }
            }, {
            })
    }

}