package vn.minhhai.springb_fskill.dto.response;

import java.io.Serializable;
import java.util.Date;

import lombok.*;
import vn.minhhai.springb_fskill.util.Gender;
import vn.minhhai.springb_fskill.util.UserStatus;

@Builder
@Getter
public class UserDetailResponse implements Serializable {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Date dateOfBirth;

    private Gender gender;

    private String username;

    private String type;

    private UserStatus status;
}