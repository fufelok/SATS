package se.leanbit.sats;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;


public class SatsWebActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        ImageView image = (ImageView) findViewById(R.id.action_bar_logo_settings);
        setupClickListner(image);
        image.setImageResource(R.drawable.back_icon);
        setSupportActionBar(toolbar);

        WebView view = (WebView)findViewById(R.id.web_view_main);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        view.loadUrl(super.getIntent().getExtras().getString("urlString"));
    }
    private void setupClickListner(ImageView image)
    {
        image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Intent intent = new Intent(view.getContext(), MapViewActivity.class);
                //view.getContext().startActivity(intent);
                finish();
            }
        });
    }
}
