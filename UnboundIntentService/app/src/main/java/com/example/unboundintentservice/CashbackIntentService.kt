package com.example.unboundintentservice

import android.app.IntentService
import android.content.Intent

/*Intent Service được sử dụng để thực hiện các nhiệm vụ một lần duy nhất,
nghĩa là khi nhiệm vụ hoàn thành dịch vụ tự hủy.

Intent Service được khởi động bởi gọi startService().
IntentService gọi một cách không tường minh phương thức stopself() để hủy.
Intent Service độc lập với thành phần đã khởi động nó.
*/


const val CASHBACK_INFO = "cashback_info"
class CashbackIntentService : IntentService("CashbackIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val cbCategory = intent!!.getStringExtra("cashback_cat")
        val cbinfo = getCashbackInfo(cbCategory)
        sendCashbackInfoToClient(cbinfo)
    }

    private fun getCashbackInfo(cbcat: String): String {
        return when (cbcat) {
            "electronics" -> "Upto 20% cashback on electronics"
            "fashion" -> "Upto 60% cashbak on all fashion items"
            else -> "All other categories except fashion and electronics, flat 30% cashback"
        }
    }

    private fun sendCashbackInfoToClient(msg: String) {
        val intent = Intent()
        intent.action = CASHBACK_INFO
        intent.putExtra("cashback", msg)
        sendBroadcast(intent)

    }
}