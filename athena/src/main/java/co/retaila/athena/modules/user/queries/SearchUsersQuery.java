package co.retaila.athena.modules.user.queries;

import co.retaila.athena.common.entities.BaseEntity;
import co.retaila.athena.common.entities.QUser;
import co.retaila.athena.common.entities.User;
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
public class SearchUsersQuery extends BaseQuery {

    private String username;
    private String email;
    private Boolean emailVerified;
    private String phoneNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String domain;

    @Override
    protected Class<? extends BaseEntity> getSortEntityClass() {
        return User.class;
    }

    @Override
    protected Predicate getPredicate(BooleanExpression expression) {
        if (StringUtils.isNotBlank(username))
            expression = expression.and(QUser.user.username.contains(username));

        if (StringUtils.isNotBlank(email))
            expression = expression.and(QUser.user.email.contains(email));

        if (emailVerified != null)
            expression = expression.and(QUser.user.emailVerified.eq(emailVerified));

        if (StringUtils.isNotBlank(phoneNumber))
            expression = expression.and(QUser.user.phoneNumber.contains(phoneNumber));

        if (StringUtils.isNotBlank(firstName))
            expression = expression.and(QUser.user.firstName.contains(firstName));

        if (StringUtils.isNotBlank(middleName))
            expression = expression.and(QUser.user.middleName.contains(middleName));

        if (StringUtils.isNotBlank(lastName))
            expression = expression.and(QUser.user.lastName.contains(lastName));

        if (StringUtils.isNotBlank(domain))
            expression = expression.and(QUser.user.domain.name.contains(domain));

        return expression;
    }

}
