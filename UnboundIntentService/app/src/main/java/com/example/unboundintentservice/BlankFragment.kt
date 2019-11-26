package com.example.unboundintentservice


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_blank.*

/**
 * A simple [Fragment] subclass.
 */
class BlankFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerCashbackReceiver()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(cashbackReciver)
    }

    private var cashbackReciver: ClashbackReciver? = null

    private fun registerCashbackReceiver() {
        cashbackReciver = ClashbackReciver()
        val intentFliter = IntentFilter()
        intentFliter.addAction(CASHBACK_INFO)
        requireActivity().registerReceiver(cashbackReciver, intentFliter)
    }


    inner class ClashbackReciver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val cbInfo = intent?.getStringExtra("cashback")
            txtHelloFragment.text = cbInfo
        }
    }
}
