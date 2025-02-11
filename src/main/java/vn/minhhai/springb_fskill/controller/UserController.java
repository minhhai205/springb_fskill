package vn.minhhai.springb_fskill.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.minhhai.springb_fskill.dto.request.UserRequestDTO;
import vn.minhhai.springb_fskill.dto.response.ResponseSuccess;

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
    // @ResponseStatus(HttpStatus.CREATED)
    public ResponseSuccess addUser(@Valid @RequestBody UserRequestDTO userDTO) {
        return new ResponseSuccess(HttpStatus.CREATED, "Add successFully", 1);
    }

    @PutMapping("user/{id}")
    // @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseSuccess updateUser(@PathVariable int id, @RequestBody UserRequestDTO userDTO) {
        return new ResponseSuccess(HttpStatus.ACCEPTED, "Updated successFully");
    }

    @PatchMapping("user/{id}") // required : tham số không bắt buộc
    // @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseSuccess changeUserStatus(@PathVariable int id, @RequestParam(required = false) boolean status) {
        return new ResponseSuccess(HttpStatus.ACCEPTED, "User Status changed");
    }

    @DeleteMapping("user/delete/{id}")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseSuccess deleteUser(@PathVariable int id) {
        return new ResponseSuccess(HttpStatus.NO_CONTENT, "User deleted");
    }

    @GetMapping("user/{id}")
    // @ResponseStatus(HttpStatus.OK)
    public ResponseSuccess getUser(@PathVariable int id) {
        return new ResponseSuccess(HttpStatus.OK, "User deleted",
                new UserRequestDTO("Minh", "Hai", "admin@gmail.vn", "0123456789"));
    }

    @GetMapping("/user") // defaultValue : tham số mặc định
    // @ResponseStatus(HttpStatus.OK)
    public ResponseSuccess getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        return new ResponseSuccess(HttpStatus.OK, "User deleted",
                List.of(
                        new UserRequestDTO("user", "1", "user1@example.com", "12345"),
                        new UserRequestDTO("user", "1", "user2@example.com", "12345")));

    }

}
