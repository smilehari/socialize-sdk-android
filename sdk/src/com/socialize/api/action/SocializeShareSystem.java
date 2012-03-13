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
package com.socialize.api.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import com.socialize.Socialize;
import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.share.ShareListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkSharer;
import com.socialize.provider.SocializeProvider;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeShareSystem extends SocializeApi<Share, SocializeProvider<Share>> implements ShareSystem {
	
	private Map<String, SocialNetworkSharer> sharers;
	private SocializeLogger logger;
	
	public SocializeShareSystem(SocializeProvider<Share> provider) {
		super(provider);
	}
	
	@Override
	public void addShare(Context context, SocializeSession session, Entity entity, String text, SocialNetwork network, Location location, ShareListener listener) {
		addShare(context, session, entity, text, null, network, location, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.ShareSystem#addShare(android.content.Context, com.socialize.api.SocializeSession, com.socialize.entity.Entity, java.lang.String, com.socialize.api.action.ShareType, android.location.Location, com.socialize.listener.share.ShareListener)
	 */
	@Override
	public void addShare(Context context, SocializeSession session, Entity entity, String text, ShareType shareType, Location location, ShareListener listener) {
		addShare(context, session, entity, text, shareType, null, location, listener);
	}
	
	protected void addShare(
			final Context context, 
			final SocializeSession session, 
			final Entity entity, 
			final String text, 
			ShareType shareType, 
			final SocialNetwork network, 
			final Location location, 
			final ShareListener listener) {
		
		if(shareType == null) {
			if(network != null) {
				shareType = ShareType.valueOf(network.name().toUpperCase());
			}
			else {
				shareType = ShareType.OTHER;
			}
		}
		
		final ShareType fshareType = shareType; 
		
		if(network != null) {
			AuthProviderType authType = AuthProviderType.valueOf(network);
			if(Socialize.getSocialize().isAuthenticated(authType)) {
				doShare(session, entity, text, shareType, network, location, listener);
			}
			else {
				Socialize.getSocialize().authenticate(context, authType, new SocializeAuthListener() {
					@Override
					public void onError(SocializeException error) {
						if(listener != null) {
							listener.onError(error);
						}
					}
					
					@Override
					public void onCancel() {
						// no network
						doShare(session, entity, text, fshareType, null, location, listener);
					}
					
					@Override
					public void onAuthSuccess(SocializeSession session) {
						doShare(session, entity, text, fshareType, network, location, listener);
					}
					
					@Override
					public void onAuthFail(SocializeException error) {
						if(listener != null) {
							listener.onError(error);
						}
					}
				});
			}
		}
		else {
			// no network
			doShare(session, entity, text, fshareType, null, location, listener);	
		}
	}
	
	protected void doShare(SocializeSession session, Entity entity, String text, ShareType shareType, SocialNetwork network, Location location, ShareListener listener) {
		
		if(StringUtils.isEmpty(text)) {
			text = entity.getDisplayName();
		}
		
		Share c = new Share();
		c.setEntity(entity);
		c.setText(text);
		c.setMedium(shareType.getId());
		c.setMediumName(shareType.getName());
		
		if(network != null) {
			ShareOptions shareOptions = new ShareOptions();
			shareOptions.setShareTo(network);
			shareOptions.setShareLocation(true);
			setPropagationData(c, shareOptions);
		}
	
		setLocation(c, location);
		
		List<Share> list = new ArrayList<Share>(1);
		list.add(c);
		
		postAsync(session, ENDPOINT, list, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.ShareSystem#getSharesByEntity(com.socialize.api.SocializeSession, java.lang.String, int, int, com.socialize.listener.share.ShareListener)
	 */
	@Override
	public void getSharesByEntity(SocializeSession session, String key, int startIndex, int endIndex, ShareListener listener) {
		listAsync(session, ENDPOINT, key, null, startIndex, endIndex, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.ShareSystem#getSharesByUser(com.socialize.api.SocializeSession, long, com.socialize.listener.share.ShareListener)
	 */
	@Override
	public void getSharesByUser(SocializeSession session, long userId, ShareListener listener) {
		String endpoint = "/user/" + userId + ENDPOINT;
		listAsync(session, endpoint, listener);
	}

	@Override
	public void shareEntity(Activity context, Entity entity, String comment, Location location, SocialNetwork destination, boolean autoAuth, SocialNetworkListener listener) {
		SocialNetworkSharer sharer = getSharer(destination);
		if(sharer != null) {
			sharer.shareEntity(context, entity, comment, autoAuth, listener);
		}
	}
	
	@Override
	public void shareComment(Activity context, Entity entity, String comment, Location location, SocialNetwork destination, boolean autoAuth, SocialNetworkListener listener) {
		SocialNetworkSharer sharer = getSharer(destination);
		if(sharer != null) {
			sharer.shareComment(context, entity, comment, autoAuth, listener);
		}
	}

	@Override
	public void shareLike(Activity context, Entity entity, String comment, Location location, SocialNetwork destination, boolean autoAuth, SocialNetworkListener listener) {
		SocialNetworkSharer sharer = getSharer(destination);
		if(sharer != null) {
			sharer.shareLike(context, entity, comment, autoAuth, listener);
		}
	}

	protected SocialNetworkSharer getSharer(SocialNetwork destination) {
		SocialNetworkSharer sharer = null;
		
		if(sharers != null) {
			sharer = sharers.get(destination.name().toLowerCase());
		}
		
		if(sharer == null) {
			if(logger != null) {
				logger.warn("No sharer found for network type [" +
						destination.name() +
						"]");
			}
		}	
		
		return sharer;
		
	}

	public void setSharers(Map<String, SocialNetworkSharer> sharers) {
		this.sharers = sharers;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}	
}
