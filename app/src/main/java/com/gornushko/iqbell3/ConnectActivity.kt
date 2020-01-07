package com.gornushko.iqbell3

import android.content.Intent
import android.os.Bundle
import android.util.Log.e
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_connect.*
import org.jetbrains.anko.intentFor


@ExperimentalUnsignedTypes
class ConnectActivity : AppCompatActivity() {

    companion object Const {
        const val KEY = "key"
    }

    private var connected = false

    private fun connected(data: ByteArray, extraData: ByteArray) {
        connected = true
        //startActivity(intentFor<MainActivity>(MainActivity.START_DATA to data,
        //   MainActivity.START_EXTRA_DATA to extraData).newTask().clearTask().clearTop())
    }

    private fun reconnecting() {
        status.text = getText(R.string.no_connection)
    }

    @ExperimentalUnsignedTypes
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        setSupportActionBar(toolbar as Toolbar?)
        when (intent.extras?.getInt(KEY)) {
            IQService.NO_INTERNET -> {
                startService(
                    intentFor<IQService>(
                        IQService.ACTION to IQService.NEW_PENDING_INTENT,
                        IQService.PENDING_INTENT to createPendingResult(1, intent, 0)
                    )
                )
                reconnecting()
            }
            else -> startService(
                intentFor<IQService>(
                    IQService.ACTION to IQService.START,
                    IQService.PENDING_INTENT to createPendingResult(1, intent, 0)
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> startActivity(intentFor<AboutActivity>())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!connected) {
            startService(
                Intent(this, IQService::class.java).putExtra(
                    IQService.ACTION,
                    IQService.STOP_SERVICE
                )
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            IQService.NO_INTERNET -> reconnecting()
            IQService.CONNECTING -> connecting()
            IQService.CONNECTED -> e("CONNECT_ACTIVITY", "connected")
            //connected(data!!.getByteArrayExtra(IQService.DATA)!!,
            //data.getByteArrayExtra(IQService.EXTRA_DATA)!!)
        }
    }

    private fun connecting() {
        status.text = getText(R.string.loading)
    }

}
