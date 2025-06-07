package com.freelancer.backend.controller;

import com.freelancer.backend.dto.ApiResponse;
import com.freelancer.backend.dto.MessageDTO;
import com.freelancer.backend.model.Message;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;
import com.freelancer.backend.service.MessageService;
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
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired private MessageService messageService;
    @Autowired private UserService userService;
    @Autowired private ProjectService projectService;
    private final ModelMapper modelMapper = new ModelMapper();

    @PostMapping
    public ResponseEntity<?> send(Authentication auth,
                                  @Valid @RequestBody MessageDTO dto) {
        User sender = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        User receiver = userService.findById(dto.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Destinatário não encontrado"));
        Project project = null;
        if (dto.getProjectId() != null) {
            project = projectService.getProjectById(dto.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
        }
        Message m = messageService.sendMessage(sender, receiver, project, dto.getContent());
        MessageDTO out = modelMapper.map(m, MessageDTO.class);
        return ResponseEntity.ok(out);
    }

    @GetMapping("/conversation/{otherId}")
    public ResponseEntity<?> convo(Authentication auth,
                                   @PathVariable Long otherId) {
        User me = userService.findByUsername(auth.getName()).orElseThrow();
        User other = userService.findById(otherId).orElseThrow();
        List<MessageDTO> msgs = messageService
                .getMessagesBetweenUsers(me, other)
                .stream()
                .map(m -> modelMapper.map(m, MessageDTO.class))
                .collect(Collectors.toList());
        msgs.addAll(messageService
                .getMessagesBetweenUsers(other, me)
                .stream()
                .map(m -> modelMapper.map(m, MessageDTO.class))
                .collect(Collectors.toList()));
        return ResponseEntity.ok(msgs);
    }

    @GetMapping("/project/{pid}")
    public ResponseEntity<?> byProject(@PathVariable Long pid) {
        Project p = projectService.getProjectById(pid)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
        List<MessageDTO> msgs = messageService.getMessagesByProject(p)
                .stream()
                .map(m -> modelMapper.map(m, MessageDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(msgs);
    }
}
