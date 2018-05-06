package cm.g2i.lalalaworker.controllers.appauthenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Sim'S on 28/07/2017.
 */

public class LaLaLaAuthService extends Service {
    private LaLaLaAuthenticator authenticator;

    @Override
    public void onCreate(){
        authenticator = new LaLaLaAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
