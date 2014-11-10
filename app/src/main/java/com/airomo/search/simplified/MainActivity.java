package com.airomo.search.simplified;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	private WebView wvMain;
	String URL = "http://static.airomo.com/loc/eng/sresults.html";
	
	private TextView tvMessage;
    private ImageButton ibActionRefresh;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_main);   
        
        ActionBar actionBar = getActionBar();
        View mActionBarView = getLayoutInflater()
                .inflate(R.layout.action_bar_custom, null);
        if (actionBar != null) {
            actionBar.setCustomView(mActionBarView);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        }

        
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        ImageButton ibActionBack = (ImageButton) mActionBarView.findViewById(R.id.action_back);
        ibActionRefresh = (ImageButton) mActionBarView.findViewById(R.id.action_refresh);
        
        ibActionBack.setOnClickListener(this);
        ibActionRefresh.setOnClickListener(this);        
        
        
        
        //WebView initialization
        wvMain = (WebView) findViewById(R.id.wv_main);

        wvMain.getSettings().setJavaScriptEnabled(true);
        wvMain.getSettings().setAppCacheEnabled(true);
        wvMain.getSettings().setAppCachePath(getApplicationContext().getFilesDir().getPath() + "/cache");
        wvMain.getSettings().setLoadWithOverviewMode(true);
        wvMain.getSettings().setUserAgentString("Android");
        
        
        wvMain.setWebChromeClient(new WebChromeClient());
        wvMain.setWebViewClient(new WebViewClient() {
        	
        	public void onReceivedError( WebView view, int errorCode, String description, String failingUrl) 
            {
        		Log.d("MyLog", "error");
        		
        		String[] splitMain = {"", "", ""};
        		try {
        			splitMain = failingUrl.trim().split("loc/eng");
        		} catch (Exception e) {
                    splitMain[0] = "";
                }
        		       		
        		if (failingUrl.equals("http://static.airomo.com/loc/eng/sresults.html") ||
        				splitMain[0].equals("http://static.airomo.com/")) {
        			view.loadUrl("http://static.airomo.com/loc/eng/sresults.html");
        		} else {
        			goBackInWebView(view);
        		}       		
            }
        	
        	@Override 
        	public void onPageStarted(WebView view, String url, Bitmap favicon) {       		
        		Log.d("MyLog", "onPageStarted: " + url);
        		
         		view.setVisibility(View.GONE);
         		String[] splitMain = url.trim().split("loc/eng");
         		
         		if (url.equals("http://static.airomo.com/loc/eng/sresults.html") || 
         				splitMain[0].equals("http://static.airomo.com/")) {
         			tvMessage.setText(getResources().getString(R.string.loading));
         		} else {
         			tvMessage.setText(getResources().getString(R.string.redirecting));
         		}
         		
         		String[] splitPlay = url.split("play");
	        		
        		if (splitPlay[0].equals("https://") || splitPlay[0].equals("http://")) {	        			
	        			
	        		String packageName = url.split("id=")[1];
	        		
	        		goBackInWebView(view);
	        		
	        		try {
	        			startActivity(new Intent(Intent.ACTION_VIEW,
	        			    		Uri.parse("market://details?id=" + packageName)));
	        			Log.d("MyLog", "market");
	        		} catch (android.content.ActivityNotFoundException anfe) {
	        			startActivity(new Intent(Intent.ACTION_VIEW,
	        			    		Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
	        			Log.d("MyLog", "browser");
	        		}	        		
	        			
	        	} else if (url.split("://")[0].equals("market")) {
	        			
	        		goBackInWebView(view);
	        		
	        		try {
	        			startActivity(new Intent(Intent.ACTION_VIEW,
	        			    		Uri.parse(url)));
	        			Log.d("MyLog", "market");
	        		} catch (android.content.ActivityNotFoundException anfe) {
	        			Toast.makeText(getApplicationContext(), "You have to install Google Play Store", Toast.LENGTH_SHORT).show();
	        		}	        		
	        		
	        	}

        	}
        	
        	@Override
        	public void onPageFinished (WebView view, String url) { 
        		Log.d("MyLog", "onPageFinished: " + url);
        		view.setVisibility(View.VISIBLE); 
        		ibActionRefresh.setEnabled(true);
        	}       	
        });

        wvMain.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        
        wvMain.loadUrl(URL);
    }
	
	public void goBackInWebView(WebView view) {

		WebBackForwardList history = view.copyBackForwardList();

		int index = 0 - (history.getCurrentIndex());
		if (index == 0) {
			view.loadUrl(URL);
		} else {
			view.goBackOrForward(index);
		}
    	//view.setVisibility(View.VISIBLE);
    	Log.d("MyLog", "goBackInWebView: " + index);
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.id.action_back:
            wvMain.goBack();
            break;
        case R.id.action_refresh:
            wvMain.reload();
            tvMessage.setText(getResources().getString(R.string.loading));
            v.setEnabled(false);
            break;
		}
	}	
}
