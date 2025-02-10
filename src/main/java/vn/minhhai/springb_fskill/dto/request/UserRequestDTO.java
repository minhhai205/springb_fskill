package vn.minhhai.springb_fskill.dto.request;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vn.minhhai.springb_fskill.util.EnumPattern;
import vn.minhhai.springb_fskill.util.Gender;
import vn.minhhai.springb_fskill.util.GenderSubset;
import vn.minhhai.springb_fskill.util.PhoneNumber;
import vn.minhhai.springb_fskill.util.UserStatus;

import static vn.minhhai.springb_fskill.util.Gender.*;

public class UserRequestDTO implements Serializable {
    @NotBlank(message = "firstName must be not blank") // Khong cho phep gia tri blank
    private String firstName;

    @NotNull(message = "lastName must be not null") // Khong cho phep gia tri null
    private String lastName;

    @Email(message = "email invalid format") // Chi chap nhan nhung gia tri dung dinh dang email
    private String email;

    // @Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
    @PhoneNumber
    private String phone;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    @GenderSubset(anyOf = { MALE, FEMALE })
    private Gender gender;

    @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;

    public UserRequestDTO(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Gender getGender() {
        return gender;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "UserRequestDTO [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", phone="
                + phone + "]";
    }

}
