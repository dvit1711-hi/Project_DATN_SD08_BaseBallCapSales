package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    @Query("""
    select a
    from Account a
    where lower(a.username) like lower(concat('%', :keyword, '%'))
       or lower(a.email) like lower(concat('%', :keyword, '%'))
       or lower(a.phoneNumber) like lower(concat('%', :keyword, '%'))
    order by a.id desc
""")
    List<Account> searchCustomers(@Param("keyword") String keyword);
}

