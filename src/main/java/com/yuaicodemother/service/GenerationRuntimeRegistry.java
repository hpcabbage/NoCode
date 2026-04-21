package com.yuaicodemother.service;

import com.yuaicodemother.model.dto.app.GenerationRuntimeState;
import com.yuaicodemother.model.enums.GenerationPhaseEnum;
import com.yuaicodemother.model.enums.GenerationStatusEnum;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class GenerationRuntimeRegistry {

    private final ConcurrentHashMap<String, GenerationRuntimeState> stateMap = new ConcurrentHashMap<>();

    public void register(GenerationRuntimeState state) {
        stateMap.put(state.getGenerationId(), state);
    }

    public GenerationRuntimeState get(String generationId) {
        return stateMap.get(generationId);
    }

    public void updatePhase(String generationId, GenerationPhaseEnum phase) {
        GenerationRuntimeState state = stateMap.get(generationId);
        if (state != null) {
            state.setPhase(phase);
        }
    }

    public void updateStatus(String generationId, GenerationStatusEnum status) {
        GenerationRuntimeState state = stateMap.get(generationId);
        if (state != null) {
            state.setStatus(status);
        }
    }

    public boolean requestStop(String generationId) {
        GenerationRuntimeState state = stateMap.get(generationId);
        if (state == null) {
            return false;
        }
        state.setStopRequested(true);
        state.setStatus(GenerationStatusEnum.STOPPED);
        if (state.getCancelAction() != null) {
            state.getCancelAction().run();
        }
        return true;
    }

    public boolean isStopRequested(String generationId) {
        GenerationRuntimeState state = stateMap.get(generationId);
        return state != null && state.isStopRequested();
    }

    public void setCancelAction(String generationId, Runnable cancelAction) {
        GenerationRuntimeState state = stateMap.get(generationId);
        if (state != null) {
            state.setCancelAction(cancelAction);
        }
    }

    public void remove(String generationId) {
        stateMap.remove(generationId);
    }
}
