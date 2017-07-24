/**
 * 
 */
package org.springframework.social.config.annotation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.ConnectionFactoryLocator;

/**
 * @author jhcao
 *
 */
@Configuration
public class MobileSocialConfiguration extends SocialConfiguration {

	@Autowired
	private Environment environment;
	@Autowired
	private List<SocialConfigurer> socialConfigurers;

	@Bean
	@Primary
	public ConnectionFactoryLocator connectionFactoryLocator() {
		MobileSecurityEnabledConnectionFactoryConfigurer cfConfig = new MobileSecurityEnabledConnectionFactoryConfigurer();
		for (SocialConfigurer socialConfigurer : socialConfigurers) {
			socialConfigurer.addConnectionFactories(cfConfig, environment);
		}
		return cfConfig.getConnectionFactoryLocator();	
	}

}
