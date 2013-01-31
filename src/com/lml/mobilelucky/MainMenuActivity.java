package com.lml.mobilelucky;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainMenuActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);
	}
	
	public void onclick1(){
		Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
		startActivity(intent);
	}
	public void onclick2(){
		Intent intent = new Intent(MainMenuActivity.this, NameLuck.class);
		startActivity(intent);
	}
	public void onclick3(){
		
	}
	public void onclick4(){
		
	}
	public void onclick5(){
		
	}
	public void onclick6(){
		
	}
	public void onclick7(){
		
	}
	public void onclick8(){
		
	}
}
