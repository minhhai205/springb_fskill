package vn.minhhai.springb_fskill.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import vn.minhhai.springb_fskill.model.User;

import static vn.minhhai.springb_fskill.repository.specification.SearchOperation.*;

import java.util.ArrayList;
import java.util.List;

public final class UserSpecificationsBuilder {

    public final List<SpecSearchCriteria> params;

    public UserSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    /**
     * Hàm with gọi đến with 6 tham số ở phia dưới nếu không truyền vào orPredicate
     * 
     * Chưa dùng đến
     */
    public UserSpecificationsBuilder with(final String key, final String operation, final String prefix,
            final Object value, final String suffix) {
        return with(null, key, operation, prefix, value, suffix);
    }

    public UserSpecificationsBuilder with(final String orPredicate, final String key, final String operation,
            final String prefix, final Object value, final String suffix) {

        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));

        /*
         * Nếu cả startWithAsterisk và endWithAsterisk thì cho tìm kiếm theo LIKE
         * Cũng có thể tìm kiếm theo ~ để tìm kiếm theo LIKE cho ngắn gọn
         */
        if (searchOperation != null) {
            if (searchOperation == EQUALITY) {
                final boolean startWithAsterisk = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX);
                final boolean endWithAsterisk = suffix != null && suffix.contains(ZERO_OR_MORE_REGEX);

                if (startWithAsterisk && endWithAsterisk) {
                    searchOperation = LIKE;
                } else if (startWithAsterisk) {
                    searchOperation = ENDS_WITH;
                } else if (endWithAsterisk) {
                    searchOperation = STARTS_WITH;
                }
            }

            // Xét nếu có cờ ' thì là tìm kiếm theo OR
            boolean orPre = StringUtils.hasLength(orPredicate) && orPredicate.equals(OR_PREDICATE_FLAG);

            params.add(new SpecSearchCriteria(orPre, key, searchOperation, value));
        }
        return this;
    }

    public Specification<User> build() {
        if (params.isEmpty())
            return null;

        Specification<User> result = new UserSpecification(params.get(0));

        // Nếu isOrPredicate() là true thì xét điều kiện nối Specification là OR, không
        // mặc định là AND.
        for (int i = 1; i < params.size(); i++) {
            result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(new UserSpecification(params.get(i)))
                    : Specification.where(result).and(new UserSpecification(params.get(i)));
        }

        return result;
    }

    /**
     * Truyền vào 1 {@link SpecSearchCriteria} để add thêm vào
     * List<SpecSearchCriteria> params ỏ trên nếu cần
     * 
     * CHưa dùng đến
     */
    public UserSpecificationsBuilder with(SpecSearchCriteria criteria) {
        params.add(criteria);
        return this;
    }
}
