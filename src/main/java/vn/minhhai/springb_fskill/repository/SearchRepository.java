package vn.minhhai.springb_fskill.repository;

import java.util.ArrayList;
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
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import vn.minhhai.springb_fskill.dto.response.PageResponse;
import vn.minhhai.springb_fskill.dto.response.criteria.SearchCriteria;
import vn.minhhai.springb_fskill.dto.response.criteria.SearchQueryCriteriaConsumer;
import vn.minhhai.springb_fskill.model.Address;
import vn.minhhai.springb_fskill.model.User;

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

    /**
     * Advance search user by criterias
     *
     * @param offset
     * @param pageSize
     * @param sortBy
     * @param search
     * @return
     */
    public PageResponse<?> searchUserByCriteria(int offset, int pageSize, String sortBy, String address,
            String... search) {
        log.info("Search user with search={} and sortBy={}", search, sortBy);

        List<SearchCriteria> criteriaList = new ArrayList<>();

        if (search.length > 0) {
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(.*)");
            for (String s : search) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        List<User> users = getUsers(offset, pageSize, criteriaList, address, sortBy);

        Long totalElements = getTotalElements(criteriaList);

        Page<User> page = new PageImpl<>(users, PageRequest.of(offset, pageSize), totalElements);

        return PageResponse.builder()
                .pageNo(offset) // vi tri cua bản gi se lay ???
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(users)
                .build();
    }

    /**
     * Get all users with conditions
     *
     * @param offset
     * @param pageSize
     * @param criteriaList
     * @param sortBy
     * @return
     */
    private List<User> getUsers(int offset, int pageSize, List<SearchCriteria> criteriaList, String address,
            String sortBy) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);
        Predicate userPredicate = criteriaBuilder.conjunction();

        SearchQueryCriteriaConsumer searchConsumer = new SearchQueryCriteriaConsumer(userPredicate,
                criteriaBuilder, userRoot);

        // Vẫn đang tìm kiếm phân biệt chữ hoa chữ thường

        // Khi duyệt qua từng SearchCriteria, điều kiện mới sẽ được kết hợp với điều
        // kiện cũ bằng builder.and(predicate, ...).
        criteriaList.forEach(searchConsumer);

        userPredicate = searchConsumer.getPredicate();

        if (StringUtils.hasLength(address)) {
            Join<Address, User> userAddressJoin = userRoot.join("addresses");
            Predicate addressPredicate = criteriaBuilder.equal(userAddressJoin.get("city"), address);
            query.where(criteriaBuilder.and(userPredicate, addressPredicate));
            // tim kiem tren tat ca cac filed cua address thế nào ???
        } else {
            query.where(userPredicate);
        }

        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);

            if (matcher.find()) {
                String fieldName = matcher.group(1);
                String direction = matcher.group(3);

                if (direction.equalsIgnoreCase("asc")) {
                    query.orderBy(criteriaBuilder.asc(userRoot.get(fieldName)));
                } else { // có thể xét thêm 1 ngoại lệ custom ở đây nếu kh phải desc|asc
                    query.orderBy(criteriaBuilder.desc(userRoot.get(fieldName)));
                }
            }
        }

        return entityManager.createQuery(query)
                .setFirstResult(offset) // vi tri cua bản gi se lay ???
                .setMaxResults(pageSize)
                .getResultList();
    }

    /**
     * Count users with conditions
     *
     * @param params
     * @return
     */
    private Long getTotalElements(List<SearchCriteria> params) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        Predicate predicate = criteriaBuilder.conjunction();
        SearchQueryCriteriaConsumer searchConsumer = new SearchQueryCriteriaConsumer(predicate, criteriaBuilder, root);

        // Thêm address như tren nếu cần
        params.forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();
        query.select(criteriaBuilder.count(root));
        query.where(predicate);

        return entityManager.createQuery(query).getSingleResult();
    }

}
