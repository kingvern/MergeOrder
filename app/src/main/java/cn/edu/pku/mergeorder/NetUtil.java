package cn.edu.pku.mergeorder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by kingvern on 17/12/19.
 */

class NetUtil {
    public static final int NETWORN_NONE = 0;
    public static final int NETWORN_WIFI = 1;
    public static final int NETWORN_MOBILE = 2;

    public static int getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NETWORN_NONE;
        }

        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            return NETWORN_MOBILE;
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            return NETWORN_WIFI;
        }
        return NETWORN_NONE;
    }
}
