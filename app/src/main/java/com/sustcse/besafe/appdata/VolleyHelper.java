package com.sustcse.besafe.appdata;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.sustcse.besafe.utils.LruBitmapCache;


public class VolleyHelper {
	private static VolleyHelper INSTANCE;
	private RequestQueue requestQueue;

	private ImageLoader mImageLoader;
	LruBitmapCache mLruBitmapCache;
	private Context context;

	private VolleyHelper(Context context) {
		this.context = context;
		this.requestQueue = getRequestQueue();
	}

	public static synchronized VolleyHelper getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new VolleyHelper(context);
		}
		return INSTANCE;
	}

	public RequestQueue getRequestQueue() {
		if (requestQueue == null) {
			requestQueue = Volley.newRequestQueue(context
					.getApplicationContext());
		}
		return requestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			getLruBitmapCache();
			mImageLoader = new ImageLoader(this.requestQueue, mLruBitmapCache);
		}

		return this.mImageLoader;
	}

	public LruBitmapCache getLruBitmapCache() {
		if (mLruBitmapCache == null)
			mLruBitmapCache = new LruBitmapCache();
		return this.mLruBitmapCache;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}

}
