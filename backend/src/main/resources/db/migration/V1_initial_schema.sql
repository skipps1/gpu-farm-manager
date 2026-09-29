-- Database Schema for GPU Farm Management and LLM Inference PlatformCREATE TABLE users
CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE gpu_nodes
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    hostname VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    agent_version VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE gpus
(
    id BIGSERIAL PRIMARY KEY,
    node_id BIGINT NOT NULL REFERENCES gpu_nodes(id) ON DELETE CASCADE,
    vendor VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    architecture VARCHAR(100),
    vram_capacity BIGINT NOT NULL,
    compute_capability VARCHAR(50),
    status VARCHAR(50) NOT NULL
);

CREATE TABLE models
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    version VARCHAR(100) NOT NULL,
    parameter_size VARCHAR(50),
    quantization VARCHAR(50),
    estimated_vram BIGINT NOT NULL
);

CREATE TABLE inference_services
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    backend VARCHAR(100) NOT NULL,
    model_id BIGINT NOT NULL REFERENCES models(id),
    gpu_id BIGINT NOT NULL REFERENCES gpus(id),
    status VARCHAR(50) NOT NULL,
    allocated_vram BIGINT NOT NULL,
    endpoint VARCHAR(255)
);

CREATE TABLE workloads
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    model_id BIGINT NOT NULL REFERENCES models(id),
    service_id BIGINT REFERENCES inference_services(id) ON DELETE SET NULL,
    gpu_id BIGINT REFERENCES gpus(id) ON DELETE SET NULL,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    requested_vram BIGINT NOT NULL,
    started_at TIMESTAMP,
    finished_at TIMESTAMP
);

CREATE TABLE token_usages
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    model_id BIGINT NOT NULL REFERENCES models(id),
    workload_id BIGINT REFERENCES workloads(id) ON DELETE CASCADE,
    input_tokens BIGINT NOT NULL DEFAULT 0,
    output_tokens BIGINT NOT NULL DEFAULT 0,
    total_tokens BIGINT NOT NULL DEFAULT 0,
    recorded_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE quotas
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    max_vram BIGINT NOT NULL,
    max_gpu_count INT NOT NULL,
    max_concurrent_workloads INT NOT NULL,
    max_tokens BIGINT NOT NULL,
    period VARCHAR(50) NOT NULL
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_gpu_nodes_status ON gpu_nodes(status);
CREATE INDEX idx_gpus_node_id ON gpus(node_id);
CREATE INDEX idx_gpus_status ON gpus(status);
CREATE INDEX idx_inference_services_model_id ON inference_services(model_id);
CREATE INDEX idx_inference_services_gpu_id ON inference_services(gpu_id);
CREATE INDEX idx_inference_services_status ON inference_services(status);
CREATE INDEX idx_workloads_user_id ON workloads(user_id);
CREATE INDEX idx_workloads_status ON workloads(status);
CREATE INDEX idx_token_usages_user_id ON token_usages(user_id);
CREATE INDEX idx_token_usages_recorded_at ON token_usages(recorded_at);
