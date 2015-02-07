package com.ggwp.maze;


import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
		
		LevelsDB DB = new LevelsDB(getApplicationContext());
		
		try {
			DB.createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Button quickGame = (Button) findViewById(R.id.quickGame);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
        quickGame.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				builder.setTitle("Choose Difficulty")
    			.setItems(R.array.difficulty_options, new DialogInterface.OnClickListener() {
                
    				public void onClick(DialogInterface dialog, int which) {
    					
    					//finish();
    					Intent intent = new Intent(getApplicationContext(), GameActivity.class);

    					switch(which) {
    					
    					case 0: //sandbox
    						intent.putExtra("cell_size", 100);
    						startActivity(intent);
    						break;
    						
    					case 1://normal
    						intent.putExtra("cell_size", 60);
    						startActivity(intent);
    						break;	
    					case 2://hard
    						intent.putExtra("cell_size", 40);
    						startActivity(intent);
    						break;	
    					case 3://insane
    						intent.putExtra("cell_size", 20);
    						startActivity(intent);
    						break;	
    					case 4://impossible
    						intent.putExtra("cell_size", 10);
    						startActivity(intent);
    						break;
    					}
    				}
    			});
				
				AlertDialog dialog = builder.create();
    			dialog.setCancelable(true);
    			dialog.show();
				
			}
		});
        
        Button normalGame = (Button) findViewById(R.id.normalGame);
        normalGame.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getApplicationContext(),LevelsList.class);
				startActivity(intent);
				
			}
		});
        
        Button records = (Button) findViewById(R.id.records);
        records.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getApplicationContext(),RecordsList.class);
				startActivity(intent);
				
			}
		});
        
      
        
    	
		AdView adView = (AdView) this.findViewById(R.id.adViewMainMenu);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		
	}
		
}
