package com.example.splitwise.services;

import com.example.splitwise.exceptions.TransactionNotFoundException;
import com.example.splitwise.exceptions.UserNotFoundException;
import com.example.splitwise.models.moneyBalance.SplitMoneyBalance;
import com.example.splitwise.models.moneyBalance.SplitMoneyBalanceId;
import com.example.splitwise.models.transaction.*;
import com.example.splitwise.models.user.SplitUser;
import com.example.splitwise.models.user.SplitUserCompressed;
import com.example.splitwise.repositories.GroupRepository;
import com.example.splitwise.repositories.MoneyBalanceRepository;
import com.example.splitwise.repositories.TransactionRepository;
import com.example.splitwise.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final MoneyBalanceRepository moneyBalanceRepository;


    private String PAYER = "PAYER";
    private String AMOUNT = "AMOUNT";
    private String DESCRIPTION = "DESCRIPTION";
    private String CATEGORY = "CATEGORY";
//    private String PARTICIPANTS_AMOUNTS = "PARTICIPANTS_AMOUNTS";

    TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, MoneyBalanceRepository moneyBalanceRepository, GroupRepository groupRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.moneyBalanceRepository = moneyBalanceRepository;
        this.groupRepository = groupRepository;
    }

    public ResponseEntity<List<SplitTransactionResponse>> getAllTransactions() {
        List<SplitTransaction> transactions = transactionRepository.findAll();

        List<SplitTransactionResponse> response = transactions.stream().map(SplitTransactionResponse::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<SplitTransactionResponse> getTransactionById(int id) throws TransactionNotFoundException {
        SplitTransaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        SplitTransactionResponse successResponse = SplitTransactionResponse.toDto(transaction);
        return ResponseEntity.ok().body(successResponse);
    }

    @Transactional
    public ResponseEntity<String> createTransaction(SplitTransactionRequest transactionRequest) {
        //need to make this design wayyy better
        System.out.println(transactionRequest);
        //check first if the transaction is already completed or not

        boolean transactionExists = transactionRepository.existsByIdempotencyKey(transactionRequest.getIdempotencyKey());

        if (transactionExists) return ResponseEntity.ok().body("Transaction created successfully");

        SplitUser payer = userRepository.findById(transactionRequest.getPayerId()).orElseThrow(() -> new UserNotFoundException(transactionRequest.getPayerId()));
        List<SplitUser> participants = userRepository.findAllById(transactionRequest.getParticipantsAmounts().keySet());


        if (participants.size() != transactionRequest.getParticipantsAmounts().size()) {
            return ResponseEntity.badRequest().body("Participants ids are invalid");
        }

        boolean groupExists = groupRepository.existsById(transactionRequest.getGroupId());

        if (!groupExists) {
            return ResponseEntity.badRequest().body("Group does not exist");
        }

        Map<SplitUser, Double> updatedMap = transactionRequest.getParticipantsAmounts().entrySet().parallelStream().collect(HashMap::new, (map, entry) -> map.put(userRepository.findById(entry.getKey()).orElseThrow(() -> new UserNotFoundException(entry.getKey())), entry.getValue()), HashMap::putAll);

        participants.forEach((receiver) -> {

            //find if the entry exists
            SplitMoneyBalanceId direct = SplitMoneyBalanceId.builder().payerId(transactionRequest.getPayerId()).receiverId(receiver.getUserId()).groupId(transactionRequest.getGroupId()).build();
            SplitMoneyBalanceId inverse = direct.getReverseMapping();


            SplitMoneyBalance directBalance = moneyBalanceRepository.findById(direct).orElse(null);
            SplitMoneyBalance reverseBalance = moneyBalanceRepository.findById(inverse).orElse(null);


            double amountOwed = transactionRequest.getParticipantsAmounts().get(receiver.getUserId());

            if (directBalance == null && reverseBalance == null) {

                //we need to create here
                SplitMoneyBalance newBalance = SplitMoneyBalance.builder().amount(amountOwed).payer(payer).moneyBalanceId(new SplitMoneyBalanceId(payer.getUserId(), receiver.getUserId(), transactionRequest.getGroupId())).receiver(receiver).build();
                moneyBalanceRepository.save(newBalance);

            } else if (reverseBalance != null) {
                reverseBalance.setAmount(reverseBalance.getAmount() - amountOwed);
                moneyBalanceRepository.save(reverseBalance);
            } else {
                directBalance.setAmount(directBalance.getAmount() + amountOwed);
                moneyBalanceRepository.save(directBalance);
            }
        });

        SplitTransaction transaction = SplitTransaction.builder().idempotencyKey(transactionRequest.getIdempotencyKey()).participantsAmounts(updatedMap).payer(payer).groupId(transactionRequest.getGroupId()).amount(transactionRequest.getAmount()).description(transactionRequest.getDescription()).category(transactionRequest.getCategory()).build();

        transactionRepository.save(transaction);

        return ResponseEntity.ok().body("Transaction created successfully");

    }


    @Transactional
    public ResponseEntity<String> deleteTransaction(int id) {
        boolean transactionExists = transactionRepository.existsById(id);
        if (transactionExists) {
            SplitTransaction transaction = transactionRepository.findById(id).get();

            transaction.getParticipantsAmounts().forEach((key, value) -> {
                SplitMoneyBalanceId direct = SplitMoneyBalanceId.builder().payerId(transaction.getPayer().getUserId()).receiverId(key.getUserId()).groupId(transaction.getGroupId()).build();
                SplitMoneyBalanceId inverse = direct.getReverseMapping();


                SplitMoneyBalance directBalance = moneyBalanceRepository.findById(direct).orElse(null);
                SplitMoneyBalance reverseBalance = moneyBalanceRepository.findById(inverse).orElse(null);

                if (reverseBalance != null) {
                    reverseBalance.setAmount(reverseBalance.getAmount() + value);
                    moneyBalanceRepository.save(reverseBalance);
                } else if (directBalance != null) {
                    directBalance.setAmount(directBalance.getAmount() - value);
                    moneyBalanceRepository.save(directBalance);
                }
            });

            transactionRepository.deleteById(id);
            return new ResponseEntity<>("success", HttpStatus.OK);
        }

        return new ResponseEntity<>("transaction does not exist", HttpStatus.BAD_REQUEST);
    }

    private Optional<Double> getParticipantOwedAmount(SplitUser user, Map<SplitUser, Double> map) {
        return map.entrySet().stream().filter(entry -> entry.getKey().getUserId() == user.getUserId()).map(Map.Entry::getValue).findFirst();
    }


    @Transactional
    public ResponseEntity<String> updateTransaction(SplitTransactionUpdateRequest transactionUpdateRequest) {

        boolean idempotencyKeyExists = transactionRepository.existsByIdempotencyKey(transactionUpdateRequest.getTransactionRequest().getIdempotencyKey());

        if (idempotencyKeyExists) {
            return ResponseEntity.ok().body("Transaction updated successfully");
        }

        SplitTransaction transaction = transactionRepository.findById(transactionUpdateRequest.getTransactionRequest().getTransactionId()).orElseThrow(() -> new TransactionNotFoundException(transactionUpdateRequest.getTransactionRequest().getTransactionId()));
        SplitTransaction updatedTransaction = SplitTransaction.builder().idempotencyKey(transaction.getIdempotencyKey()).transactionId(transaction.getTransactionId()).payer(transaction.getPayer()).amount(transaction.getAmount()).participantsAmounts(transaction.getParticipantsAmounts()).groupId(transaction.getGroupId()).description(transaction.getDescription()).category(transaction.getCategory()).build();


        transactionUpdateRequest.getUpdates().forEach(update -> {
            SplitTransactionRequest transactionRequest = transactionUpdateRequest.getTransactionRequest();
            if (Objects.equals(update, PAYER)) {
                SplitUser payer = userRepository.findById(transactionRequest.getPayerId()).orElseThrow(() -> new UserNotFoundException(transactionRequest.getPayerId()));
                updatedTransaction.setPayer(payer);
            } else if (Objects.equals(update, DESCRIPTION)) {
                updatedTransaction.setDescription(transactionRequest.getDescription());
            } else if (Objects.equals(update, CATEGORY)) {
                updatedTransaction.setCategory(transactionRequest.getCategory());
            }
            if (Objects.equals(update, AMOUNT) || Objects.equals(update, PAYER)) {
                List<SplitUser> newParticipants = userRepository.findAllById(transactionRequest.getParticipantsAmounts().keySet());
                SplitUser newPayer = userRepository.findById(transactionRequest.getPayerId()).orElseThrow(() -> new UserNotFoundException(transactionRequest.getPayerId()));

                if (newParticipants.size() != transactionRequest.getParticipantsAmounts().size())
                    throw new UserNotFoundException();

                List<SplitUser> oldParticipants = new ArrayList<SplitUser>(transaction.getParticipantsAmounts().keySet());


                Map<SplitUser, Double> updatedMap = transactionRequest.getParticipantsAmounts().entrySet().parallelStream().collect(HashMap::new, (map, entry) -> map.put(userRepository.findById(entry.getKey()).orElseThrow(() -> new UserNotFoundException(entry.getKey())), entry.getValue()), HashMap::putAll);

                oldParticipants.forEach((receiver) -> {

                    //find if the entry exists
                    SplitMoneyBalanceId direct = SplitMoneyBalanceId.builder().payerId(transaction.getPayer().getUserId()).receiverId(receiver.getUserId()).groupId(transaction.getGroupId()).build();
                    SplitMoneyBalanceId inverse = direct.getReverseMapping();

                    SplitMoneyBalance directBalance = moneyBalanceRepository.findById(direct).orElse(null);
                    SplitMoneyBalance reverseBalance = moneyBalanceRepository.findById(inverse).orElse(null);


                    double amountOwed = getParticipantOwedAmount(receiver, transaction.getParticipantsAmounts()).orElse(0.0);

                    if (reverseBalance != null) {
                        reverseBalance.setAmount(reverseBalance.getAmount() + amountOwed);
                        moneyBalanceRepository.save(reverseBalance);
                    } else if (directBalance != null) {
                        directBalance.setAmount(directBalance.getAmount() - amountOwed);
                        moneyBalanceRepository.save(directBalance);
                    }
                });


                newParticipants.forEach((receiver) -> {

                    //find if the entry exists
                    SplitMoneyBalanceId direct = SplitMoneyBalanceId.builder().payerId(transactionRequest.getPayerId()).receiverId(receiver.getUserId()).groupId(transactionRequest.getGroupId()).build();
                    SplitMoneyBalanceId inverse = direct.getReverseMapping();


                    SplitMoneyBalance directBalance = moneyBalanceRepository.findById(direct).orElse(null);
                    SplitMoneyBalance reverseBalance = moneyBalanceRepository.findById(inverse).orElse(null);


                    double amountOwed = transactionRequest.getParticipantsAmounts().get(receiver.getUserId());

                    if (directBalance == null && reverseBalance == null) {
                        SplitMoneyBalance newBalance = SplitMoneyBalance.builder().amount(amountOwed).payer(newPayer).moneyBalanceId(new SplitMoneyBalanceId(newPayer.getUserId(), receiver.getUserId(), transactionRequest.getGroupId())).receiver(receiver).build();
                        moneyBalanceRepository.save(newBalance);
                    } else if (reverseBalance != null) {
//                        double originallyOwed = transaction.getParticipantsAmounts().get(receiver);
                        reverseBalance.setAmount(reverseBalance.getAmount() - amountOwed);
                        moneyBalanceRepository.save(reverseBalance);
                    } else {
                        // double originallyOwed = getParticipantOwedAmount(receiver, transaction.getParticipantsAmounts()).orElse(0.0);
                        directBalance.setAmount(directBalance.getAmount() + amountOwed);
                        moneyBalanceRepository.save(directBalance);
                    }
                });

                updatedTransaction.setAmount(transactionRequest.getAmount());
                updatedTransaction.setParticipantsAmounts(updatedMap);
            }
        });


        transactionRepository.save(updatedTransaction);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
