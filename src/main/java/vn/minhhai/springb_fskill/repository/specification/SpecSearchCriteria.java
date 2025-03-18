package vn.minhhai.springb_fskill.repository.specification;

import static vn.minhhai.springb_fskill.repository.specification.SearchOperation.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SpecSearchCriteria {

    private String key;
    private SearchOperation operation;
    private Object value;
    private boolean orPredicate;

    public SpecSearchCriteria(final boolean orPredicate, final String key, final SearchOperation operation,
            final Object value) {
        super();
        this.orPredicate = orPredicate;
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(String orPredicate, String key, String operation, String prefix, String value,
            String suffix) {

        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));

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
        }

        this.key = key;
        this.operation = searchOperation;
        this.value = value;
    }

}
