package kr.kro.probono

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapTapi
import com.skt.Tmap.TMapTapi.OnAuthenticationListenerCallback
import kr.kro.probono.bluetooth.BlueModule
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var destName: EditText
    private lateinit var exitOnArrive: CheckBox
    private lateinit var exe: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scrollChildLayout = R.layout.activity_main as ViewGroup;
        scrollChildLayout.removeViewAt(R.layout.activity_main)

        setContentView(findViewById(R.layout.activity_main))
        exitOnArrive = findViewById(R.id.view_navigate_exit_on_arrive_CheckBox)
        exe = findViewById(R.id.view_navigate_execute_button)
        val tapi = TMapTapi(this@MainActivity)
        tapi.setOnAuthenticationListener(object : OnAuthenticationListenerCallback {
            override fun SKTMapApikeySucceed() {
                println("succeed api key")
                println("installed : " + tapi.isTmapApplicationInstalled)
                println(tapi.invokeTmap())
            }

            override fun SKTMapApikeyFailed(s: String) {
                println("installed :!!!! " + tapi.isTmapApplicationInstalled)
            }
        })
        tapi.setSKTMapAuthentication("l7xx5330b9922ba74d55880d8ce5f1b0a568")
        setContentView(exe);
        exe.setOnClickListener(View.OnClickListener {
            execute();
            println("!!!!")
        })
    }

    private fun invokeNavigate(DestName: String?, isAutoClose: Boolean): Boolean {

        var szDestName  = DestName
        if (szDestName != null && szDestName.toByteArray().size > 128) {
            val byteTemp = ByteArray(128)
            System.arraycopy(szDestName.toByteArray(), 0, byteTemp, 0, 128)
            szDestName = String(byteTemp)
        }
        if (TMapData.invokeStatistics("A0", true)) {
            var szInvokeMessage = "tmap://navigate?referrer=com.skt.Tmap&name="
            szInvokeMessage = if (szDestName != null && szDestName.trim { it <= ' ' } != "") {
                szInvokeMessage + szDestName
            } else {
                szInvokeMessage + "도착지"
            }
            szInvokeMessage = szInvokeMessage + "&autoclose=" + if (isAutoClose) "y" else "n"
            val intent = Intent("android.intent.action.MAIN")
            intent.setClassName("com.skt.tmap.ku", "com.skt.tmap.ku.IntroActivity")
            intent.putExtra("url", szInvokeMessage)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            this.startActivity(intent)

        }
        return true
    }

    private fun execute() {
        val x = 0f
        val y = 0f
        val id = 11
        println(destName!!.text.toString() + " " + x + " " + y + " " + id + " " + exitOnArrive!!.isChecked)
        val b = invokeNavigate(
                destName!!.text.toString(),
                exitOnArrive!!.isChecked
        )
        println(b)
    }

    private inner class bgYolo : Runnable {
        override fun run() {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    socket = device!!.createInsecureRfcommSocketToServiceRecord(UUID.randomUUID())
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (device == null) {
                println("device not found")
                return
            }
            BlueModule.BlueConf.ConnectThread.__init__()
            BlueModule.BlueConf.ConnectThread.__onRun__(findViewById(R.id.logView), socket)
            println("device detected - " + device)
        }
    }



    companion object {
        lateinit var socket: BluetoothSocket
        lateinit var device: BluetoothDevice
    }
}