package com.kerneladiutormod.reborn.services;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import com.kerneladiutormod.reborn.R;
import com.kerneladiutormod.reborn.utils.Constants;
import com.kerneladiutormod.reborn.utils.root.Control;
import java.util.Locale;

import static com.kerneladiutormod.reborn.utils.Constants.SETENFORCE;

@TargetApi(Build.VERSION_CODES.N)
public class SELinuxQSTileService extends TileService {

    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    private static final String PREFERENCES_KEY = "com.google.android_quick_settings";
    protected Context context;

    @Override
    public void onTileAdded() {
        Log.d("QS", "SELinux - Tile added");
        tileSELinuxUpdate();
    }

    @Override
    public void onTileRemoved() {
        Log.d("QS", "SELinux - Tile removed");
    }

    @Override
    public void onClick() {
        Log.d("QS", "SELinux - Tile tapped");
        tileSELinuxUpdate();
    }

    @Override
    public void onStartListening () {
        Log.d("QS", "SELinux - Start listening");
        tileSELinuxUpdate();
    }

    @Override
    public void onStopListening () {
        Log.d("QS", "SELinux - Stop Listening");
    }



    private void tileSELinuxUpdate () {

        Tile tile = this.getQsTile();
        boolean isActive = getServiceStatus();

        Icon newIcon;
        String newLabel;
        int newState;

        // Change the tile to match the service status.
        if (isActive) {
            Log.i(Constants.TAG + ": " + getClass().getSimpleName(), "SELinux - Set Enforcing");
            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label),
                    getString(R.string.service_active));

            newIcon = Icon.createWithResource(getApplicationContext(),
                    R.drawable.ic_selinux_enf);

            newState = Tile.STATE_ACTIVE;
            Control.runCommand("1", SETENFORCE, Control.CommandType.SHELL, context);

        } else {
            Log.i(Constants.TAG + ": " + getClass().getSimpleName(), "SELinux - Set Permissive");
            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label),
                    getString(R.string.service_inactive));

            newIcon =
                    Icon.createWithResource(getApplicationContext(),
                            R.drawable.ic_selinux);

            newState = Tile.STATE_INACTIVE;
            Control.runCommand("0", SETENFORCE, Control.CommandType.SHELL, context);
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);

        // Need to call updateTile for the tile to pick up changes.
        tile.updateTile();
    }

    // Access storage to see how many times the tile
// has been tapped.
    private boolean getServiceStatus() {

        SharedPreferences prefs =
                getApplicationContext()
                        .getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        boolean isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        isActive = !isActive;

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();

        return isActive;
    }
}

