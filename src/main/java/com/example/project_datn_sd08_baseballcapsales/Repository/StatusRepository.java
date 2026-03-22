package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {

}
