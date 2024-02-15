package com.example.splitwise.models.transaction;

import com.example.splitwise.models.user.SplitUser;
import com.example.splitwise.models.user.SplitUserCompressed;
import lombok.Builder;
import lombok.Data;
import org.apache.catalina.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Data
public class SplitTransactionResponse {
    private int transactionId;
    private SplitUserCompressed payer;
    private List<SplitUserAmount> participantsAmounts;
    private String category;
    private String description;
    private int groupId;


    public static SplitTransactionResponse toDto(SplitTransaction transaction) {
        SplitUser payer = transaction.getPayer();
        SplitUserCompressed payerCompressed = SplitUserCompressed.builder().userId(payer.getUserId()).email(payer.getEmail()).name(payer.getName()).build();

        List<SplitUserAmount> participantsAmountsCompressed = transaction.getParticipantsAmounts().entrySet().stream().map(entry -> SplitUserAmount.builder().amount(entry.getValue()).userId(entry.getKey().getUserId()).name(entry.getKey().getName()).email(entry.getKey().getEmail()).build()).collect(Collectors.toList());
        return SplitTransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .payer(payerCompressed)
                .participantsAmounts(participantsAmountsCompressed)
                .category(transaction.getCategory())
                .description(transaction.getDescription())
                .groupId(transaction.getGroupId())
                .build();
    }
}
