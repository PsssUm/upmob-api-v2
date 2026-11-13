package com.psssum.upmob

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

/**
 * Выбор файла для полей <input type="file"> на страницах в окне SDK — например, скриншот к заданию
 * (отзыв, подписка). Без WebChromeClient.onShowFileChooser WebView молча игнорирует нажатие на такое поле.
 *
 * Прежний способ не тронут: методы Android.checkPickPhoto и т. п. остаются как были — ими пользуются
 * страницы, рассчитанные на старые версии SDK.
 */
internal class FileChooser(private val activity: Activity) {

    /** Поле ждёт ответа: каждый такой ответ нужно отдать WebView ровно один раз */
    private var pending: ValueCallback<Array<Uri>>? = null

    val client: WebChromeClient = object : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            if (filePathCallback == null) return false
            // прошлый выбор остался без ответа — отменить его, иначе поле больше не откроется
            pending?.onReceiveValue(null)
            pending = filePathCallback
            return try {
                activity.startActivityForResult(chooserIntent(fileChooserParams), REQUEST_CODE)
                true
            } catch (e: ActivityNotFoundException) {
                // на телефоне нечем выбрать файл — отменить выбор, как велит документация WebView
                pending = null
                filePathCallback.onReceiveValue(null)
                true
            }
        }
    }

    /** Ответ окна выбора файла. false — это ответ не на наш запрос. */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode != REQUEST_CODE) return false
        val callback = pending ?: return true
        pending = null
        callback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
        return true
    }

    /** Окно выбора по атрибутам поля (accept — какие файлы подходят); если их не получилось прочитать — любые картинки */
    private fun chooserIntent(params: WebChromeClient.FileChooserParams?): Intent {
        val fromPage = try {
            params?.createIntent()
        } catch (e: Exception) {
            null
        }
        return fromPage ?: Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("image/*")
    }

    companion object {
        /** Свой код запроса: 1 занят прежним выбором фото (checkPickPhoto), 3 — запросом разрешений */
        const val REQUEST_CODE = 0x5F11
    }
}
