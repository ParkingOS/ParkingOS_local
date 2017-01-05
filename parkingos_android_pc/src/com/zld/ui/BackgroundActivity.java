package com.zld.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.umeng.analytics.MobclickAgentJSInterface;
import com.zld.R;
import com.zld.lib.constant.Constant;

public class BackgroundActivity extends BaseActivity {

	private WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);   
		setContentView(R.layout.background_web);
		webView = (WebView) findViewById(R.id.wv_background_web);
		webView.getSettings().setJavaScriptEnabled(true); // 设置支持javascript
		webView.setWebViewClient(new WebViewClient() {

			@SuppressWarnings("unused")
			public void onProgressChanged(WebView view, int newProgress) {
				// activity的进度是0 to 10000 (both inclusive),所以要*100
				BackgroundActivity.this.setProgress(newProgress * 100);
			}
		});
		new MobclickAgentJSInterface(this, webView, new WebChromeClient());
		webView.loadUrl(Constant.INTO_BACK);
	}
}
