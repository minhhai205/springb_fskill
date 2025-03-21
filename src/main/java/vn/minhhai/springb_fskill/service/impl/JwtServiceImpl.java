package vn.minhhai.springb_fskill.service.impl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

}
