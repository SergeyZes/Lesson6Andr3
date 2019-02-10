package lesson5.rumpilstilstkin.ru.databaseex;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkInfomodule {
    private Context context;

    public NetworkInfomodule(Context context) {
        this.context = context;
    }

    @Provides
    ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    NetworkInfo getNetworkInfo(ConnectivityManager connectivityManager) {
        return connectivityManager.getActiveNetworkInfo();
    }

}
