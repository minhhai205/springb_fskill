package vn.minhhai.springb_fskill.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.minhhai.springb_fskill.dto.request.AddressDTO;
import vn.minhhai.springb_fskill.dto.request.UserRequestDTO;
import vn.minhhai.springb_fskill.dto.response.PageResponse;
import vn.minhhai.springb_fskill.dto.response.UserDetailResponse;
import vn.minhhai.springb_fskill.exception.ResourceNotFoundException;
import vn.minhhai.springb_fskill.model.Address;
import vn.minhhai.springb_fskill.model.User;
import vn.minhhai.springb_fskill.repository.SearchRepository;
import vn.minhhai.springb_fskill.repository.UserRepository;
import vn.minhhai.springb_fskill.repository.specification.UserSpecificationsBuilder;
import vn.minhhai.springb_fskill.service.UserService;
import vn.minhhai.springb_fskill.util.UserStatus;
import vn.minhhai.springb_fskill.util.UserType;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SearchRepository searchRepository;

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
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .build();

        // Thêm address cho user và gán lại user vào address để tự động lưu address khi
        // lưu user
        // dựa vào cấu hình cascade = CascadeType.ALL
        request.getAddresses().forEach(a -> user.saveAddress(Address.builder()
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
        // Chú ý với nghiệp vụ từng dụ án để check chi tiết và xác thực các trường
        // như username, email kh đc trùng, gửi sms xác thực sđt, ...
        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        if (!request.getEmail().equals(user.getEmail())) {
            // check email from database if not exist then allow update email otherwise
            // throw exception
            user.setEmail(request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        user.setAddresses(convertToAddress(request.getAddresses()));
        userRepository.save(user);

        log.info("User has updated successfully, userId={}", userId);
    }

    @Override
    public void changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);

        log.info("User status has changed successfully, userId={}", userId);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
        log.info("User has deleted permanent successfully, userId={}", userId);
    }

    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        return UserDetailResponse.builder()
                .id(userId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .phone(user.getPhone())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .type(user.getType())
                .build();
    }

    @Override
    public PageResponse<?> getAllUsers(int pageNo, int pageSize, String... sorts) {

        // Danh sách các tiêu chí sắp xếp
        List<Sort.Order> orders = new ArrayList<>();

        if (sorts != null) {
            for (String sortBy : sorts) {
                log.info("sortBy: {}", sortBy);

                // Biểu thức chính quy để tách tên trường và hướng sắp xếp (asc/desc)
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);

                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("desc")) {
                        // Thêm hướng sắp xếp và tiêu chí cần sắp xếp vào danh sách cách tiêu trí sort
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    }
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(orders));

        // Thực hiện truy vấn lấy danh sách người dùng từ cơ sở dữ liệu theo phân trang
        Page<User> users = userRepository.findAll(pageable);

        List<UserDetailResponse> response = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()).toList();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .items(response)
                .build();
    }

    @Override
    public PageResponse<?> getAllUsersAndSearchWithPagingAndSorting(int pageNo, int pageSize, String search,
            String sort) {
        return searchRepository.searchUser(pageNo, pageSize, search, sort);
    }

    @Override
    public PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address,
            String... search) {
        return searchRepository.searchUserByCriteria(pageNo, pageSize, sortBy, address, search);
    }

    @Override
    public PageResponse<?> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] address) {
        log.info("getUsersBySpecifications");

        if (user != null && address != null) {
            return searchRepository.searchUserByCriteriaWithJoin(pageable, user, address);
        } else if (user != null) {
            UserSpecificationsBuilder builder = new UserSpecificationsBuilder();

            Pattern pattern = Pattern.compile("([']?)([\\w]+)([><:~!])(\\*?)([^*]+)(\\*?)");

            for (String s : user) {
                Matcher matcher = pattern.matcher(s);

                if (matcher.find()) {

                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4),
                            matcher.group(5), matcher.group(6));
                }
            }

            Page<User> users = userRepository.findAll(Objects.requireNonNull(builder.build()), pageable);

            return convertToPageResponse(users, pageable);

        }

        return convertToPageResponse(userRepository.findAll(pageable), pageable);
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found!"));
    }

    private Set<Address> convertToAddress(Set<AddressDTO> addresses) {
        Set<Address> result = new HashSet<>();
        addresses.forEach(a -> result.add(Address.builder()
                .apartmentNumber(a.getApartmentNumber())
                .floor(a.getFloor())
                .building(a.getBuilding())
                .streetNumber(a.getStreetNumber())
                .street(a.getStreet())
                .city(a.getCity())
                .country(a.getCountry())
                .addressType(a.getAddressType())
                .build()));
        return result;
    }

    /**
     * Convert Page<User> to PageResponse
     *
     * @param users
     * @param pageable
     * @return
     */
    private PageResponse<?> convertToPageResponse(Page<User> users, Pageable pageable) {
        List<UserDetailResponse> response = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()).toList();
        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(users.getTotalPages())
                .items(response)
                .build();
    }

}
