package vn.minhhai.springb_fskill.service.impl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import vn.minhhai.springb_fskill.service.JwtService;
import vn.minhhai.springb_fskill.util.TokenType;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryHour}")
    private long expiryHour;

    @Value("${jwt.expiryDay}")
    private long expiryDay;

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Override
    public String generateToken(UserDetails user) {
        return generateToken(new HashMap<>(), user);
    }

    @Override
    public String generateRefreshToken(UserDetails user) {
        return generateRefreshToken(new HashMap<>(), user);
    }

    private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims) // Đặt thông tin bổ sung vào token
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // Thời gian phát hành token
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expiryHour)) // Time hết hạn
                .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS256) // Ký token bằng khóa bí mật và
                                                                                    // thuật toán HS256
                .compact(); // Chuyển đổi thành chuỗi JWT hoàn chỉnh
    }

    private String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay))
                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Lấy khóa bí mật tương ứng với loại token.
     *
     * @param type Loại token (ACCESS_TOKEN hoặc REFRESH_TOKEN)
     * @return Đối tượng Key chứa khóa bí mật đã giải mã
     */
    private Key getKey(TokenType type) {
        if (TokenType.ACCESS_TOKEN.equals(type))
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
        else
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
    }

    /**
     * Trả về giá trị hợp lệ nếu username của userDetails trùng với username được
     * tách ra từ token
     */
    @Override
    public boolean isValid(String token, TokenType tokenType, UserDetails userDetails) {
        final String username = extractUsername(token, tokenType);
        return username.equals(userDetails.getUsername());
    }

    /**
     * Extract username từ token và trả về username
     */
    @Override
    public String extractUsername(String token, TokenType tokenType) {
        return extractClaim(token, tokenType, Claims::getSubject);
    }

    private <T> T extractClaim(String token, TokenType type, Function<Claims, T> claimResolver) {
        final Claims claims = extraAllClaim(token, type);
        return claimResolver.apply(claims);
    }

    private Claims extraAllClaim(String token, TokenType type) {
        return Jwts.parserBuilder().setSigningKey(getKey(type)).build().parseClaimsJws(token).getBody();
    }

}
