package com.freelancer.backend.repository;

import com.freelancer.backend.model.Payment;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPayer(User payer);
    List<Payment> findByReceiver(User receiver);
    List<Payment> findByProject(Project project);
}
