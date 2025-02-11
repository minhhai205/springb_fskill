package vn.minhhai.springb_fskill.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import vn.minhhai.springb_fskill.dto.request.UserRequestDTO;
import vn.minhhai.springb_fskill.dto.response.ResponseData;
import vn.minhhai.springb_fskill.dto.response.ResponseError;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@Validated
public class UserController {

    @PostMapping("/user/create")
    public ResponseData<Integer> addUser(@Valid @RequestBody UserRequestDTO userDTO) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Add successFully", 1);
    }

    @PutMapping("user/{id}")
    public ResponseData<?> updateUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") int id,
            @RequestBody UserRequestDTO userDTO) {
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Updated successFully");
    }

    @PatchMapping("user/{id}") // required : tham số không bắt buộc
    public ResponseData<?> changeUserStatus(
            @PathVariable @Min(value = 1, message = "userId must be greater than 0") int id,
            @RequestParam(required = false) boolean status) {
        // return new ResponseData<>(HttpStatus.ACCEPTED.value(), "User Status
        // changed");
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change Status failed");
    }

    @DeleteMapping("user/delete/{id}")
    public ResponseData<?> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") int id) {
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "User deleted");
    }

    @GetMapping("user/{id}")
    public ResponseData<UserRequestDTO> getUser(
            @PathVariable @Min(value = 1, message = "userId must be greater than 0") int id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get user successed",
                new UserRequestDTO("Minh", "Hai", "admin@gmail.vn", "0123456789"));
    }

    @GetMapping("/user") // defaultValue : tham số mặc định
    public ResponseData<List<UserRequestDTO>> getUsers(
            @Min(value = 1, message = "page must be greater than or equal to 1") @RequestParam(defaultValue = "1", required = false) int page,
            @Min(value = 10, message = "limit must be greater than or equal to 10") @RequestParam(defaultValue = "10", required = false) int limit) {

        return new ResponseData<>(HttpStatus.OK.value(), "Get user successed",
                List.of(
                        new UserRequestDTO("user", "1", "user1@example.com", "12345"),
                        new UserRequestDTO("user", "1", "user2@example.com", "12345")));

    }

}
