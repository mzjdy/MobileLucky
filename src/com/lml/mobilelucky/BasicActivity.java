package com.lml.mobilelucky;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.domob.android.ads.DomobAdEventListener;
import cn.domob.android.ads.DomobAdManager.ErrorCode;
import cn.domob.android.ads.DomobAdView;


public abstract class BasicActivity extends Activity implements OnClickListener{
	private Handler mHandler = null;
	private ContentAD dialog = null;
	private RelativeLayout mAdContainer = null;
	private DomobAdView mAdview320x50; 
	protected Button title_left ,title_right;
	protected TextView title_mid ; 
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initReceiver();
		getHandler();
	}
	
	protected void initAD(){
		mAdContainer = (RelativeLayout) findViewById(R.id.adcontainer);
		mAdview320x50 = new DomobAdView(this, Common.key, DomobAdView.INLINE_SIZE_320X50);
		mAdview320x50.setKeyword("game");
		mAdview320x50.setAdEventListener(new GGListener());
		mAdContainer.addView(mAdview320x50);
	}
	
	private void initReceiver(){
		IntentFilter temp = new IntentFilter();
		temp.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(netreceiver, temp);
	}
	
	protected void initTitle(){
		title_left = (Button)findViewById(R.id.title_left);
        title_right = (Button)findViewById(R.id.title_right);
        title_mid = (TextView)findViewById(R.id.title_mid_tv);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
	}
	
	BroadcastReceiver netreceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			showLoginBar(checkNet());
		}
	};
	
	protected Handler getHandler() {
		if(mHandler == null){
			mHandler = new Handler(){
				public void handleMessage(android.os.Message msg) {
					BasicActivity.this.handleStateMsg(msg);
				};
			};
		}
		return mHandler;
	}
	
	protected void handleStateMsg(Message msg){
	}
	
	protected void showDialog(String msg) {
		if(dialog == null){
			dialog = new ContentAD(BasicActivity.this, msg);
		}
		dialog.setText(msg);
		if(!dialog.isShowing()){
			dialog.show();
		}
	}
	protected void disDilog(){
		if(dialog != null){
			dialog.dismiss();
		}
	}
	protected void showToast(String msg){
		Toast.makeText(BasicActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onDestroy() {
		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
			dialog = null;
		}
		if(netreceiver != null){
			unregisterReceiver(netreceiver);
		}
		super.onDestroy();
	}
	
	private void showLoginBar(boolean type){
		View loginBar = findViewById(R.id.connection_changed);
		if (loginBar != null){
			Button button = (Button) loginBar.findViewById(R.id.relogin);
            button.setEnabled(true);
            TextView netWorkTitle = (TextView) loginBar.findViewById(R.id.networking_title);
            ImageView noNetImage = (ImageView) loginBar.findViewById(R.id.networking_image);
            // TODO 
            if(type){
            	loginBar.setVisibility(View.GONE);
            }else{
            	netWorkTitle.setText("网络不可用");
            	button.setText("设置");
//            	button.setVisibility(View.VISIBLE);
            	netWorkTitle.setVisibility(View.VISIBLE);
            	noNetImage.setVisibility(View.VISIBLE);
            }
            
		}
	}
	
	protected boolean checkNet()
    {
        //检查网络
        ConnectivityManager manger = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manger.getActiveNetworkInfo();
        if (!(info != null && info.isConnected()))
        {
            showToast("网络不可用");
            return false;
        }
        return true;
    }
	
	protected void hideInputWindow(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		if (imm != null && view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	class GGListener implements DomobAdEventListener{

    	@Override
    	public void onDomobAdReturned(DomobAdView adView) {
    		Log.i("DomobSDKDemo", "onDomobAdReturned");				
    	}

    	@Override
    	public void onDomobAdOverlayPresented(DomobAdView adView) {
    		Log.i("DomobSDKDemo", "overlayPresented");
    	}

    	@Override
    	public void onDomobAdOverlayDismissed(DomobAdView adView) {
    		Log.i("DomobSDKDemo", "Overrided be dismissed");				
    	}

    	@Override
    	public void onDomobAdClicked(DomobAdView arg0) {
    		Log.i("DomobSDKDemo", "onDomobAdClicked");				
    	}

    	@Override
    	public void onDomobAdFailed(DomobAdView arg0, ErrorCode arg1) {
    		Log.i("DomobSDKDemo", "onDomobAdFailed");
    	}

    	@Override
    	public void onDomobLeaveApplication(DomobAdView arg0) {
    		Log.i("DomobSDKDemo", "onDomobLeaveApplication");
    	}

    }
}
