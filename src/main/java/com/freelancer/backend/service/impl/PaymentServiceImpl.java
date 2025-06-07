package com.freelancer.backend.service.impl;

import com.freelancer.backend.model.Payment;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;
import com.freelancer.backend.repository.PaymentRepository;
import com.freelancer.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Payment createPayment(User payer, User receiver, Project project, Double amount) {
        Payment payment = Payment.builder()
                .payer(payer)
                .receiver(receiver)
                .project(project)
                .amount(amount)
                .status("PENDING")
                .build();
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public List<Payment> getPaymentsByPayer(User payer) {
        return paymentRepository.findByPayer(payer);
    }

    @Override
    public List<Payment> getPaymentsByReceiver(User receiver) {
        return paymentRepository.findByReceiver(receiver);
    }

    @Override
    @Transactional
    public Payment updatePaymentStatus(Long id, String newStatus) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
        if (!List.of("PENDING", "COMPLETED", "REFUNDED").contains(newStatus)) {
            throw new RuntimeException("Status de pagamento inválido.");
        }
        payment.setStatus(newStatus);
        return paymentRepository.save(payment);
    }
}
