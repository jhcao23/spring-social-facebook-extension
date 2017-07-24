package org.springframework.social.facebook.security;

import lombok.Getter;
import lombok.Setter;

public class AuthResponse {

	@Getter @Setter
	private String accessToken;
	@Getter @Setter
	private String signedRequest;
	@Getter @Setter
	private Long expiresIn;		
	@Getter @Setter
	private String userID;
	@Getter @Setter
	private String scope;
	
}
