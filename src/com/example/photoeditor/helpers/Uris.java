package com.example.photoeditor.helpers;

public class Uris {
	public static final String AUTHURL = "https://api.instagram.com/oauth/authorize/";
	public static final String TOKENURL = "https://api.instagram.com/oauth/access_token";
	public static final String APIURL = "https://api.instagram.com/v1";
	public static final String CLIENT_ID = "9eca079586dc4eda8c71a39801dede9e";
	public static final String CLIENT_SECRET = "0adf7e3fd4fb479383e9bdbe0763bbd9";
	public static String REDIRECT_URI = "http://lobintsev.com/";
	public static final String authURLString = AUTHURL + "?client_id="+CLIENT_ID+
	"&redirect_uri="+REDIRECT_URI+"&response_type=token";
	public static final String tokenURLString =TOKENURL + "?client_id=" + CLIENT_ID + "&client_secret=" +
    CLIENT_SECRET + "&redirect_uri=" +REDIRECT_URI+ "&grant_type=authorization_code";
	public static final String PHOTO_GET ="https://api.instagram.com/v1/users/self/media/recent/?access_token=";
	public static final String LOG_OUT_URI = "https://instagram.com/accounts/logout/";
	public static final String START_PAGE_URI = "https://instagram.com/";
}
