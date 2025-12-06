package co.retaila.athena.modules.client.queries;

import co.retaila.athena.common.entities.BaseEntity;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.QClient;
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
public class SearchClientsQuery extends BaseQuery {

    private String identifier;
    private String name;

    @Override
    protected Class<? extends BaseEntity> getSortEntityClass() {
        return Client.class;
    }

    @Override
    protected Predicate getPredicate(BooleanExpression expression) {
        if (StringUtils.isNotBlank(identifier)) {
            expression = expression.and(QClient.client.identifier.contains(identifier));
        }

        if (StringUtils.isNotBlank(name)) {
            expression = expression.and(QClient.client.name.contains(name));
        }

        return expression;
    }

}
