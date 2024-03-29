package com.example.splitwise.models.moneyBalance;

import com.example.splitwise.models.group.SplitGroup;
import com.example.splitwise.models.user.SplitUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SplitMoneyBalance implements Serializable {
    @EmbeddedId
    private SplitMoneyBalanceId moneyBalanceId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", insertable = false, updatable = false, referencedColumnName = "user_id")
    private SplitUser payer;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false, referencedColumnName = "user_id")
    private SplitUser receiver;


    @ManyToOne
    @JoinColumn(name = "group_id", insertable = false, updatable = false, referencedColumnName = "group_id")
    private SplitGroup group;

    private double amount;
}
