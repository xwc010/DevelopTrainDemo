package xwc.com.webviewdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview = (WebView) findViewById(R.id.web);
        webview.getSettings().setJavaScriptEnabled(true);// 设置支持javascript脚本
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if(!TextUtils.isEmpty(url)) { // &&url.contains("/hotel/")
                    //根据class名称获取div数组
                    String fun = "javascript:function getClass(parent,sClass) { "
                                    + "var aEle=parent.getElementsByTagName('div'); "
                                    + "var aResult=[]; "
                                    + "var i=0; "
                                    + "for(i<0;i<aEle.length;i++) { "
                                        + "if(aEle[i].className==sClass) { "
                                        + "aResult.push(aEle[i]); } "
                                    + "};"
                                    + "return aResult; } ";
                    view.loadUrl(fun);

                    //更改特定div的css属性
                    String fun2 = "javascript:function hideOther() {"
                            + "getClass(document,'article_social')[0].style.display='none';"
                            + "getClass(document,'navbar-fixed-top')[0].style.display='none';"
                            + "getClass(document,'comments')[0].style.display='none';"
                            + "getClass(document,'footer')[0].style.display='none';"
                            + "getClass(document,'footer-inner')[0].style.display='none';"
                            + "getClass(document,'article_share_fav')[0].style.display='none';}";

                    String fun3 = "javascript:function hideOther() {"
                            + "var aEle=document.getElementsByTagName('div'); "
                            + "for(i<0;i<aEle.length;i++) { "
//                                + "aEle[i].style.display='none'; "
                                + "var aResult=[]; "
                                + "if(aEle[i].className!='article_body') { "
                                    + "aResult.push(aEle[i]);"
                                    + "aResult[0].style.display='none'; "
                                    +"} "
                            + "};"
                            +"}";

                    view.loadUrl(fun2);

                    view.loadUrl("javascript:hideOther();");
                }

                super.onPageFinished(view, url);
                webview.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            //            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if (url.startsWith("http://") || url.startsWith("https://")) {
//                    Intent webintent = new Intent(context, ShowResultWebViewActivity.class);
//                    webintent.putExtra("url", url);
//                    context.startActivity(webintent);
//                } else {
//                    Log.e("TAG", "url=" + url);
//                }
//                return true;
//            }
        });

        webview.setVisibility(View.GONE);
        webview.loadUrl("http://www.tuicool.com/articles/fiAVjeF");
    }
}
