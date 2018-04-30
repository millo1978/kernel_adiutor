package com.kerneladiutormod.reborn.services;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

import com.kerneladiutormod.reborn.R;
import com.kerneladiutormod.reborn.utils.root.Control;

import static com.kerneladiutormod.reborn.utils.Constants.SETENFORCE;
import static com.kerneladiutormod.reborn.utils.kernel.Misc.isSELinuxActive;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SELinuxQSTileService extends TileService {

    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onTileAdded() {
        tileSELinuxUpdate(getQsTile());
    }

    @Override
    public void onTileRemoved() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick() {
        tileSELinuxToggle(getQsTile());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStartListening () {
        tileSELinuxUpdate(getQsTile());
    }

    @Override
    public void onStopListening () {

    }
    @TargetApi(Build.VERSION_CODES.N)
    private void tileSELinuxUpdate (Tile qsTile) {
        Icon icon =  Icon.createWithResource(getApplicationContext(), R.drawable.ic_selinux);
        if (isSELinuxActive()) {
            if (qsTile.getState() == Tile.STATE_INACTIVE) {
                qsTile.setState(Tile.STATE_ACTIVE);
            }
        } 
        if (!isSELinuxActive()) {
            if (qsTile.getState() == Tile.STATE_ACTIVE) {
                qsTile.setState(Tile.STATE_INACTIVE);
            }
        }
        getQsTile().setIcon(icon);
        getQsTile().updateTile();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void tileSELinuxToggle (Tile qsTile) {
        if (isSELinuxActive() && qsTile.getState() == Tile.STATE_ACTIVE) {
            Control.runCommand("0", SETENFORCE, Control.CommandType.SHELL, context);
        }
        else if (!isSELinuxActive() && qsTile.getState() == Tile.STATE_INACTIVE) {
            Control.runCommand("1", SETENFORCE, Control.CommandType.SHELL, context);
        }
       try{
            // Pause momentarily for sysfs changes
            // This should be done differently, but this will work for now.
            Thread.sleep(100);
       }
        catch(InterruptedException e){

    }
        tileSELinuxUpdate(qsTile);
    }

}
