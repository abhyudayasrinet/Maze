package com.ggwp.maze;


import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

	
	LevelsDB DB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
		
		DB = new LevelsDB(getApplicationContext());
		
		try {
			DB.createDataBase();
//			Log.d("db created","done");
		} catch (IOException e) {
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
    						intent.putExtra("level", 1);
    						intent.putExtra("mode", 0);
    						startActivity(intent);
    						break;
    						
    					case 1://normal
    						intent.putExtra("level", 2);
    						intent.putExtra("mode", 0);
    						startActivity(intent);
    						break;	
    					case 2://hard
    						intent.putExtra("level", 3);
    						intent.putExtra("mode", 0);
    						startActivity(intent);
    						break;	
    					case 3://insane
    						intent.putExtra("level", 4);
    						intent.putExtra("mode", 0);
    						startActivity(intent);
    						break;	
    					case 4://impossible
    						intent.putExtra("level", 5);
    						intent.putExtra("mode", 0);
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
				
				Intent intent = new Intent(getApplicationContext(),RecordsTabFragmentActivity.class);
				startActivity(intent);
				
			}
		});
        
        ImageButton help = (ImageButton) findViewById(R.id.help);
        help.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getApplicationContext(),Tutorial.class);
				startActivity(intent);
				
			}
		});
      
        
    	
		AdView adView = (AdView) this.findViewById(R.id.adViewMainMenu);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		
		final SharedPreferences mPreferences = getSharedPreferences("mazePrefs", Activity.MODE_PRIVATE);
		final int rateAppCounter = mPreferences.getInt("rateAppCounter", 1);
		
		if(rateAppCounter != -1 && rateAppCounter%10 == 0) {
			
			AlertDialog.Builder rateAppBuilder = new Builder(getApplicationContext());
			builder.setTitle("RATE US").setMessage("Had fun? Would you like to rate the app?");
			builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					SharedPreferences.Editor editor = mPreferences.edit();
					editor.putInt("rateAppCounter", -1);
					editor.commit();
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ggwp.maze")));
					 
				}
			});
			builder.setNeutralButton("Later", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences.Editor editor = mPreferences.edit();
					editor.putInt("rateAppCounter", rateAppCounter+1);
					editor.commit();
				}
			});
			builder.setNegativeButton("Never", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				
					SharedPreferences.Editor editor = mPreferences.edit();
					editor.putInt("rateAppCounter", -1);
					editor.commit();
					
				}
			});
			
			AlertDialog dialog = builder.create();
			dialog.setCancelable(false);
			dialog.show();
			
		}
		else if(rateAppCounter != -1){
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putInt("rateAppCounter", rateAppCounter+1);
			editor.commit();
		}
		
//		Log.d("rateAppCounter",rateAppCounter+"");
		
		
	}
	
}
