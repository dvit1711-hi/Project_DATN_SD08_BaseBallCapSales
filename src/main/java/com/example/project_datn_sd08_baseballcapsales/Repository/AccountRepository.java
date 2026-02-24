package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);
}

