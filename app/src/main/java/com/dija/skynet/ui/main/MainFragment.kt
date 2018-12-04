package com.dija.skynet.ui.main

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dija.skynet.R
import com.dija.skynet.service.FetchNewsFeedAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val FetchNews by lazy {
        FetchNewsFeedAPI.create()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel

        FetchNews.loadPredictions("1")
            .subscribeOn(Schedulers.io())
            .debounce(10, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    run {

                        val newsList = result

              //         newsList.forEach {
//            //                Toast.makeText(context,it.title,Toast.LENGTH_LONG).show()
              //          }
                    }
                },
                { error ->
                    run {

                    //    if(!AppStatus.getInstance(context).isOnline)
                      //  {
                       //     MessageDialog().showInternetIssueDialog("Network Issue",context.getString(R.string.InternetWarningMessage),context)
                       // }
                    }
                })



    }

}
