package vn.minhhai.springb_fskill.service;

import vn.minhhai.springb_fskill.dto.request.UserRequestDTO;

public interface UserService {
    int addUser(UserRequestDTO user);
}
