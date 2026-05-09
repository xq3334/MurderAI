package com.codexlab.aimurder.web.service;

import com.codexlab.aimurder.web.dto.ChatContextMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatContextService {

    private final Map<String, List<ChatContextMessage>> sessionContextStore = new ConcurrentHashMap<>();

    /**
     * 获取指定会话当前的上下文消息列表。
     * 返回副本而不是原始集合，避免外部直接修改内存中的上下文。
     *
     * @param sessionId 当前会话标识
     * @return 上下文消息列表副本
     */
    public List<ChatContextMessage> getContext(String sessionId) {
        return new ArrayList<>(sessionContextStore.getOrDefault(sessionId, List.of()));
    }

    /**
     * 向指定会话的内存上下文中追加一条消息。
     *
     * @param sessionId 当前会话标识
     * @param role      消息角色，例如 user 或 assistant
     * @param content   消息内容
     */
    public void appendMessage(String sessionId, String role, String content) {
        sessionContextStore.computeIfAbsent(sessionId, key -> new ArrayList<>())
                .add(new ChatContextMessage(role, content));
    }
}
