package com.nitobi.phonegap;
/* License (MIT)
 * Copyright (c) 2008 Nitobi
 * website: http://phonegap.com
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * “Software”), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class DroidGap extends Activity {
	
	private static final String LOG_TAG = "DroidGap";
	private WebView appView;
	
	private Handler mHandler = new Handler();
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
         
        appView = (WebView) findViewById(R.id.appView);
        
        /* This changes the setWebChromeClient to log alerts to LogCat!  Important for Javascript Debugging */
        
        appView.setWebChromeClient(new MyWebChromeClient());
        appView.getSettings().setJavaScriptEnabled(true);
        appView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);        
        
        /* Bind the appView object to the gap class methods */
        bindBrowser(appView);
        
        /* Load a URI from the strings.xml file */
        String uri = null;
        Class<R.string> c = R.string.class;
        Field f;
        int i = 0;
        
        try {
        	f = c.getField("url");
        	i = f.getInt(f);
        	uri = this.getResources().getString(i);
        } catch (Exception e)
        {
        	uri = "http://www.phonegap.com";
        }
                
        appView.loadUrl(uri);
        
    }
    
    protected void loadFile(WebView appView){
        try {
            InputStream is = getAssets().open("index.html");
                      
            int size = is.available();
            
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            
            // Convert the buffer into a Java string.
            String text = new String(buffer);
            
            // Load the local file into the webview
            appView.loadData(text, "text/html", "UTF-8");
            
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
    }
    
    private void bindBrowser(WebView appView)
    {
    	PhoneGap gap = new PhoneGap(this, mHandler, appView);
    	appView.addJavascriptInterface(gap, "DroidGap");
    }
    
    /**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    final class MyWebChromeClient extends WebChromeClient {
    	@Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d(LOG_TAG, message);
            result.confirm();
            return true;
        }
    }
    
}