package vn.minhhai.springb_fskill.service;

import org.springframework.security.core.userdetails.UserDetails;

import vn.minhhai.springb_fskill.util.TokenType;

public interface JwtService {

    String generateToken(UserDetails user);

    String generateRefreshToken(UserDetails user);

    String extractUsername(String token, TokenType type);

    boolean isValid(String token, TokenType tokenType, UserDetails userDetails);
}
