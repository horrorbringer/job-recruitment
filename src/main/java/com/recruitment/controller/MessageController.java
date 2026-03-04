package com.recruitment.controller;

import com.recruitment.model.Message;
import com.recruitment.model.User;
import com.recruitment.service.MessageService;
import com.recruitment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    @GetMapping
    public String inbox(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        List<Message> latestMessages = messageService.getMessageSummary(user);

        model.addAttribute("messages", latestMessages);
        model.addAttribute("currentUser", user);

        return "messages/inbox";
    }

    @GetMapping("/chat/{otherUserId}")
    public String chat(@PathVariable Long otherUserId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        User otherUser = userService.findById(otherUserId);

        if (otherUser == null) {
            return "redirect:/messages";
        }

        List<Message> conversation = messageService.getConversation(user, otherUser);

        model.addAttribute("conversation", conversation);
        model.addAttribute("otherUser", otherUser);
        model.addAttribute("currentUser", user);

        return "messages/chat";
    }

    @PostMapping("/send")
    public String sendMessage(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long receiverId,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {
        User sender = userService.findByEmail(userDetails.getUsername());
        User receiver = userService.findById(receiverId);

        if (receiver != null && !content.trim().isEmpty()) {
            messageService.sendMessage(sender, receiver, content);
        } else {
            redirectAttributes.addFlashAttribute("error", "Message could not be sent.");
        }

        return "redirect:/messages/chat/" + receiverId;
    }
}
