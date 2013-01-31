package com.lml.mobilelucky;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContentAD extends AlertDialog{
	private Context context;
	private String  str_content;
	private TextView tv_content;
	private LinearLayout ad_view = null;
	
	public ContentAD(Context context, 
			String content){
		super(context);
		this.context = context;
		this.str_content = content;
		
		ad_view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.mydialog_content, null);
		tv_content = (TextView)ad_view.findViewById(R.id.content);
		tv_content.setText(str_content);
	}
	
	public void setText(String content){
		if(tv_content != null){
			this.str_content = content;
			tv_content.setText(str_content);
		}
	}
	@Override
	public void show() {
		super.show();
		Window window = this.getWindow();
		window.setContentView(ad_view);
	}
	
}
