package co.retaila.athena.modules.role.queries;

import co.retaila.athena.common.entities.BaseEntity;
import co.retaila.athena.common.entities.QRole;
import co.retaila.athena.common.entities.Role;
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
public class SearchRolesQuery extends BaseQuery {

    private String name;
    private String domain;

    @Override
    protected Class<? extends BaseEntity> getSortEntityClass() {
        return Role.class;
    }

    @Override
    protected Predicate getPredicate(BooleanExpression expression) {
        if (StringUtils.isNotBlank(name)) {
            expression = expression.and(QRole.role.name.contains(name));
        }

        if (StringUtils.isNotBlank(domain)) {
            expression = expression.and(QRole.role.domain.name.contains(domain));
        }

        return expression;
    }

}
