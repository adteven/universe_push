package com.comsince.github.security;


import java.io.IOException;

public class TokenAuthenticator implements IAuthenticator, ITokenGenerator {
    public static void main(String[] args) throws IOException {
    	TokenAuthenticator authenticator = new TokenAuthenticator();
    	String strToken = authenticator.generateToken("user1");
    	if (authenticator.checkValid(null, "user1", strToken.getBytes())) {
			System.out.println("pass" + strToken);
		} else {
			System.out.println("fail" + strToken);
		}
    }


	@Override
	public boolean checkValid(String clientId, String username, byte[] password) {
        String id = Tokenor.getUserId(password);
        if (id != null && id.equals(username)) {
            return true;
        }
		return false;
	}

	@Override
	public String generateToken(String username) {
		return Tokenor.getToken(username);
	}
}
