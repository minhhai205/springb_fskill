package vn.minhhai.springb_fskill.model;

import jakarta.persistence.*;
import lombok.*;
import vn.minhhai.springb_fskill.util.Gender;
import vn.minhhai.springb_fskill.util.UserStatus;
import vn.minhhai.springb_fskill.util.UserType;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_user")
public class User extends AbstractEntity<Long> implements UserDetails {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type")
    private UserType type;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private UserStatus status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    @JsonIgnore // Stop infinite loop
    @Builder.Default
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private Set<GroupHasUser> groups = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private Set<UserHasRole> roles = new HashSet<>();

    public void saveAddress(Address address) {
        if (address != null) {
            if (addresses == null) {
                addresses = new HashSet<>();
            }
            addresses.add(address);
            address.setUser(this); // save user_id
            // System.out.println(this.getId());
        }
    }

    /**
     * Trả về danh sách quyền (roles) của người dùng.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * Kiểm tra xem tài khoản có hết hạn không.
     * Nếu trả về true, tài khoản vẫn còn hạn sử dụng.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Kiểm tra xem tài khoản có bị khóa không.
     * Nếu trả về true, tài khoản không bị khóa.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Kiểm tra xem thông tin đăng nhập (mật khẩu) có hết hạn không.
     * Nếu trả về true, mật khẩu vẫn còn hiệu lực.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Kiểm tra xem tài khoản có đang hoạt động không.
     * Nếu trả về true, tài khoản được kích hoạt.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
