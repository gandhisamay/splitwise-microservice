package com.example.splitwise.models.moneyBalance;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
public class SplitMoneyBalanceId implements Serializable {
    @Column(name = "payer_id")
    private int payerId;

    @Column(name = "receiver_id")
    private int receiverId;

    @Column(name = "group_id")
    private int groupId;

    public SplitMoneyBalanceId getReverseMapping() {
        return SplitMoneyBalanceId.builder().receiverId(this.payerId).payerId(this.receiverId).groupId(this.groupId).build();
    }
}
