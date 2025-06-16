package com.psssum.testsdk

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.FileNotFoundException


class TestUpmobActivity : AppCompatActivity() {
    private lateinit var webInterFace : WebAppInterface
    private val SELECT_PHOTO = 1
    //    private lateinit var handlePathOz: HandlePathOz
    private lateinit var webview : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        setAnimation()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_layout)
        val token = intent.getStringExtra(Constants.TOKEN)
        val device_id = intent.getStringExtra(Constants.DEVICE_ID)
        val api_key = intent.getStringExtra(Constants.API_KEY)
        val uniq_user_id = intent.getStringExtra(Constants.USER_ID)
        webview = findViewById<WebView>(R.id.webview)
        webview.loadUrl("https://app-coins.ru/?google_user_id=115617333779596885662&device_id=cec6222f97c593cf&api_key=smoozi")

//        webview.loadUrl("https://app-coins.ru/tasks?device_id=gsagasdag23g2gewag&token_google=eyJhbGciOiJSUzI1NiIsImtpZCI6ImFiODYxNGZmNjI4OTNiYWRjZTVhYTc5YTc3MDNiNTk2NjY1ZDI0NzgiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI3OTIzNjc1MDQyMjctc2NiMGhsajhzYzFnc2tvZnRub3ZjdDFyNWRwa3ZmMG4uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI3OTIzNjc1MDQyMjctMjYzNWhtaDZxZjQ0NmZpNGJrcGxscDQ0YjNmMm9waTAuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTU2MTczMzM3Nzk1OTY4ODU2NjIiLCJlbWFpbCI6ImR1ZmxydUBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Ik1vdW50YWluIEhlYWRzIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FDZzhvY0xNQUVwZ01DQnVmR2RNS2Y1LTludjUzb3NoMEtFMnN0aDMxbXA3aTJ2SmsxSU13UmpaMnc9czk2LWMiLCJnaXZlbl9uYW1lIjoiTW91bnRhaW4iLCJmYW1pbHlfbmFtZSI6IkhlYWRzIiwiaWF0IjoxNzM1NDg0NTIxLCJleHAiOjE3MzU0ODgxMjF9.pc8fVLg3G8o2OsEAxHBUywzGSbnFluQPlHDQhTyHVXXNQahlsVQXtOCAz8XX7uLPmLww3xobsFadh2HenVQXjUfE7TjEA0zRbYkNrLv3V0G9kbKy9vuoV67tfNxF_xG1dq1X7z9PjxwThWe1K5WdmQQmJQc-P6FhAnj6j2TfUKQzWh2hsgS0XTxbZTfxWupE99Ci1zLqR_fq9NflW3ZdFEirMQ2twxOo_YYTtWdUfw6IhpzOIVaFXUzS8iKHvUq8D570BFQZ84JaMSVdd_mPdQph2JJBZy2sl9mJV3kHXBe07smV-eHivvf2ckkOktC_udMaKsYBiGyHT2wrylGFPw")
//        webview.loadUrl("https://app-coins.ru/tasks?device_id=$device_id&token_google=$token&api_key=$api_key&uniq_user_id=$uniq_user_id&testReactJS=1&bundle=${applicationContext.packageName}")
        val webSettings: WebSettings = webview.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        webview.webViewClient = MyWebViewClient()
        webInterFace = WebAppInterface(this)
        webview.addJavascriptInterface(webInterFace, "Android")
        webSettings.javaScriptCanOpenWindowsAutomatically = false
