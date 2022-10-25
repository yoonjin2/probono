package kr.kro.probono

import android.Manifest
import android.content.Intent
import android.widget.Button
import android.widget.CheckBox
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapTapi
import com.skt.Tmap.TMapTapi.OnAuthenticationListenerCallback
import kr.kro.probono.R.*
import android.os.Bundle
import java.util.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.speech.tts.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.io.IOError
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)


        val tapi = TMapTapi(this@MainActivity)
        tapi.setOnAuthenticationListener(object : OnAuthenticationListenerCallback {
            override fun SKTMapApikeySucceed() {
                println("succeed api key")
                println(tapi.invokeTmap())
            }

            override fun SKTMapApikeyFailed(s: String) {
            }
        })
        tapi.setSKTMapAuthentication("l7xx5330b9922ba74d55880d8ce5f1b0a568")

        val exe: Button = findViewById(id.view_navigate_execute_button)
        exe.setOnClickListener {
            execute()
            initListener()
        }
    }

    private fun initListener() {
        val connPerm = arrayOf(
            android.Manifest.permission.BLUETOOTH_CONNECT,
        )
        requestPermissions(connPerm, 100)
           try {
               var devLocals = BluetoothAdapter.getDefaultAdapter().getBondedDevices()
               lateinit var dev : BluetoothDevice
               val i=0;
               for(i in devLocals) {
                   if(i.name.equals("raspberrypi")) {
                       dev=i;
                       break
                   } else if (i==devLocals.last()) {
                       return
                   }
               }
               val sock = dev.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                sock.run {
                    try {
                        this.connect()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                    while (true) {
                        try {
                            if(! sock.isConnected()) {
                                sock.connect()
                                continue
                            }
                            val outputStream = this!!.outputStream
                            outputStream.write(1)
                            val inputStream = this.inputStream
                            var prev = ""
                            var input = inputStream.readBytes().toString()
                            if (input != prev) {
                                prev = input;
                                SynthesisRequest(input, Bundle())
                                Log.d("Probono", input)
                            }
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                        } catch (e:  IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
    }



    private fun invokeNavigate(DestName: String?, isAutoClose: Boolean): Boolean {

        var szDestName  = DestName
        if ((szDestName != null) && (szDestName.toByteArray().size > 128)) {
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
        val destName :TextInputEditText = findViewById(id.view_navigate_destination_input)
        val exitOnArrive :CheckBox = findViewById(id.view_navigate_exit_on_arrive_CheckBox)
        val x = 0f
        val y = 0f
        val id = 11
        println(destName.text.toString()+"로 도착지를 설정하셨습니다.")
        val b = invokeNavigate(
                destName.text.toString(),
                exitOnArrive.isChecked
        )
        println(b)
    }

}
