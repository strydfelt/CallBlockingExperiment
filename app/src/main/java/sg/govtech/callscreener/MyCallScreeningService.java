package sg.govtech.callscreener;

import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.util.Log;

public class MyCallScreeningService extends CallScreeningService {

    public static final String TAG = "MCSS";

    @Override
    public void onScreenCall(Call.Details callDetails) {
        Log.i(TAG, "onScreenCall invoked");
    }
}
