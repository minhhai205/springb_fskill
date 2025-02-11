package vn.minhhai.springb_fskill.service.impl;

import org.springframework.stereotype.Service;

import vn.minhhai.springb_fskill.dto.request.UserRequestDTO;
import vn.minhhai.springb_fskill.exception.InvalidDataException;
import vn.minhhai.springb_fskill.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public int addUser(UserRequestDTO user) {
        if (!user.getFirstName().equals("user")) {
            throw new InvalidDataException("Add user that bai");
        }
        return 0;
    }

}
