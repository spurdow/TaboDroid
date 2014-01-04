package com.example.tabodroid;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

	
	private WebView web;
	private final String URL =  "http://192.168.0.120/TaboDroid V5";//"http://tabodroid.freeserver.me";
	private final static int FILECHOOSER_RESULTCODE = 1;
	
	private ValueCallback<Uri> mUploadMessage;
	private String mCameraFilePath;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        web = (WebView) findViewById(R.id.webView1);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setSaveFormData(true);
            
        web.setWebChromeClient(new WebChromeClient(){
        	
        	/*
        	 *  2.0 and above
        	 */
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            	 final String imageMimeType = "image/*";
            	
	              mUploadMessage = uploadMsg;
	              
	          	  
	              Intent chooser = createChooserIntent(createCameraIntent());
	              chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent(imageMimeType));           
	              MainActivity.this.startActivityForResult(
	                        Intent.createChooser(chooser, "Image Browser"),
	                        FILECHOOSER_RESULTCODE);
	               // Toast.makeText(MainActivity.this, "first file chooser", Toast.LENGTH_LONG).show();
	                
	                
	                Log.d("MainActivity", "openFileChooser!");
            }        	
            /*
             *  for 3.0 and above
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg , String acceptType){
            	openFileChooser(uploadMsg);
            	//Toast.makeText(MainActivity.this, "second file chooser", Toast.LENGTH_LONG).show();
            }
            
            /*
             *  for 4.1 and above
             */
            public void openFileChooser(ValueCallback<Uri> u , String accept , String capture){
            	openFileChooser(u ,accept);
            }
            
            /*
             *  for 4.1 and above
             */
            public void openFileChooser(ValueCallback<Uri> u , String accept , String capture , String str){
            	openFileChooser(u ,accept);
            }
            
        });
        web.setWebViewClient(new WebViewClient(){


			@Override
    		public void onPageFinished(WebView view, String url) {
    			// TODO Auto-generated method stub
    			String cookies = CookieManager.getInstance().getCookie(url);
    			//Toast.makeText(MainActivity.this, cookies, Toast.LENGTH_LONG).show();
    			//Log.d("TA", cookies);
    		}
  
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				//return super.shouldOverrideUrlLoading(view, url);
			
				
				if(url.startsWith("tel:")){
					Intent i = new Intent(Intent.ACTION_DIAL , Uri.parse(url));
					// call the activity using this code
					MainActivity.this.startActivity(i);
				}
				else
				  view.loadUrl(url);
				
				
				
				
				return true;
			}
			
        	
        });
        
        web.loadUrl(URL);
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.refresh: 
				web.reload();
			break;
		case R.id.next :
				if(web.canGoForward()){
					web.goForward();
				}
			break;
		case R.id.previous : 
				if(web.canGoBack()){
					web.goBack();
				}
			break;
		
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "Activity Result Workd!", Toast.LENGTH_LONG).show();
	    if (requestCode == FILECHOOSER_RESULTCODE) {
	        if (null == mUploadMessage)
	            return;
	        Uri result = data == null || resultCode != RESULT_OK ? null
	                : data.getData();
	        if (result == null && data == null && resultCode == Activity.RESULT_OK) {
	            File cameraFile = new File(mCameraFilePath);
	            if (cameraFile.exists()) {
	                result = Uri.fromFile(cameraFile);
	                // Broadcast to the media scanner that we have a new photo
	                // so it will be added into the gallery for the user.
	                this.sendBroadcast(
	                        new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
	            }
	        }

	        mUploadMessage.onReceiveValue(result);
	        mUploadMessage = null;

	    }
	}
	/*
	 *  if web can go back
	 *  then we can go back
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(web.canGoBack()){
			web.goBack();
			return;
		}
		super.onBackPressed();
	}
    
    private Intent createOpenableIntent(String type) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType(type);
        return i;
    }
    
    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File externalDataDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        File cameraDataDir = new File(externalDataDir.getAbsolutePath() +
                File.separator + "browser-photos");
        cameraDataDir.mkdirs();
        mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator +
                System.currentTimeMillis() + ".jpg";
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mCameraFilePath)));
        return cameraIntent;
    }
	/*
	 *  chooser uploiad
	 */
    private Intent createChooserIntent(Intent... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        chooser.putExtra(Intent.EXTRA_TITLE,"Choose Upload");
        return chooser;
    }
	
    
}
