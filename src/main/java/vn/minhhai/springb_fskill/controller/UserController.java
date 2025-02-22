package vn.minhhai.springb_fskill.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.minhhai.springb_fskill.config.Translator;
import vn.minhhai.springb_fskill.dto.request.UserRequestDTO;
import vn.minhhai.springb_fskill.dto.response.ResponseData;
import vn.minhhai.springb_fskill.dto.response.ResponseError;
import vn.minhhai.springb_fskill.service.UserService;
import vn.minhhai.springb_fskill.util.UserStatus;

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
@Tag(name = "User Controller") // Đổi tên hiển thị
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @SuppressWarnings("unchecked")
    @PostMapping("/user/create")
    // Ctrl Click để xem các mô tả khác có thể viết
    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO userDTO) {
        // bắt ngoại lệ custom như ResourceNotFoundException trong hàm getUserById file
        // userServiceIpl hoặc ngoại lệ lưu database
        try {
            long userId = userService.saveUser(userDTO);
            return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), userId);
        } catch (Exception e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PutMapping("user/{id}")
    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    public ResponseData<?> updateUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") long id,
            @RequestBody UserRequestDTO userDTO) {
        log.info("Request update userId={}", id);

        // bắt ngoại lệ custom như ResourceNotFoundException trong hàm getUserById file
        // userServiceIpl hoặc ngoại lệ lưu database
        try {
            userService.updateUser(id, userDTO);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.upd.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage() + " Update user fail");
        }
    }

    @PatchMapping("user/{id}") // required : tham số không bắt buộc
    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    public ResponseData<?> changeUserStatus(
            @PathVariable @Min(value = 1, message = "userId must be greater than 0") long id,
            @RequestParam UserStatus status) {
        log.info("Request change status, userId={}", id);

        // bắt ngoại lệ custom như ResourceNotFoundException trong hàm getUserById file
        // userServiceIpl hoặc ngoại lệ lưu database
        try {
            userService.changeStatus(id, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.change.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage() + " Change status fail");
        }
    }

    @DeleteMapping("user/delete/{id}")
    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    public ResponseData<?> deleteUser(
            @PathVariable @Min(value = 1, message = "userId must be greater than 0") long id) {
        log.info("Request delete userId={}", id);

        // bắt ngoại lệ custom như ResourceNotFoundException trong hàm getUserById file
        // userServiceIpl hoặc ngoại lệ lưu database
        try {
            userService.deleteUser(id);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("user.del.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage() + " Delete user fail");
        }
    }

    @GetMapping("user/detail/{id}")
    @Operation(summary = "Get user detail", description = "Send a request via this API to get user information")
    public ResponseData<UserRequestDTO> getUser(
            @PathVariable @Min(value = 1, message = "userId must be greater than 0") int id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get user successed",
                new UserRequestDTO("Minh", "Hai", "admin@gmail.vn", "0123456789"));
    }

    @GetMapping("/user") // defaultValue : tham số mặc định
    @Operation(summary = "Get list of users per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")
    public ResponseData<List<UserRequestDTO>> getUsers(
            @Min(value = 1, message = "page must be greater than or equal to 1") @RequestParam(defaultValue = "1", required = false) int page,
            @Min(value = 10, message = "limit must be greater than or equal to 10") @RequestParam(defaultValue = "10", required = false) int limit) {

        return new ResponseData<>(HttpStatus.OK.value(), "Get user successed",
                List.of(
                        new UserRequestDTO("user", "1", "user1@example.com", "12345"),
                        new UserRequestDTO("user", "1", "user2@example.com", "12345")));

    }

}
