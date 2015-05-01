package com.vhiefa.whatsonundip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Afifatul Mukaroh
 */
public class SubmitEventFragment extends Fragment {
	
	private WebView browser;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_submitevent, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        browser = (WebView)rootView.findViewById(R.id.webView1);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        browser.setWebViewClient(new MyBrowser());
        String url = "http://docs.google.com/forms/d/1hZJ5c6KnBqyzVIdMyfhsrIXPoBHThTmV53Tap13938U/viewform";
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        browser.loadUrl(url); 
    }



    @Override
    public void onResume() {
        super.onResume();
    }
        
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
           view.loadUrl(url);
           return true;
        }
     }
    

}


