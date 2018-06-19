package com.adev.root.snipps.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.adev.root.snipps.view.NewBookActivityView;

public class NewBookPresenter {

    private final NewBookActivityView view;
    private final Context context;
    private NetworkInfo[] allNetworkInfo;

    public NewBookPresenter(NewBookActivityView view, Context context) {

        this.view = view;
        this.context = context;


    }

    public void CheckForInternet()
    {
        boolean haveWifiConnected = false;
        boolean haveMobileDataConnected = false;

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        allNetworkInfo = cm.getAllNetworkInfo();

        for(NetworkInfo nf : allNetworkInfo)
        {
            if(nf.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if(nf.isConnected())
                {
                    haveWifiConnected = true;
                }
            }

            if (nf.getTypeName().equalsIgnoreCase("MOBILE"))
            {
                if(nf.isConnected())
                {
                    haveMobileDataConnected = true;
                }
            }
        }

        if( haveWifiConnected || haveMobileDataConnected)
        {
            view.sendForScan();
        }
        else
        {
            view.showNoInternet();
        }

    }

}
