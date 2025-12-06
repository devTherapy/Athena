package co.retaila.athena.modules.domain.queries;

import co.retaila.athena.common.entities.BaseEntity;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.QDomain;
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
public class SearchDomainsQuery extends BaseQuery {

    private String name;

    @Override
    protected Class<? extends BaseEntity> getSortEntityClass() {
        return Domain.class;
    }

    @Override
    protected Predicate getPredicate(BooleanExpression expression) {
        if (StringUtils.isNotBlank(name)) {
            expression = expression.and(QDomain.domain.name.contains(name));
        }

        return expression;
    }

}
