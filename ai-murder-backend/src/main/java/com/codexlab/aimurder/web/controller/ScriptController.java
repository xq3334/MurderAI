package com.codexlab.aimurder.web.controller;

import com.codexlab.aimurder.web.dto.PlayerRoleCardResponse;
import com.codexlab.aimurder.web.dto.RandomScriptSetupRequest;
import com.codexlab.aimurder.web.dto.Result;
import com.codexlab.aimurder.web.dto.ScriptSetupRequest;
import com.codexlab.aimurder.web.dto.ScriptSummaryResponse;
import com.codexlab.aimurder.web.dto.SessionDetailResponse;
import com.codexlab.aimurder.web.dto.SessionBootstrapResponse;
import com.codexlab.aimurder.web.service.ScriptCatalogService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 副本目录控制器。
 */
@RestController
@RequestMapping("/api/scripts")
public class ScriptController {

    private final ScriptCatalogService scriptCatalogService;

    public ScriptController(ScriptCatalogService scriptCatalogService) {
        this.scriptCatalogService = scriptCatalogService;
    }

    /**
     * 查询全部副本摘要。
     *
     * @return 副本摘要列表
     */
    @GetMapping
    public Result<List<ScriptSummaryResponse>> listScripts() {
        return Result.success(scriptCatalogService.listScripts());
    }

    /**
     * 查询某个副本下可供玩家选择的角色卡。
     *
     * @param scriptId 副本标识
     * @return 角色卡列表
     */
    @GetMapping("/{scriptId}/roles")
    public Result<List<PlayerRoleCardResponse>> listRoles(@PathVariable String scriptId) {
        return Result.success(scriptCatalogService.listPlayerRoles(scriptId));
    }

    /**
     * 初始化一局副本会话，并绑定玩家角色。
     *
     * @param request 初始化请求
     * @return 初始化结果
     */
    @PostMapping("/select")
    public Result<SessionBootstrapResponse> selectScript(@Valid @RequestBody ScriptSetupRequest request) {
        return Result.success(scriptCatalogService.initializeSession(
                request.sessionId(),
                request.scriptId(),
                request.playerCharacterId()
        ));
    }

    /**
     * 随机抽取指定副本中的一个可扮演角色，并初始化会话。
     *
     * @param request 初始化请求
     * @return 初始化结果
     */
    @PostMapping("/random-select")
    public Result<SessionBootstrapResponse> randomSelect(@Valid @RequestBody RandomScriptSetupRequest request) {
        return Result.success(scriptCatalogService.initializeSessionWithRandomRole(
                request.sessionId(),
                request.scriptId()
        ));
    }

    /**
     * 根据会话标识查询当前局状态。
     *
     * @param sessionId 会话标识
     * @return 会话详情
     */
    @GetMapping("/sessions/{sessionId}")
    public Result<SessionDetailResponse> getSessionDetail(@PathVariable String sessionId) {
        return Result.success(scriptCatalogService.getSessionDetail(sessionId));
    }
}
