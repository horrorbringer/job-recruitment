package com.recruitment.service;

import com.recruitment.model.Message;
import com.recruitment.model.User;
import com.recruitment.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final NotificationService notificationService;

    @Transactional
    public Message sendMessage(User sender, User receiver, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        Message saved = messageRepository.save(message);

        // Notify the receiver
        notificationService.createNotification(
                receiver,
                "New Message from " + sender.getFullName(),
                content.length() > 50 ? content.substring(0, 47) + "..." : content,
                "message",
                "/messages/chat/" + sender.getId());

        return saved;
    }

    public List<Message> getConversation(User u1, User u2) {
        List<Message> messages = messageRepository.findConversation(u1, u2);

        // Mark as read when viewing conversation
        messages.stream()
                .filter(m -> m.getReceiver().getId().equals(u1.getId()) && !m.isRead())
                .forEach(m -> {
                    m.setRead(true);
                    m.setReadAt(LocalDateTime.now());
                    messageRepository.save(m);
                });

        return messages;
    }

    public List<Message> getMessageSummary(User user) {
        return messageRepository.findLatestMessagesPerConversation(user);
    }

    public long getUnreadCount(User user) {
        return messageRepository.countUnreadForUser(user);
    }

    @Transactional
    public void markAsRead(Long messageId) {
        messageRepository.findById(messageId).ifPresent(m -> {
            m.setRead(true);
            m.setReadAt(LocalDateTime.now());
            messageRepository.save(m);
        });
    }
}
