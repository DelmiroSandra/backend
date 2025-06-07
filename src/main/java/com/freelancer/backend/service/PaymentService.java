package com.freelancer.backend.service;

import com.freelancer.backend.model.Payment;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment createPayment(User payer, User receiver, Project project, Double amount);
    Optional<Payment> getPaymentById(Long id);
    List<Payment> getPaymentsByPayer(User payer);
    List<Payment> getPaymentsByReceiver(User receiver);
    Payment updatePaymentStatus(Long id, String newStatus);
}
