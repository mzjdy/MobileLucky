package com.lml.mobilelucky;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.domob.android.ads.DomobAdEventListener;
import cn.domob.android.ads.DomobAdManager.ErrorCode;
import cn.domob.android.ads.DomobAdView;

public class MainActivity extends Activity {
	EditText et;
	Button btn, send;
	ProgressDialog ad ;
	TextView mobile, jx, jxdetail, gx, gxdetail;
	/**
	 * 纯Json
	 */
	String data ;
	/**
	 * 纯文本，不带有格式的
	 */
	String realData = "";
	private JSONObject json = null;
	
	private RelativeLayout mAdContainer = null;
	private DomobAdView mAdview320x50; 
	
	private Button title_left ,title_right;
	private TextView title_mid_tv ; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        title_left = (Button)findViewById(R.id.title_left);
        title_right = (Button)findViewById(R.id.title_right);
        title_mid_tv = (TextView)findViewById(R.id.title_mid_tv);
        title_left.setText("返回");
        title_mid_tv.setText("手机号测吉凶");
        title_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        et = (EditText)findViewById(R.id.et);
        btn = (Button)findViewById(R.id.btn_search);
        send = (Button)findViewById(R.id.send);
        mobile = (TextView)findViewById(R.id.mobile);
        jx = (TextView)findViewById(R.id.jx);
        jxdetail = (TextView)findViewById(R.id.jxdetail);
        gx = (TextView)findViewById(R.id.gx);
        gxdetail = (TextView)findViewById(R.id.gxdetail);
        
        ad = new ProgressDialog(MainActivity.this);
        ad.setMessage("嘛迷嘛迷宏~");
        btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isPhoneNumber(et.getText().toString())){
					getData(et.getText().toString());
				}else{
					Toast.makeText(MainActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
				}
			}
		});
        send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone =et.getText().toString();
				if(isPhoneNumber(phone) && realData!=null && !realData.equals("")){
					
					Intent intent=new Intent();
					intent.setAction(Intent.ACTION_SENDTO);
					intent.setData(Uri.parse("smsto:"+phone));
					intent.putExtra("sms_body", realData);
					startActivity(intent);
				}else{
					Toast.makeText(MainActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        
        mAdContainer = (RelativeLayout) findViewById(R.id.adcontainer);
		mAdview320x50 = new DomobAdView(this, Common.key, DomobAdView.INLINE_SIZE_320X50);
		mAdview320x50.setKeyword("game");
		mAdview320x50.setAdEventListener(new GGListener());
		mAdContainer.addView(mAdview320x50);
    }
    
    public void getData(final String number) {
    	new Thread(){
    		public void run(){
    			handler.sendEmptyMessage(SHOWAD);
    			try{
	    			String url = "http://jixiong.showji.com/baidu/api.aspx?m="+number+"&output=json&callback=querycallback";
	    			HttpGet httpget = new HttpGet(url);
	    			HttpClient httpclient = new DefaultHttpClient();
	    			HttpResponse httpresponse = httpclient.execute(httpget);
	    			if(httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
	    				String strResult = EntityUtils.toString(httpresponse.getEntity());
	    				Log.v("0000",strResult+"");
	    				data = strResult.replace("querycallback(", "").replace(");", "");
	    				Log.v("1111",data+"");
	    				json = new JSONObject(data);
	    				realData = "您的手机号吉凶分析："+json.optString("JXDetail")+";个性分析："+json.optString("GXDetail");
	    				handler.sendEmptyMessage(LOADOK);
	    			}else{
	    				Log.v("22222","strResult == null");
	    				handler.sendEmptyMessage(WRONG);
	    			}
    			}catch (Exception e) {
    				e.printStackTrace();
    				handler.sendEmptyMessage(WRONG);
				}
    			/*
    			try{
    				handler.sendEmptyMessage(SHOWAD);
	    			Document doc = Jsoup.connect("http://jixiong.showji.com/search.htm?m="+number).get();
	    			Log.v("==","http://jixiong.showji.com/search.htm?m="+number);
	    			if(doc != null){
	    				Element ele_content = doc.getElementsByClass("cx").first();
	    				if(ele_content != null){
	    					Element ele_data = ele_content.getElementsByTag("ul").first();
	    					if(ele_data != null){
		    					data = ele_data.toString();
		    					Log.v("00", ele_data.toString());
		    					Log.v("11", ele_data.text());
		    					Log.v("22", ele_data.html());
		    					Log.v("33", ele_data.ownText());
		    					realData = ele_data.text();
		    					handler.sendEmptyMessage(LOADOK);
	    					}else{
	    						handler.sendEmptyMessage(WRONG);
	    					}
	    				}else{
	    					handler.sendEmptyMessage(WRONG);
	    				}
	    			}else{
	    				handler.sendEmptyMessage(WRONG);
	    			}
					
    			}catch (Exception e) {
    				handler.sendEmptyMessage(WRONG);
				}
				*/
    		}
    	}.start();
    }
    
    private static final int SHOWAD = 100;
    private static final int DISAD = 101;
    private static final int WRONG = 102;
    private static final int LOADOK = 103;
    
    private Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case SHOWAD:
				if(ad != null){
					ad.show();
				}
				break;
			case DISAD:
				if(ad != null){
					ad.dismiss();
				}
				break;
			case WRONG:
				if(ad != null){
					ad.dismiss();
				}
				Toast.makeText(MainActivity.this, "wrong", Toast.LENGTH_SHORT).show();
				break;
			case LOADOK:
				if(ad != null){
					ad.dismiss();
				}
				refreshView();
				break;
			default:
				break;
			}
    	};
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public boolean isPhoneNumber(String str){
		String temp = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
		Pattern pattern = Pattern.compile(temp);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
    
    public void refreshView(){
    	if(json != null){
    		mobile.setText(json.optString("Mobile",et.getText().toString()) );
    		jx.setText("("+json.optString("JX", "未知")+")");
    		jxdetail.setText(json.optString("JXDetail", "未知"));
    		gx.setText(json.optString("GX", "未知"));
    		gxdetail.setText(json.optString("GXDetail", "未知"));
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
