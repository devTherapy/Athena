package co.retaila.athena.modules.authority.queries;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.BaseEntity;
import co.retaila.athena.common.entities.QAuthority;
import co.retaila.athena.common.queries.BaseQuery;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchAuthoritiesQuery extends BaseQuery {

    private String name;
    private String domain;

    @Override
    protected Class<? extends BaseEntity> getSortEntityClass() {
        return Authority.class;
    }

    @Override
    protected Predicate getPredicate(BooleanExpression expression) {
        if (StringUtils.isNotBlank(name)) {
            expression = expression.and(QAuthority.authority.name.contains(name));
        }

        if (StringUtils.isNotBlank(domain)) {
            expression = expression.and(QAuthority.authority.domain.name.contains(domain));
        }

        return expression;
    }

}