//        handlePathOz = HandlePathOz(this, object : HandlePathOzListener.SingleUri{
//            override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
//                val file = File(pathOz.path)
//                val value: String = Base64.encodeToString(file.readBytes(), Base64.DEFAULT)
//                runOnUiThread {
//                    webview.loadUrl("javascript:handleImage('data:image/png;base64,${value}')")
//                }
//            }
//        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_PHOTO) {
            try {
                val imageUri: Uri? = data?.data
                imageUri?.let {
                    //webview.loadUrl("javascript:onResume()")
                    //handlePathOz.getRealPath(imageUri)
                }

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        webInterFace.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    class WebAppInterface internal constructor(c: Activity) {
        var mContext: Activity
        private val READ_STORAGE_PERMISSION_REQUEST_CODE = 3

        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ){
            when (requestCode) {
                READ_STORAGE_PERMISSION_REQUEST_CODE -> {
                    // If request is cancelled, the result arrays are empty.
                    if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        pickPhoto()
                    }
                    return
                } else -> {
                // Ignore all other requests.
            }
            }
        }
        @JavascriptInterface
        fun checkInstalledNew(packagename: String): Boolean {
            return mContext.packageManager!!.getLaunchIntentForPackage(packagename) != null
        }
        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        }
        @JavascriptInterface
        fun isAppInstalled(packagename: String): Boolean {
            try {
                mContext.packageManager.getPackageInfo(packagename, 0)
                return true
            } catch (e: Exception) {
                return false
            }
        }
        @JavascriptInterface
        fun openUrl(url: String){

            Log.d("mainActivty", "url =" + url)
            val intent = Intent("android.intent.action.VIEW", Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            mContext.startActivity(intent)
        }
        @JavascriptInterface
        fun openApp(packagename: String) {
            try {
                val i = mContext.packageManager!!.getLaunchIntentForPackage(packagename)
                mContext.startActivity(i)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        @JavascriptInterface
        fun openAppWithParams(packagename: String, google_user_id : String, order_id : String) {
            try {
                val i = mContext.packageManager!!.getLaunchIntentForPackage(packagename)
                i!!.putExtra("google_user_id", google_user_id)
                i!!.putExtra("order_id", order_id)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                mContext.startActivity(i)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        @JavascriptInterface
        fun copyId(id: String, text: String){
            val clipboard = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", id)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show()
        }
        @JavascriptInterface
        fun registrationFailed(desc: String){
            if (Constants.onFailListener != null){
                Constants.onFailListener!!.onError(desc)
                mContext.finish()
            }
        }
        @JavascriptInterface
        fun finish(){
            mContext.finish()
        }
        @JavascriptInterface
        fun showReviewDialog() {
//            if (!isRated(mContext)) {
//                setRated(mContext)
//                val manager = ReviewManagerFactory.create(mContext)
//                //val manager = FakeReviewManager(requireContext()) //TEST MODE!!!
//                val request = manager.requestReviewFlow()
//                request.addOnCompleteListener { r ->
//                    if (r.isSuccessful) {
//                        val reviewInfo = r.result
//                        val flow = manager.launchReviewFlow(mContext, reviewInfo)
//                        flow.addOnCompleteListener { _ ->
//
//                        }
//                    }
//                }
//            }
        }
        private val IS_RATE = "IS_RATE"
        fun isRated(ctx: Context?): Boolean {
            val sPref = PreferenceManager
                .getDefaultSharedPreferences(ctx)
            return sPref.getBoolean(IS_RATE, false)
        }

        fun setRated(ctx: Context?) {
            val sPref = PreferenceManager
                .getDefaultSharedPreferences(ctx)
            val ed = sPref.edit()
            ed.putBoolean(IS_RATE, true)
            ed.apply()
        }
        @JavascriptInterface
        fun getStringPref(key : String): String {
            val sPref = PreferenceManager
                .getDefaultSharedPreferences(mContext)
            return sPref.getString(key, "").toString()
        }
        @JavascriptInterface
        fun setStringPref(text: String, key : String) {
            val sPref = PreferenceManager
                .getDefaultSharedPreferences(mContext)
            val ed = sPref.edit()
            ed.putString(key, text)
            ed.apply()
        }
        @JavascriptInterface
        fun checkPickPhoto(){
            if (checkPermissionForReadExtertalStorage()) {
                pickPhoto()
            } else {
                requestPermissionForReadExtertalStorage()
            }
        }
        private fun checkPermissionForReadExtertalStorage(): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val result: Int = mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                val resultWrite: Int = mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                return result == PackageManager.PERMISSION_GRANTED && resultWrite == PackageManager.PERMISSION_GRANTED
            }
            return false
        }
        private fun pickPhoto() {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            mContext.startActivityForResult(photoPickerIntent, 1)
        }
        @SuppressLint("NewApi")
        @Throws(Exception::class)
        fun requestPermissionForReadExtertalStorage() {
            try {
                mContext.requestPermissions(
                    arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_REQUEST_CODE
                )
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
        init {
            mContext = c


        }
    }


    override fun onResume() {
        super.onResume()
        webview.loadUrl("javascript:onResume()")
    }
    private class MyWebViewClient : WebViewClient() {
        //HERE IS THE MAIN CHANGE.
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return false
        }
    }
    private fun setAnimation(){

        if (Build.VERSION.SDK_INT > 20) {
            val fadeAnimaton = android.transition.Fade()
            fadeAnimaton.setDuration(200)
            fadeAnimaton.setInterpolator(DecelerateInterpolator())
            getWindow().setExitTransition(fadeAnimaton)
            getWindow().setEnterTransition(fadeAnimaton)
        }
    }
}