package org.springframework.social.facebook.security;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.provider.OAuth2AuthenticationService;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author jhcao
 *
 */
public class MobileFacebookAuthenticationService extends OAuth2AuthenticationService<Facebook> {

	/**
	 * @param apiKey
	 * @param appSecret
	 */
	public MobileFacebookAuthenticationService(String apiKey, String appSecret) {
		super(new FacebookConnectionFactory(apiKey, appSecret));	
	}

	public MobileFacebookAuthenticationService(OAuth2ConnectionFactory<Facebook> connectionFactory) {
		super(connectionFactory);		
	}
	
	/**
	 * Override OAuth2AuthenticationService
	 * access_token should be provided in the body part, either plain text or encrypted by POST.
	 */
	@Override
	public SocialAuthenticationToken getAuthToken(HttpServletRequest request, HttpServletResponse response) throws SocialAuthenticationRedirectException {
		String agent = request.getHeader("agent-type");
		if(StringUtils.hasText(agent) && "mobile".equalsIgnoreCase(agent)) {	//mobile
			logger.debug("client is using mobile and should provide access_token");
			try {
				BufferedReader reader = request.getReader();
				AuthResponse authResponse = (new ObjectMapper()).readValue(reader, AuthResponse.class);
				//TODO: 1st exchange for a long-lived token or validate it
				//if validate, see {@link https://developers.facebook.com/docs/facebook-login/access-tokens#apptokens}
//				GET /oauth/access_token
//			    ?client_id={app-id}
//			    &client_secret={app-secret}
//			    &grant_type=client_credentials
				//convert AuthResponse to regular AccessGrant
				AccessGrant accessGrant = new AccessGrant(authResponse.getAccessToken(), authResponse.getScope(), null, authResponse.getExpiresIn());
				Connection<Facebook> connection = getConnectionFactory().createConnection(accessGrant);
				//TODO: find appName from URL by request.getServletPath()?			
				return new SocialAuthenticationToken(connection, null);
				
			} catch (IOException e) {
				e.printStackTrace();			
			}

			return null;
		}else {//if not mobile, use original method
			return super.getAuthToken(request, response);
		}
	}
	
	

}

