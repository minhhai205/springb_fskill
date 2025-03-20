package vn.minhhai.springb_fskill.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class TokenResponse implements Serializable {

    private String accessToken;

    private String refreshToken;

    private Long userId;

    // Có thể trả về thêm các cái khác cần thiết như tiền tệ, vùng miền, ... nếu cần
}
