package vn.minhhai.springb_fskill.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.minhhai.springb_fskill.dto.request.UserRequestDTO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class UserController {

    @PostMapping("/user/create")
    public String addUser(@Valid @RequestBody UserRequestDTO userDTO) {
        return "entity";
    }

    @PutMapping("user/{id}")
    public String updateUser(@PathVariable int id, @RequestBody UserRequestDTO userDTO) {
        return "updated";
    }

    @PatchMapping("user/{id}") // required : tham số không bắt buộc
    public String changeUserStatus(@PathVariable int id, @RequestParam(required = false) boolean status) {
        return "User Status changed";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        return "User deleted";
    }

    @GetMapping("/{id}")
    public UserRequestDTO getUser(@PathVariable int id) {
        return new UserRequestDTO("Minh", "Hai", "admin@gmail.vn", "0123456789");
    }

    @GetMapping("/user") // defaultValue : tham số mặc định
    public List<UserRequestDTO> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        return List.of(
                new UserRequestDTO("user", "1", "user1@example.com", "12345"),
                new UserRequestDTO("user", "1", "user2@example.com", "12345"));

    }

}
