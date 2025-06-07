package com.freelancer.backend.service.impl;

import com.freelancer.backend.model.Message;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;
import com.freelancer.backend.repository.MessageRepository;
import com.freelancer.backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    @Transactional
    public Message sendMessage(User sender, User receiver, Project project, String content) {
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .project(project)
                .content(content)
                .build();
        return messageRepository.save(message);
    }

    @Override
    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> getMessagesBetweenUsers(User sender, User receiver) {
        return messageRepository.findBySenderAndReceiver(sender, receiver);
    }

    @Override
    public List<Message> getMessagesByProject(Project project) {
        return messageRepository.findByProject(project);
    }
}
