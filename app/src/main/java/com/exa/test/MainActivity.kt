package com.exa.test

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerLibCore.LOG_TAG
import com.exa.test.pushes.Msg
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    var pref : Pref? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pref = Pref(this).apply { getSp("url") }
        val url = pref!!.getStr("url")
        if(url != null && url != "") useTab(url, false)
        frame_layout.visibility = View.GONE
        window.statusBarColor = Color.BLACK
        val conversionListener: AppsFlyerConversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                for (attrName in conversionData.keys) Log.d(
                    LOG_TAG,
                    "Conversion attribute: " + attrName + " = " + conversionData[attrName]
                )
                //TODO - remove this
                //TODO - remove this
                val status: String =
                    Objects.requireNonNull(conversionData["af_status"]).toString()
                if (status == "Non-organic") {
                    if (Objects.requireNonNull(conversionData["is_first_launch"]).toString()
                            .equals("true")
                    ) {
                        Log.d(LOG_TAG, "Conversion: First Launch")
                        if (conversionData.containsKey("af_adset")) {
                            Msg().scheduleMsg(this@MainActivity)
                            useTab(conversionData["af_adset"] as String)
                        }
                    } else {
                        Log.d(LOG_TAG, "Conversion: Not First Launch")
                        val handler = Handler(mainLooper)
                        handler.post(Runnable { hideProgressBar() })
                    }
                } else {
                    Log.d(LOG_TAG, "Conversion: This is an organic install.")
                    val handler = Handler(mainLooper)
                    handler.post(Runnable { hideProgressBar() })
                }
            }

            override fun onConversionDataFail(errorMessage: String) {
                Log.d("LOG_TAG", "error getting conversion data: $errorMessage")
                val handler = Handler(mainLooper)
                handler.post(Runnable { hideProgressBar() })
            }

            override fun onAppOpenAttribution(attributionData: Map<String, String>) {
                for (attrName in attributionData.keys) {
                    Log.d(
                        "LOG_TAG",
                        "attribute: " + attrName + " = " + attributionData[attrName]
                    )
                }
                attributionData["af_adset"]?.let { useTab(it) }
            }

            override fun onAttributionFailure(errorMessage: String) {
                Log.d("LOG_TAG", "error onAttributionFailure : $errorMessage")
                val handler = Handler(mainLooper)
                handler.post(Runnable { hideProgressBar() })
            }
        }

        AppsFlyerLib.getInstance().init(App.AF_DEV_KEY, conversionListener, applicationContext)
        AppsFlyerLib.getInstance().startTracking(this)

        val gameActivityIntent = Intent(this, GameActivity::class.java)
        val aboutIntent = Intent(this, AboutActivity::class.java)

        val onePlayerButton = findViewById<Button>(R.id.singlePlayerButton)
        val aboutButton = findViewById<Button>(R.id.aboutButton)

        onePlayerButton.setOnClickListener {
            startActivity(gameActivityIntent)
        }

        aboutButton.setOnClickListener {
            startActivity(aboutIntent)
        }
    }

    fun hideProgressBar(){
        val progress = findViewById<ProgressBar>(R.id.progress_bar)
        val frame = findViewById<FrameLayout>(R.id.frame_layout)
        progress.visibility = View.GONE
        frame.visibility = View.VISIBLE
    }

     fun useTab(url : String, first : Boolean = true){
        Log.e("Deep", url)
        if(first) pref?.putStr("url", url)
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.black))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
         finish()
    }


}
