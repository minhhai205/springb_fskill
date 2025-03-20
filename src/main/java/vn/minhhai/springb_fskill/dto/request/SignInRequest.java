package vn.minhhai.springb_fskill.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import vn.minhhai.springb_fskill.util.Platform;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {

    @NotBlank(message = "username must be not null")
    private String username;

    @NotBlank(message = "username must be not blank")
    private String password;

    @NotNull(message = "username must be not null")
    private Platform platform; // Thiết bị login

    private String deviceToken; // Khi viết API cho mobile thì mỗi thiết bị cần 1 device token

    private String version;
}
