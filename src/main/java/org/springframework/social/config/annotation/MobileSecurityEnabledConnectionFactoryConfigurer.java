package org.springframework.social.config.annotation;

import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.facebook.security.MobileFacebookAuthenticationService;
import org.springframework.social.security.SocialAuthenticationServiceRegistry;
import org.springframework.social.security.provider.OAuth1AuthenticationService;
import org.springframework.social.security.provider.OAuth2AuthenticationService;
import org.springframework.social.security.provider.SocialAuthenticationService;

public class MobileSecurityEnabledConnectionFactoryConfigurer implements ConnectionFactoryConfigurer {

	private SocialAuthenticationServiceRegistry registry;
		
	public ConnectionFactoryRegistry getConnectionFactoryLocator() {
		return registry;
	}
	
	public MobileSecurityEnabledConnectionFactoryConfigurer() {
		registry = new SocialAuthenticationServiceRegistry();
	}
	
	public void addConnectionFactory(ConnectionFactory<?> connectionFactory) {
		((SocialAuthenticationServiceRegistry)getConnectionFactoryLocator())
			.addAuthenticationService(wrapAsSocialAuthenticationService(connectionFactory));
	}
	
	@SuppressWarnings("unchecked")
	private <A> SocialAuthenticationService<A> wrapAsSocialAuthenticationService(ConnectionFactory<A> cf) {
		
		if (cf instanceof OAuth1ConnectionFactory) {
			return new OAuth1AuthenticationService<A>((OAuth1ConnectionFactory<A>) cf);
		} else if (cf instanceof OAuth2ConnectionFactory) {
			final OAuth2AuthenticationService<A> authService;
			if(cf instanceof FacebookConnectionFactory) {			
				authService = (OAuth2AuthenticationService<A>) new MobileFacebookAuthenticationService((FacebookConnectionFactory) cf);				
			}else {
				authService = new OAuth2AuthenticationService<A>((OAuth2ConnectionFactory<A>) cf);				
			}
			authService.setDefaultScope(((OAuth2ConnectionFactory<A>) cf).getScope());
			return authService;
		}
		throw new IllegalArgumentException("The connection factory must be one of OAuth1ConnectionFactory or OAuth2ConnectionFactory");
	}

}
