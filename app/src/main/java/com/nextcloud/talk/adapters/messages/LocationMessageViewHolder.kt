package com.nextcloud.talk.adapters.messages

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import autodagger.AutoInjector
import butterknife.BindView
import butterknife.ButterKnife
import com.nextcloud.talk.R
import com.nextcloud.talk.application.NextcloudTalkApplication
import com.nextcloud.talk.models.json.chat.ChatMessage
import com.stfalcon.chatkit.messages.MessageHolders
import java.net.URLEncoder
import javax.inject.Inject

@AutoInjector(NextcloudTalkApplication::class)
class LocationMessageViewHolder(incomingView: View) : MessageHolders
.IncomingTextMessageViewHolder<ChatMessage>(incomingView) {

    private val TAG = "LocationMessageViewHolder"

    var lon : String? = ""
    var lat : String? = ""
    var name : String? = ""
    var id : String? = ""

    @JvmField
    @BindView(R.id.locationText)
    var messageText: TextView? = null

    @JvmField
    @BindView(R.id.webview)
    var webview: WebView? = null

    @JvmField
    @Inject
    var context: Context? = null

    init {
        ButterKnife.bind(
            this,
            itemView
        )
    }

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    override fun onBind(message: ChatMessage) {
        super.onBind(message)
        // if (message.messageType == ChatMessage.MessageType.SINGLE_NC_GEOLOCATION_MESSAGE) {
        //     Log.d(TAG, "handle geolocation here")
        //     messageText!!.text = "geolocation..."
        // }
        if (message.messageParameters != null && message.messageParameters.size > 0) {
            for (key in message.messageParameters.keys) {
                val individualHashMap: Map<String, String> = message.messageParameters[key]!!
                if (individualHashMap["type"] == "geo-location") {
                    lon = individualHashMap["longitude"]
                    lat = individualHashMap["latitude"]
                    name = individualHashMap["name"]
                    id = individualHashMap["id"]
                    Log.d(TAG, "lon $lon lat $lat name $name id $id")
                }
            }
        }


        webview?.settings?.javaScriptEnabled = true

        webview?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    view?.context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    true
                } else {
                    false
                }
            }
        }

        val urlStringBuffer = StringBuffer("file:///android_asset/leafletMapMessagePreview.html")
        urlStringBuffer.append("?mapProviderUrl=" + URLEncoder.encode("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}" +
            ".png"))
        urlStringBuffer.append("&mapProviderAttribution=" + URLEncoder.encode("<a href=\"https://www.openstreetmap" +
            ".org/copyright\">OpenStreetMap</a> contributors"))
        urlStringBuffer.append("&lat=" + URLEncoder.encode(lat))
        urlStringBuffer.append("&lon=" + URLEncoder.encode(lon))
        urlStringBuffer.append("&name=" + URLEncoder.encode(name))


        webview?.loadUrl(urlStringBuffer.toString())
    }
}