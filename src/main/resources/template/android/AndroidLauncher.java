package ${package};

import android.os.Bundle;
import com.jme3.app.AndroidHarness;

public class AndroidLauncher extends AndroidHarness {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        app=new Main();
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}