package co.retaila.athena.common.viewmodels;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseViewModel {

    private Long id;
    private LocalDateTime createdOn;
    private LocalDateTime lastModifiedOn;

}
