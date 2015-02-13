package com.ggwp.maze;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Window;


public class RecordsTabFragmentActivity extends FragmentActivity {
	
	private FragmentTabHost mTabHost;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.records_fragment);

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("QuickGame").setIndicator("Quick Game"),
            RecordsQuickGameFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("NormalGame").setIndicator("Normal Game"),
            RecordsLevelsFragment.class, null);
        
		
    }
		
	

}
