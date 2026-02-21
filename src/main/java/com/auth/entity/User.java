package com.auth.entity;

import com.auth.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String username;

    String password;

    @Enumerated(EnumType.STRING)
    Role role;

    boolean enabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<RefreshToken> refreshTokens;

}
