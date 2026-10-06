package com.skipps.gpu_farm_manager.model;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skipps.gpu_farm_manager.exception.ModelAlreadyExistsException;
import com.skipps.gpu_farm_manager.exception.ModelNotFoundException;
import com.skipps.gpu_farm_manager.model.dto.CreateModelRequest;
import com.skipps.gpu_farm_manager.model.dto.ModelResponse;
import com.skipps.gpu_farm_manager.model.dto.UpdateModelRequest;

@Service
@Transactional
public class ModelService {

    private final ModelRepository repository;

    public ModelService(ModelRepository repository) {
        this.repository = repository;
    }

    public ModelResponse createModel(CreateModelRequest request) {
        if (repository.existsByNameAndVersion(request.name(), request.version())) {
            throw new ModelAlreadyExistsException(
                String.format("Model '%s' with version '%s' already exists", request.name(), request.version())
            );
        }

        ModelModel model = new ModelModel();
        model.setName(request.name());
        model.setVersion(request.version());
        model.setParameterSize(request.parameterSize());
        model.setQuantization(request.quantization());
        model.setEstimatedVram(request.estimatedVram());

        model = repository.save(model);
        return mapToResponse(model);
    }

    @Transactional(readOnly = true)
    public ModelResponse getModel(Long id) {
        ModelModel model = repository.findById(id)
            .orElseThrow(() -> new ModelNotFoundException(id));
        return mapToResponse(model);
    }

    @Transactional(readOnly = true)
    public List<ModelResponse> getAllModels() {
        return repository.findAll().stream()
            .map(this::mapToResponse)
            .toList();
    }

    public ModelResponse updateModel(Long id, UpdateModelRequest request) {
        ModelModel model = repository.findById(id)
            .orElseThrow(() -> new ModelNotFoundException(id));

        String targetName = request.name() != null ? request.name() : model.getName();
        String targetVersion = request.version() != null ? request.version() : model.getVersion();

        if ((!targetName.equals(model.getName()) || !targetVersion.equals(model.getVersion()))
                && repository.existsByNameAndVersion(targetName, targetVersion)) {
            throw new ModelAlreadyExistsException(
                String.format("Model '%s' with version '%s' already exists", targetName, targetVersion)
            );
        }

        if (request.name() != null) {
            model.setName(request.name());
        }
        if (request.version() != null) {
            model.setVersion(request.version());
        }
        if (request.parameterSize() != null) {
            model.setParameterSize(request.parameterSize());
        }
        if (request.quantization() != null) {
            model.setQuantization(request.quantization());
        }
        if (request.estimatedVram() != null) {
            model.setEstimatedVram(request.estimatedVram());
        }

        model = repository.save(model);
        return mapToResponse(model);
    }

    public void deleteModel(Long id) {
        if (!repository.existsById(id)) {
            throw new ModelNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private ModelResponse mapToResponse(ModelModel model) {
        return new ModelResponse(
            model.getId(),
            model.getName(),
            model.getVersion(),
            model.getParameterSize(),
            model.getQuantization(),
            model.getEstimatedVram()
        );
    }
}
