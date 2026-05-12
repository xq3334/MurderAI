package com.codexlab.aimurder.web.controller;

import com.codexlab.aimurder.web.dto.EndingRevealResponse;
import com.codexlab.aimurder.web.dto.FinalAccusationRequest;
import com.codexlab.aimurder.web.dto.Result;
import com.codexlab.aimurder.web.service.FinalAccusationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 最终指认控制器。
 */
@RestController
@RequestMapping("/api/ending")
public class FinalAccusationController {

    private final FinalAccusationService finalAccusationService;

    public FinalAccusationController(FinalAccusationService finalAccusationService) {
        this.finalAccusationService = finalAccusationService;
    }

    /**
     * 提交最终指认并返回真相揭晓结果。
     *
     * @param request 最终指认请求
     * @return 结局揭晓结果
     */
    @PostMapping("/accuse")
    public Result<EndingRevealResponse> accuse(@Valid @RequestBody FinalAccusationRequest request) {
        return Result.success(finalAccusationService.submitAccusation(
                request.sessionId(),
                request.accusedCharacterId(),
                request.reasoning()
        ));
    }
}
