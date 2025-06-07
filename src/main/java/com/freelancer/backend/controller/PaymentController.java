package com.freelancer.backend.controller;

import com.freelancer.backend.dto.ApiResponse;
import com.freelancer.backend.dto.PaymentDTO;
import com.freelancer.backend.model.Payment;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;
import com.freelancer.backend.service.PaymentService;
import com.freelancer.backend.service.ProjectService;
import com.freelancer.backend.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    private final ModelMapper modelMapper = new ModelMapper();

    // POST /api/payments → criar pagamento (contratante paga freelancer)
    @PostMapping
    public ResponseEntity<?> createPayment(Authentication authentication,
                                           @Valid @RequestBody PaymentDTO paymentDTO) {
        String username = authentication.getName();
        User payer = userService.findByUsername(username).orElseThrow();
        Project project = projectService.getProjectById(paymentDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
        User receiver = userService.findById(paymentDTO.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Freelancer não encontrado"));
        if (!project.getContractor().getUsername().equals(username)) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "Apenas o contratante pode efetuar pagamento."));
        }
        Payment payment = paymentService.createPayment(payer, receiver, project, paymentDTO.getAmount());
        return ResponseEntity.ok(modelMapper.map(payment, PaymentDTO.class));
    }

    // PUT /api/payments/{id}?status=COMPLETED ou REFUNDED → admin ou contratante pode alterar
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePaymentStatus(Authentication authentication,
                                                 @PathVariable Long id,
                                                 @RequestParam("status") String status) {
        // Poderíamos validar: apenas admin pode reembolsar, contratante pode marcar como completed etc.
        Payment updated = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(modelMapper.map(updated, PaymentDTO.class));
    }

    // GET /api/payments/payer → lista pagamentos feitos pelo contratante autenticado
    @GetMapping("/payer")
    public ResponseEntity<?> getMyPayments(Authentication authentication) {
        String username = authentication.getName();
        User payer = userService.findByUsername(username).orElseThrow();
        List<Payment> payments = paymentService.getPaymentsByPayer(payer);
        List<PaymentDTO> result = payments.stream()
                .map(p -> modelMapper.map(p, PaymentDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // GET /api/payments/receiver → lista pagamentos recebidos pelo freelancer autenticado
    @GetMapping("/receiver")
    public ResponseEntity<?> getMyReceivedPayments(Authentication authentication) {
        String username = authentication.getName();
        User receiver = userService.findByUsername(username).orElseThrow();
        List<Payment> payments = paymentService.getPaymentsByReceiver(receiver);
        List<PaymentDTO> result = payments.stream()
                .map(p -> modelMapper.map(p, PaymentDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
