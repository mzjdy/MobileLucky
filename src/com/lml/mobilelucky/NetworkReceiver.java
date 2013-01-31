package com.lml.mobilelucky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkReceiver extends BroadcastReceiver {
	protected Context    mContext;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		 mContext = context;
		 
		 
	}

}
