package com.derster.cafesystem.dao;

import com.derster.cafesystem.pojo.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDao extends JpaRepository<Bill, Integer> {
    List<Bill> getAllBills();
    List<Bill> getBillByCreatedBy(@Param("username") String username);
}
