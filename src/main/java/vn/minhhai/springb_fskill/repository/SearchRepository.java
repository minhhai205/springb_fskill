package vn.minhhai.springb_fskill.repository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import vn.minhhai.springb_fskill.dto.response.PageResponse;

@Repository
@Slf4j
public class SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String LIKE_FORMAT = "%%%s%%";

    /**
     * Search user using keyword and
     *
     * @param pageNo
     * @param pageSize
     * @param search
     * @param sortBy
     * @return user list with sorting and paging
     */
    public PageResponse<?> searchUser(int pageNo, int pageSize, String search, String sortBy) {
        log.info("Execute search user with keyword={}", search);

        // Xây dựng truy vấn động
        StringBuilder sqlQuery = new StringBuilder(
                "SELECT new vn.minhhai.springb_fskill.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.email, u.phone, u.dateOfBirth, u.gender, u.username, u.type, u.status) FROM User u WHERE 1=1");

        // Thêm điều kiện tìm kiếm
        if (StringUtils.hasLength(search)) {
            sqlQuery.append(" AND lower(u.firstName) like lower(:firstName)");
            sqlQuery.append(" OR lower(u.lastName) like lower(:lastName)");
            sqlQuery.append(" OR lower(u.email) like lower(:email)");
        }

        // Xử lý sắp xếp (Sorting)
        if (StringUtils.hasLength(sortBy)) {
            // value:asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                sqlQuery.append(String.format(" ORDER BY u.%s %s", matcher.group(1),
                        matcher.group(3)));
            }
        }

        // Tạo đối tượng Query từ chuỗi truy vấn đã xây dựng
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());

        // Nếu có giá trị tìm kiếm, set các tham số cho truy vấn
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter("firstName", String.format(LIKE_FORMAT, search));
            selectQuery.setParameter("lastName", String.format(LIKE_FORMAT, search));
            selectQuery.setParameter("email", String.format(LIKE_FORMAT, search));
        }

        // Phân trang (Pagination)
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);

        // Lấy giá trị danh sách user
        List<?> users = selectQuery.getResultList();

        // Count users
        StringBuilder sqlCountQuery = new StringBuilder("SELECT COUNT(*) FROM User u");

        // Xét giá trị thỏa mãn search
        if (StringUtils.hasLength(search)) {
            sqlCountQuery.append(" WHERE lower(u.firstName) like lower(?1)");
            sqlCountQuery.append(" OR lower(u.lastName) like lower(?2)");
            sqlCountQuery.append(" OR lower(u.email) like lower(?3)");
        }

        // Tạo đối tượng query cho count user
        Query countQuery = entityManager.createQuery(sqlCountQuery.toString());

        // Nếu có giá trị tìm kiếm, set các tham số cho truy vấn
        if (StringUtils.hasLength(search)) {
            countQuery.setParameter(1, String.format(LIKE_FORMAT, search));
            countQuery.setParameter(2, String.format(LIKE_FORMAT, search));
            countQuery.setParameter(3, String.format(LIKE_FORMAT, search));
            countQuery.getSingleResult();
        }

        // Lấy giá trị tổng số user
        Long totalElements = (Long) countQuery.getSingleResult();
        log.info("totalElements={}", totalElements);

        // Tạo đối tượng PageImpl với danh sách kết quả, thông tin phân trang và tổng số
        // phần tử.
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<?> page = new PageImpl<>(users, pageable, totalElements);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(users)
                .build();
    }

}
