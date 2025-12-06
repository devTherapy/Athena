package co.retaila.athena.modules.resourceserver.queries;

import co.retaila.athena.common.entities.BaseEntity;
import co.retaila.athena.common.entities.QResourceServer;
import co.retaila.athena.common.entities.ResourceServer;
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
public class SearchResourceServersQuery extends BaseQuery {

    private String resourceId;

    @Override
    protected Class<? extends BaseEntity> getSortEntityClass() {
        return ResourceServer.class;
    }

    @Override
    protected Predicate getPredicate(BooleanExpression expression) {
        if (StringUtils.isNotBlank(resourceId)) {
            expression = expression.and(QResourceServer.resourceServer.resourceId.contains(resourceId));
        }

        return expression;
    }

}
