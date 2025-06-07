package com.freelancer.backend.repository;

import com.freelancer.backend.model.Message;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiver(User sender, User receiver);
    List<Message> findByProject(Project project);
    List<Message> findByReceiver(User receiver);
}
