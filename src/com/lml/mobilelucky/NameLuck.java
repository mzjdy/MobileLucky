package com.lml.mobilelucky;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NameLuck extends BasicWithPopActivity{
	private EditText et ;
	private Button btn_search;
	TextView mobile, jx, jxdetail, gx, gxdetail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nameluck);
		initTitle();
		initView();
		initAD();
	}
	
	public void initView(){
		et = (EditText)findViewById(R.id.et);
		btn_search = (Button)findViewById(R.id.btn_search);
		btn_search.setOnClickListener(this);
		
		mobile = (TextView)findViewById(R.id.mobile);
        jx = (TextView)findViewById(R.id.jx);
        jxdetail = (TextView)findViewById(R.id.jxdetail);
        gx = (TextView)findViewById(R.id.gx);
        gxdetail = (TextView)findViewById(R.id.gxdetail);
        
        title_left.setText("返回");
        title_mid_tv.setText("身份证测吉凶");
	}

	@Override
	protected void handleStateMsg(Message msg) {
		switch (msg.what) {
		case 100:
			showDialog("嘛迷嘛迷宏~");
			break;
		case 101:
			disDilog();
			break;
		case 200:
			String[] result = (String[]) msg.getData().get("result");
			mobile.setText(et.getText().toString());
			jx.setText(result[0]);
			jxdetail.setText(result[1]);
			gx.setText(result[2]);
			gxdetail.setText(result[3]);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		hideInputWindow(v);
		switch (v.getId()) {
		case R.id.title_left:
			finish();
			break;
		case R.id.btn_search:
			getResult(et.getText().toString().trim());
			break;
		default:
			break;
		}
	}
	
	public void getResult(final String number){
		if(number!= null && number.length()> 0 )
		{
			new Thread(){
				public void run(){
					// loading
					getHandler().sendEmptyMessage(100);
					try{
						String url = "http://toolsapp.duapp.com/id_lucky.php?action=do";
						HttpClient httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost(url);
						
						//请求参数
						ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("num", number));
						httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
						HttpResponse httpResponse = httpclient.execute(httppost);
			            int statusCode = httpResponse.getStatusLine().getStatusCode();
			            if(statusCode==HttpStatus.SC_OK)
			            {
			                //获得返回结果
			            	String temp = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			            	if(temp != null && temp.length()>0){
			            		Document doc = Jsoup.parse(temp);
			            		Elements eles = doc.getElementsByClass("d");
			            		String[] result = new String[]{"天机不可泄露","天机不可泄露","天机不可泄露","天机不可泄露"};
			            		if(eles.size() == 5){
				            		for (int i = 0; i < 4; i++) {
										if(i<eles.size()){
											result[i]=eles.get(i+1).text();
											Log.v("888",i+"="+eles.get(i+1));
										}
									}
			            		}
			            		Message msg = new Message();
			            		msg.what = 200;
			            		Bundle data = new Bundle();
			            		data.putStringArray("result", result);
			            		msg.setData(data);
			            		getHandler().sendMessage(msg);
			            	}else{
			            		throw new Exception("返回的内容html为空");
			            	}
			            }
					}catch (Exception e) {
						e.printStackTrace();
						// 错误提示
						getHandler().sendEmptyMessage(400);
					}finally{
						// 消失loading
						getHandler().sendEmptyMessage(101);
					}
				}
			}.start();
		}else{
			showToast("输入的证件号有误！");
		}
	}

	@Override
	String[] getPopItemsWords() {
		return Common.popitems;
	}

	@Override
	OnItemClickListener getPopItemsListener() {
		OnItemClickListener listener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				switch (position) {
				// 身份
				case 0:
					break;
					// 手机号
				case 1:
					intent = new Intent(NameLuck.this, MainActivity.class);
					popupWindow.dismiss();
					startActivity(intent);
					break;
				default:
					break;
				}
			}
		};
		return listener;
	}

}
