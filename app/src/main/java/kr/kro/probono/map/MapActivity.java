package kr.kro.probono.map;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapView;

import kr.kro.probono.R;
import kr.kro.probono.APIKeys;

public class MapActivity extends AppCompatActivity {

    private TMapView view;

    public TMapView getView() {
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_map);
        super.onCreate(savedInstanceState);

        /* init map view start */
        FrameLayout container = findViewById(R.id.TMapViewContainer);
        view = new TMapView(this);
        container.addView(view);
        view.setSKTMapApiKey(APIKeys.SKT_MAP_API_KEY);
    }


}
