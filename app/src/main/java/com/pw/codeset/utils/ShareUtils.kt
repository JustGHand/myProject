package com.pw.codeset.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast

object ShareUtils {
    fun shareTextToWeChat(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            `package` = "com.tencent.mm" // 指定微信包名
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // 如果手机没装微信，会抛出 ActivityNotFoundException
            Toast.makeText(context, "未安装微信", Toast.LENGTH_SHORT).show()
        }
    }
}