package com.freelancer.backend.service;

import com.freelancer.backend.model.Message;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    Message sendMessage(User sender, User receiver, Project project, String content);
    Optional<Message> getMessageById(Long id);
    List<Message> getMessagesBetweenUsers(User sender, User receiver);
    List<Message> getMessagesByProject(Project project);
}
