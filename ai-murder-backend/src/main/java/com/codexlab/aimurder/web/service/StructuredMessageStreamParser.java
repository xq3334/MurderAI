package com.codexlab.aimurder.web.service;

import com.codexlab.aimurder.web.dto.ChatStreamStructuredMessage;
import com.codexlab.aimurder.web.dto.StructuredMessageKind;
import com.codexlab.aimurder.web.dto.StructuredMessageRole;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 结构化流式消息解析器。
 * 用于将模型连续输出的多人对话文本，实时拆成带角色信息的结构化消息分片。
 */
@Service
public class StructuredMessageStreamParser {

    private static final Pattern LABEL_PATTERN = Pattern.compile("【([^】]+)】");

    /**
     * 创建新的解析会话。
     *
     * @return 解析状态
     */
    public ParserState newState(StructuredMessageKind kind) {
        ParserState state = new ParserState();
        state.currentKind = kind;
        return state;
    }

    /**
     * 处理一段流式文本增量。
     *
     * @param state 解析状态
     * @param chunk 本次增量文本
     * @return 结构化消息事件列表
     */
    public List<ChatStreamStructuredMessage> parse(ParserState state, String chunk) {
        state.buffer.append(chunk);
        List<ChatStreamStructuredMessage> events = new ArrayList<>();
        consume(state, events, false);
        return events;
    }

    /**
     * 在流结束时冲刷剩余内容。
     *
     * @param state 解析状态
     * @return 剩余结构化消息事件列表
     */
    public List<ChatStreamStructuredMessage> flush(ParserState state) {
        List<ChatStreamStructuredMessage> events = new ArrayList<>();
        consume(state, events, true);
        if (state.currentMessageId != null) {
            events.add(buildMessage(state, "", true));
            state.currentMessageId = null;
            state.currentSpeaker = null;
            state.currentSpeakerKey = null;
            state.currentRole = null;
            state.currentTone = null;
        } else if (state.buffer.length() > 0) {
            initializeSpeaker(state, "管家");
            String remaining = state.buffer.toString().trim();
            if (!remaining.isEmpty()) {
                events.add(buildMessage(state, remaining, true));
            }
            state.buffer.setLength(0);
            state.currentMessageId = null;
            state.currentSpeaker = null;
            state.currentSpeakerKey = null;
            state.currentRole = null;
            state.currentTone = null;
        }
        return events;
    }

    /**
     * 执行解析消费。
     *
     * @param state   解析状态
     * @param events  输出事件列表
     * @param flushAll 是否为最终冲刷
     */
    private void consume(ParserState state, List<ChatStreamStructuredMessage> events, boolean flushAll) {
        boolean progressed = true;
        while (progressed) {
            progressed = false;
            String current = state.buffer.toString();

            if (state.currentMessageId == null) {
                Matcher matcher = LABEL_PATTERN.matcher(current);
                if (!matcher.find()) {
                    return;
                }

                String label = matcher.group(1).trim();
                state.buffer.delete(0, matcher.end());
                initializeSpeaker(state, label);
                progressed = true;
                continue;
            }

            Matcher nextLabelMatcher = LABEL_PATTERN.matcher(current);
            if (nextLabelMatcher.find()) {
                int labelStart = nextLabelMatcher.start();
                if (labelStart > 0) {
                    String content = current.substring(0, labelStart);
                    if (!content.isBlank()) {
                        events.add(buildMessage(state, content, false));
                    }
                }
                String nextLabel = nextLabelMatcher.group(1).trim();
                state.buffer.delete(0, nextLabelMatcher.end());
                initializeSpeaker(state, nextLabel);
                progressed = true;
                continue;
            }

            int partialLabelIndex = current.lastIndexOf('【');
            if (!flushAll && partialLabelIndex >= 0) {
                String tail = current.substring(partialLabelIndex);
                if (!tail.contains("】")) {
                    String safeContent = current.substring(0, partialLabelIndex);
                    if (!safeContent.isBlank()) {
                        events.add(buildMessage(state, safeContent, false));
                    }
                    state.buffer.delete(0, partialLabelIndex);
                    return;
                }
            }

            if (flushAll) {
                if (!current.isBlank()) {
                    events.add(buildMessage(state, current, false));
                    state.buffer.setLength(0);
                }
                return;
            }

            if (!current.isBlank()) {
                events.add(buildMessage(state, current, false));
                state.buffer.setLength(0);
            }
            return;
        }
    }

    /**
     * 初始化当前发言者信息。
     *
     * @param state 解析状态
     * @param label 角色标签
     */
    private void initializeSpeaker(ParserState state, String label) {
        state.currentMessageId = "msg-" + UUID.randomUUID().toString().substring(0, 8);
        state.currentSpeaker = label;
        state.currentSpeakerKey = resolveSpeakerKey(label);
        state.currentRole = resolveRole(label);
        state.currentTone = resolveTone(label);
    }

    /**
     * 构建结构化消息对象。
     *
     * @param state     解析状态
     * @param delta     当前新增文本
     * @param completed 是否已完成
     * @return 结构化消息
     */
    private ChatStreamStructuredMessage buildMessage(ParserState state, String delta, boolean completed) {
        return new ChatStreamStructuredMessage(
                state.currentMessageId,
                state.currentSpeaker,
                state.currentSpeakerKey,
                state.currentRole,
                state.currentKind,
                state.currentTone,
                delta,
                completed
        );
    }

    /**
     * 将角色标签映射为稳定标识。
     *
     * @param label 角色标签
     * @return 稳定标识
     */
    private String resolveSpeakerKey(String label) {
        return switch (label) {
            case "管家", "庄园管家" -> "butler";
            case "旁白" -> "narrator";
            case "林乔" -> "lin-qiao";
            case "顾深" -> "gu-shen";
            case "周衍" -> "zhou-yan";
            case "陆沉" -> "lu-chen";
            default -> label.toLowerCase();
        };
    }

    /**
     * 将角色标签映射为消息角色类型。
     *
     * @param label 角色标签
     * @return 消息角色类型
     */
    private StructuredMessageRole resolveRole(String label) {
        return switch (label) {
            case "管家", "庄园管家" -> StructuredMessageRole.SYSTEM;
            case "旁白" -> StructuredMessageRole.NARRATOR;
            default -> StructuredMessageRole.CHARACTER;
        };
    }

    /**
     * 将角色标签映射为语气标签。
     *
     * @param label 角色标签
     * @return 语气标签
     */
    private String resolveTone(String label) {
        return switch (label) {
            case "管家", "庄园管家" -> "控场";
            case "旁白" -> "镜头";
            default -> "在场发言";
        };
    }

    /**
     * 解析状态对象。
     */
    public static class ParserState {

        private final StringBuilder buffer = new StringBuilder();
        private String currentMessageId;
        private String currentSpeaker;
        private String currentSpeakerKey;
        private StructuredMessageRole currentRole;
        private StructuredMessageKind currentKind;
        private String currentTone;
    }
}
