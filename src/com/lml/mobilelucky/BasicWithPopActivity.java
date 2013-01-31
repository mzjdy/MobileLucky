package com.lml.mobilelucky;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.domob.android.ads.DomobAdEventListener;
import cn.domob.android.ads.DomobAdManager.ErrorCode;
import cn.domob.android.ads.DomobAdView;


public abstract class BasicWithPopActivity extends Activity implements OnClickListener{
	private Handler mHandler = null;
	private ContentAD dialog = null;
	private RelativeLayout mAdContainer = null;
	private DomobAdView mAdview320x50; 
	protected Button title_left ,title_right;
	protected TextView title_mid_tv ; 
	protected ImageButton title_mid_ib;
	protected PopupWindow popupWindow ;
	
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
        title_mid_tv = (TextView)findViewById(R.id.title_mid_tv);
        title_mid_ib = (ImageButton)findViewById(R.id.title_mid_ib);
        title_mid_ib.setVisibility(View.VISIBLE);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        title_mid_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showItemPop();
			}
		});
	}
	
	private void showItemPop(){
		if(popupWindow == null){
			LinearLayout view = (LinearLayout) LayoutInflater.from(BasicWithPopActivity.this).inflate(R.layout.pop_type_layout, null);
			ListView temp = (ListView) view.findViewById(R.id.pop_type_lv);
			PopTypeAdapter tempdadapter = new PopTypeAdapter(BasicWithPopActivity.this, getPopItemsWords());
			temp.setAdapter(tempdadapter);
			popupWindow = new PopupWindow(view ,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			popupWindow.setFocusable(true);
			popupWindow.setTouchable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  
			temp.setOnItemClickListener(getPopItemsListener());
		}
		if(popupWindow != null){
			if(popupWindow.isShowing()){
				popupWindow.dismiss();
				title_mid_ib.setBackgroundResource(R.drawable.spinner_undown);
			}else{
			    // 计算x轴方向的偏移量，使得PopupWindow在Title的正下方显示，此处的单位是pixels  
			    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			    Rect frame = new Rect();  
	            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
	            int state_heght = frame.top;// 状态栏的高度  
	            int y = findViewById(R.id.title).getBottom()+state_heght ;  
	            int x = getWindowManager().getDefaultDisplay().getWidth()/4;  
				popupWindow.showAtLocation(findViewById(R.id.title), Gravity.TOP | Gravity.LEFT, x, y);
				title_mid_ib.setBackgroundResource(R.drawable.spinner_down);
			}
		}
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
					BasicWithPopActivity.this.handleStateMsg(msg);
				};
			};
		}
		return mHandler;
	}
	
	abstract void handleStateMsg(Message msg);
	
	protected void showDialog(String msg) {
		if(dialog == null){
			dialog = new ContentAD(BasicWithPopActivity.this, msg);
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
		Toast.makeText(BasicWithPopActivity.this, msg, Toast.LENGTH_SHORT).show();
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
	
	
	abstract String[] getPopItemsWords();
	abstract OnItemClickListener getPopItemsListener();
	
	class PopTypeAdapter extends BaseAdapter {

		private Context context ;
		private LayoutInflater li ;
		private String[] data;
		public PopTypeAdapter(Context context, String[] data){
			this.context = context;
			this.data = data;
			li = LayoutInflater.from(context);
		}
		

		@Override
		public int getCount() {
			return data.length;
		}

		@Override
		public Object getItem(int position) {
			return data[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if(convertView == null){
				holder = new Holder();
				convertView = li.inflate(R.layout.pop_type_lv_item, null);
				holder.tv = (TextView)convertView.findViewById(R.id.pop_type_lv_item_tv);
				convertView.setTag(holder); 
			}else{
				holder = (Holder)convertView.getTag();
			}
			holder.tv.setText(data[position]);
			return convertView;
		}
		
		class Holder{
			TextView tv;
		}

	}
}
