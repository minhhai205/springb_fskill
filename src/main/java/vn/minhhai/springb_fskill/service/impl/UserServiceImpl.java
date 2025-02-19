package vn.minhhai.springb_fskill.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.minhhai.springb_fskill.dto.request.UserRequestDTO;
import vn.minhhai.springb_fskill.dto.response.UserDetailResponse;
import vn.minhhai.springb_fskill.model.Address;
import vn.minhhai.springb_fskill.model.User;
import vn.minhhai.springb_fskill.repository.UserRepository;
import vn.minhhai.springb_fskill.service.UserService;
import vn.minhhai.springb_fskill.util.UserStatus;
import vn.minhhai.springb_fskill.util.UserType;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

     /**
     * Save new user to DB
     *
     * @param request
     * @return userId
     */
    @Override
    public long saveUser(UserRequestDTO request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                // .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .build();

        // Thêm address cho user và gán lại user vào address để tự động lưu address khi lưu user
        // dựa vào cấu hình cascade = CascadeType.ALL
        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));
        userRepository.save(user);

        log.info("User has added successfully, userId={}", user.getId());

        return user.getId();
    }

    @Override
    public void updateUser(long userId, UserRequestDTO request) {
       
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public void changeStatus(long userId, UserStatus status) {
       
        throw new UnsupportedOperationException("Unimplemented method 'changeStatus'");
    }

    @Override
    public void deleteUser(long userId) {
       
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public UserDetailResponse getUser(long userId) {
      
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public List<UserDetailResponse> getAllUsers(int pageNo, int pageSize) {
       
        throw new UnsupportedOperationException("Unimplemented method 'getAllUsers'");
    }

}
