package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class Transfer {

    @Getter
    @NotNull
    @NotEmpty
    private final String accountFrom;

    @Getter
    @NotNull
    @NotEmpty
    private final String accountTo;

    @NotNull
    @Min(value = 0, message = "Transfer amount must be greater than zero.")
    private BigDecimal amount;

     @JsonCreator
    public Transfer(@JsonProperty("accountFrom") String accountFrom,
                   @JsonProperty("accountTo") String accountTo,
                   @JsonProperty("amount") BigDecimal amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }
    
}
