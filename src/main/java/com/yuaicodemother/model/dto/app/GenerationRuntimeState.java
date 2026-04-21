package com.yuaicodemother.model.dto.app;

import com.yuaicodemother.model.enums.GenerationPhaseEnum;
import com.yuaicodemother.model.enums.GenerationStatusEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerationRuntimeState {
    private String generationId;
    private Long appId;
    private Long userId;
    private GenerationStatusEnum status;
    private GenerationPhaseEnum phase;
    private volatile boolean stopRequested;
    private Runnable cancelAction;
}
