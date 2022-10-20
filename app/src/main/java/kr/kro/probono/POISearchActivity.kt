package kr.kro.probono

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.skt.Tmap.TMapData

class POISearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_poi)
        super.onCreate(savedInstanceState)
        initListener(findViewById(R.id.button_poi_search), findViewById(R.id.edit_text_poi_search))
    }

    private fun initListener(button: Button, editText: EditText) {
        button.setOnClickListener {
            val input = editText;

        }
        val data = TMapData()
    }
}