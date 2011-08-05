/*
 * Copyright (c) 2011 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.auth.facebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import com.facebook.android.Facebook;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 *
 */
public class FacebookService {
	
	private Activity context;
	private Facebook facebook; 
	private FacebookSessionStore facebookSessionStore; 
	private AuthProviderListener listener;
	
	public FacebookService(Activity context, Facebook facebook, FacebookSessionStore facebookSessionStore, AuthProviderListener listener) {
		super();
		this.context = context;
		
		this.facebook = facebook;
		this.facebookSessionStore = facebookSessionStore;
		this.listener = listener;
	}

	public void authenticate() {
		authenticate(new String[]{});
	}
	
	public void authenticate(String[] permissions) {
		facebookSessionStore.restore(facebook, context);
		
		FacebookDialogListener facebookDialogListener = new FacebookDialogListener(context, facebook, facebookSessionStore, listener) {
			
			@Override
			public void onFinish() {
				finish();
			}
			
			@Override
			public void handleError(Throwable error) {
				if(listener != null) {
					listener.onError(new SocializeException(error));
				}
				else {
					doError(error);
				}
			}
		};
		
		facebook.authorize(context, permissions, facebookDialogListener);
	}
	
	
	public void logout() {
		try {
			facebook.logout(context);
		}
		catch (Exception e) {
			if(listener != null) {
				listener.onError(new SocializeException(e));
			}
			else {
				e.printStackTrace();
			}
		}
	}
	
	public void doError(final Throwable e) {
		context.runOnUiThread(new Runnable() {
			public void run() {
				Log.e(SocializeLogger.LOG_TAG, "Facebook error", e);
				doErrorUI(e.getMessage());
			}
		});
	}
	
	public void doErrorUI(final String error) {
		
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Oops!");
			builder.setMessage("Oops!\nSomething went wrong...");
			builder.setCancelable(false);
			builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					authenticate();
				}
			});	
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					finish();
				}
			});	
			builder.create().show();
		}
		catch (Exception e) {
			Log.e(SocializeLogger.LOG_TAG, "Facebook error", e);
		}
	}
	
	public void finish() {
		context.finish();
	}
	
}
