package com.example.splitwise.models.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/*@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SplitUserExpenses {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private SplitUser user;

}*/
