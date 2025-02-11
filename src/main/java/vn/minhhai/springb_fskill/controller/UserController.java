package vn.minhhai.springb_fskill.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.minhhai.springb_fskill.dto.request.UserRequestDTO;
import vn.minhhai.springb_fskill.dto.response.ResponseData;
import vn.minhhai.springb_fskill.dto.response.ResponseError;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class UserController {

    @PostMapping("/user/create")
    public ResponseData<Integer> addUser(@Valid @RequestBody UserRequestDTO userDTO) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Add successFully", 1);
    }

    @PutMapping("user/{id}")
    public ResponseData<?> updateUser(@PathVariable int id, @RequestBody UserRequestDTO userDTO) {
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Updated successFully");
    }

    @PatchMapping("user/{id}") // required : tham số không bắt buộc
    public ResponseData<?> changeUserStatus(@PathVariable int id, @RequestParam(required = false) boolean status) {
        // return new ResponseData<>(HttpStatus.ACCEPTED.value(), "User Status
        // changed");
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change Status failed");
    }

    @DeleteMapping("user/delete/{id}")
    public ResponseData<?> deleteUser(@PathVariable int id) {
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "User deleted");
    }

    @GetMapping("user/{id}")
    public ResponseData<UserRequestDTO> getUser(@PathVariable int id) {
        return new ResponseData<>(HttpStatus.OK.value(), "User deleted",
                new UserRequestDTO("Minh", "Hai", "admin@gmail.vn", "0123456789"));
    }

    @GetMapping("/user") // defaultValue : tham số mặc định
    public ResponseData<List<UserRequestDTO>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        return new ResponseData<>(HttpStatus.OK.value(), "User deleted",
                List.of(
                        new UserRequestDTO("user", "1", "user1@example.com", "12345"),
                        new UserRequestDTO("user", "1", "user2@example.com", "12345")));

    }

}
