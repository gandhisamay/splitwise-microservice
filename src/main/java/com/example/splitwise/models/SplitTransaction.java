package com.example.splitwise.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
/*
public class SplitTransaction {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private SplitUser payer;

    @ManyToMany
    private List<SplitUser> participants;

    private double amount;
    private int groupId;
    private String description;
}
*/

//public class SplitTransaction {
//
//    @Id
//    @GeneratedValue
//    private int id;
//
//    @ManyToOne
//    @JoinColumn(name = "payer_id")
//    private SplitUser payer;
//
//    @ManyToMany
//    @JoinTable(
//            name = "split_transaction_participants",
//            joinColumns = @JoinColumn(name = "transaction_id"),
//            inverseJoinColumns = @JoinColumn(name = "participant_id")
//    )
//    private Map<SplitUser, double> participants;
//
//    private double amount;
//    private int groupId;
//    private String description;
//
//    //we need percentages as well
//    //update the user owed balances as well.
//    //and make this all transactional
//}

public class SplitTransaction {

    private int idempotencyKey;

    @Id
    @GeneratedValue
    private int transactionId;

    @ManyToOne
    @JoinColumn(name = "payer_id", referencedColumnName = "user_id")
    private SplitUser payer;

    @ElementCollection
    @CollectionTable(name = "split_transaction_participants", joinColumns = @JoinColumn(name = "transaction_id"))
    @MapKeyJoinColumn(name = "participant_id", referencedColumnName = "user_id")
    @Column(name = "amount_owed")
    private Map<SplitUser, Double> participantsAmounts;

    private double amount;
    private int groupId;
    private String description;
    private String category;

    // Getters and setters
}
