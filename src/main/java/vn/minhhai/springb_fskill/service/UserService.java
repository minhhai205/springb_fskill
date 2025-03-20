package vn.minhhai.springb_fskill.service;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import vn.minhhai.springb_fskill.dto.request.UserRequestDTO;
import vn.minhhai.springb_fskill.dto.response.PageResponse;
import vn.minhhai.springb_fskill.dto.response.UserDetailResponse;
import vn.minhhai.springb_fskill.util.UserStatus;

public interface UserService {
    UserDetailsService userDetailsService();

    long saveUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsers(int pageNo, int pageSize, String... sorts);

    PageResponse<?> getAllUsersAndSearchWithPagingAndSorting(int pageNo, int pageSize, String search, String sortBy);

    PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address,
            String... search);

    PageResponse<?> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] address);
}
