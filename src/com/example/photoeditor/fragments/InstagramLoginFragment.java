package com.example.photoeditor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.example.photoeditor.R;
import com.example.photoeditor.helpers.Uris;

/**
 * Created by Андрей on 23.11.2014.
 */
public class InstagramLoginFragment extends Fragment {
    private WebView mWebView;
    private String mAcessToken;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment,container,false);
        mWebView = (WebView)view.findViewById(R.id.webViewInstagram);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSaveFormData(false);
        mWebView.setWebViewClient(new InstagramWebViewClient());
        mWebView.loadUrl(Uris.authURLString);
        return view;
    }
    private class InstagramWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith(Uris.REDIRECT_URI))
            {
                mAcessToken = (url.split("="))[1];
                FragmentManager manager = getActivity().getSupportFragmentManager();
                Fragment photoFragment = DisplayPhotoFragment.newInstance(mAcessToken);
                manager.beginTransaction().replace(R.id.instagram_frame_container,photoFragment)
                        .commit();
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
