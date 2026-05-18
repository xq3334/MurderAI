SET NAMES utf8mb4;
USE ai_murder;

CREATE TABLE IF NOT EXISTS am_user_account (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID，预留多租户',
    user_id VARCHAR(64) NOT NULL COMMENT '业务用户ID',
    username VARCHAR(64) NOT NULL COMMENT '登录名/唯一用户名',
    nickname VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    avatar_url VARCHAR(512) DEFAULT NULL COMMENT '头像地址',
    mobile VARCHAR(32) DEFAULT NULL COMMENT '手机号，预留',
    email VARCHAR(128) DEFAULT NULL COMMENT '邮箱，预留',
    role_code VARCHAR(32) NOT NULL DEFAULT 'PLAYER' COMMENT '角色类型：PLAYER/ADMIN/OPERATOR/CREATOR',
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '账户状态',
    register_source VARCHAR(32) DEFAULT 'SYSTEM' COMMENT '注册来源',
    last_login_at DATETIME DEFAULT NULL COMMENT '最后登录时间',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_user_account_user_id (user_id),
    UNIQUE KEY uk_am_user_account_username (username),
    KEY idx_am_user_account_role_status (role_code, status, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户账户表';

CREATE TABLE IF NOT EXISTS am_script (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID，预留多租户',
    script_id VARCHAR(64) NOT NULL COMMENT '剧本业务ID，对应代码中的 scriptId',
    script_code VARCHAR(64) DEFAULT NULL COMMENT '剧本编码，预留运营体系',
    script_name VARCHAR(128) NOT NULL COMMENT '剧本名称',
    genre_code VARCHAR(32) NOT NULL DEFAULT 'MURDER_MYSTERY' COMMENT '题材类型',
    theme_code VARCHAR(32) DEFAULT NULL COMMENT '主题类型',
    player_mode_code VARCHAR(32) NOT NULL DEFAULT 'SINGLE_PLAYER_AI' COMMENT '玩法模式编码',
    player_mode_name VARCHAR(128) DEFAULT NULL COMMENT '玩法模式名称',
    difficulty_level TINYINT NOT NULL DEFAULT 2 COMMENT '难度等级 1-5',
    total_character_count INT NOT NULL DEFAULT 0 COMMENT '总角色数',
    selectable_character_count INT NOT NULL DEFAULT 0 COMMENT '可选玩家角色数',
    recommended_player_count_min TINYINT NOT NULL DEFAULT 1 COMMENT '建议最少真人玩家数',
    recommended_player_count_max TINYINT NOT NULL DEFAULT 1 COMMENT '建议最多真人玩家数',
    estimated_duration_minutes INT DEFAULT NULL COMMENT '预计时长',
    unlock_order INT NOT NULL DEFAULT 1 COMMENT '解锁顺序',
    is_random_role_on_start TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否开局随机角色',
    default_locale VARCHAR(16) NOT NULL DEFAULT 'zh-CN' COMMENT '默认语言',
    current_version_no INT NOT NULL DEFAULT 1 COMMENT '当前发布版本号',
    publication_status VARCHAR(32) NOT NULL DEFAULT 'PUBLISHED' COMMENT '发布状态',
    content_source_type VARCHAR(32) NOT NULL DEFAULT 'MANUAL' COMMENT '内容来源',
    cover_image_url VARCHAR(512) DEFAULT NULL COMMENT '封面图',
    trailer_audio_url VARCHAR(512) DEFAULT NULL COMMENT '预告音频',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_script_script_id (script_id),
    KEY idx_am_script_status (publication_status, unlock_order, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='剧本主表';

CREATE TABLE IF NOT EXISTS am_script_version (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    script_id BIGINT NOT NULL COMMENT '剧本主表ID',
    version_no INT NOT NULL COMMENT '版本号',
    version_label VARCHAR(64) DEFAULT NULL COMMENT '版本标签，如 v1.0',
    content_status VARCHAR(32) NOT NULL DEFAULT 'PUBLISHED' COMMENT '内容状态',
    script_name VARCHAR(128) NOT NULL COMMENT '该版本下剧本名',
    summary_text TEXT DEFAULT NULL COMMENT '剧本简介',
    opening_narration LONGTEXT DEFAULT NULL COMMENT '开场旁白',
    opening_instruction LONGTEXT DEFAULT NULL COMMENT '开场控制指令',
    narration_instruction LONGTEXT DEFAULT NULL COMMENT '旁白使用规则',
    truth_summary LONGTEXT DEFAULT NULL COMMENT '真相摘要',
    ending_title VARCHAR(128) DEFAULT NULL COMMENT '结局标题',
    ending_story LONGTEXT DEFAULT NULL COMMENT '结局正文',
    minimum_key_clues_for_accusation INT NOT NULL DEFAULT 2 COMMENT '最终指认所需关键线索数',
    host_character_code VARCHAR(64) DEFAULT NULL COMMENT '主持角色编码',
    content_checksum VARCHAR(128) DEFAULT NULL COMMENT '内容摘要，用于校验',
    ai_system_prompt_template LONGTEXT DEFAULT NULL COMMENT '预留系统提示模板',
    guardrail_policy_json JSON DEFAULT NULL COMMENT '预留守卫策略',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_script_version_script_version (script_id, version_no),
    CONSTRAINT fk_am_script_version_script
        FOREIGN KEY (script_id) REFERENCES am_script (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='剧本版本表';

CREATE TABLE IF NOT EXISTS am_script_character (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    script_version_id BIGINT NOT NULL COMMENT '剧本版本ID',
    character_code VARCHAR(64) NOT NULL COMMENT '角色业务编码',
    character_name VARCHAR(64) NOT NULL COMMENT '角色姓名',
    identity_text VARCHAR(128) DEFAULT NULL COMMENT '角色身份',
    relationship_text VARCHAR(255) DEFAULT NULL COMMENT '与案件/死者/核心人物关系',
    personality_tags_json JSON DEFAULT NULL COMMENT '人格标签',
    public_persona TEXT DEFAULT NULL COMMENT '公开人设',
    public_backstory LONGTEXT DEFAULT NULL COMMENT '公开背景',
    private_backstory LONGTEXT DEFAULT NULL COMMENT '私密背景',
    public_objective TEXT DEFAULT NULL COMMENT '公开目标',
    private_objective TEXT DEFAULT NULL COMMENT '私密目标',
    opening_tip TEXT DEFAULT NULL COMMENT '开局提示',
    response_strategy LONGTEXT DEFAULT NULL COMMENT '回应策略',
    seat_order INT NOT NULL DEFAULT 0 COMMENT '座位顺序/展示顺序',
    portrait_url VARCHAR(512) DEFAULT NULL COMMENT '角色立绘',
    voice_profile_code VARCHAR(64) DEFAULT NULL COMMENT '语音人设编码',
    is_selectable_by_player TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否可由玩家选择',
    is_host TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否主持角色',
    is_killer TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否真凶',
    is_accomplice TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否帮凶',
    availability_status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '角色状态',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_script_character_version_code (script_version_id, character_code),
    KEY idx_am_script_character_selectable (script_version_id, is_selectable_by_player),
    CONSTRAINT fk_am_script_character_version
        FOREIGN KEY (script_version_id) REFERENCES am_script_version (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='剧本角色表';

CREATE TABLE IF NOT EXISTS am_script_character_secret (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    script_character_id BIGINT NOT NULL COMMENT '角色ID',
    secret_type VARCHAR(32) NOT NULL COMMENT '秘密类型：KNOWN_FACT/HIDDEN_SECRET/FORBIDDEN_DISCLOSURE',
    secret_text LONGTEXT NOT NULL COMMENT '秘密内容',
    visible_from_stage_order INT NOT NULL DEFAULT 1 COMMENT '最早可见阶段序号',
    visible_to_stage_order INT DEFAULT NULL COMMENT '最晚可见阶段序号',
    importance_level TINYINT NOT NULL DEFAULT 1 COMMENT '重要性 1-5',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_am_script_character_secret_type (script_character_id, secret_type, sort_no),
    CONSTRAINT fk_am_script_character_secret_character
        FOREIGN KEY (script_character_id) REFERENCES am_script_character (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色秘密信息表';

CREATE TABLE IF NOT EXISTS am_script_stage (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    script_version_id BIGINT NOT NULL COMMENT '剧本版本ID',
    stage_code VARCHAR(64) NOT NULL COMMENT '阶段业务编码',
    stage_name VARCHAR(128) NOT NULL COMMENT '阶段名称',
    stage_order INT NOT NULL COMMENT '阶段顺序',
    objective_text LONGTEXT DEFAULT NULL COMMENT '阶段目标',
    opening_narration LONGTEXT DEFAULT NULL COMMENT '阶段开场旁白',
    advance_condition_text LONGTEXT DEFAULT NULL COMMENT '推进条件说明',
    minimum_turns_before_advance INT NOT NULL DEFAULT 1 COMMENT '最少推进回合数',
    stage_theme_code VARCHAR(32) DEFAULT NULL COMMENT '阶段主题',
    atmosphere_tag VARCHAR(64) DEFAULT NULL COMMENT '氛围标签',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_script_stage_version_code (script_version_id, stage_code),
    UNIQUE KEY uk_am_script_stage_version_order (script_version_id, stage_order),
    CONSTRAINT fk_am_script_stage_version
        FOREIGN KEY (script_version_id) REFERENCES am_script_version (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='剧本阶段表';

CREATE TABLE IF NOT EXISTS am_script_stage_focus_character (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    script_stage_id BIGINT NOT NULL COMMENT '阶段ID',
    script_character_id BIGINT NOT NULL COMMENT '角色ID',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_script_stage_focus (script_stage_id, script_character_id),
    CONSTRAINT fk_am_script_stage_focus_stage
        FOREIGN KEY (script_stage_id) REFERENCES am_script_stage (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_am_script_stage_focus_character
        FOREIGN KEY (script_character_id) REFERENCES am_script_character (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='阶段聚焦角色表';

CREATE TABLE IF NOT EXISTS am_script_stage_keyword (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    script_stage_id BIGINT NOT NULL COMMENT '阶段ID',
    keyword_text VARCHAR(128) NOT NULL COMMENT '推进关键词',
    keyword_type VARCHAR(32) NOT NULL DEFAULT 'ADVANCE' COMMENT '关键词类型',
    trigger_weight INT NOT NULL DEFAULT 1 COMMENT '触发权重',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_am_script_stage_keyword_stage (script_stage_id, keyword_type, sort_no),
    CONSTRAINT fk_am_script_stage_keyword_stage
        FOREIGN KEY (script_stage_id) REFERENCES am_script_stage (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='阶段关键词表';

CREATE TABLE IF NOT EXISTS am_script_clue (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    script_version_id BIGINT NOT NULL COMMENT '剧本版本ID',
    clue_code VARCHAR(64) NOT NULL COMMENT '线索业务编码',
    clue_name VARCHAR(128) NOT NULL COMMENT '线索名称',
    clue_type VARCHAR(32) NOT NULL COMMENT '线索类型',
    content_text LONGTEXT NOT NULL COMMENT '线索内容',
    effect_text LONGTEXT DEFAULT NULL COMMENT '线索作用描述',
    first_unlock_stage_code VARCHAR(64) DEFAULT NULL COMMENT '首次解锁阶段编码',
    reveal_mode_code VARCHAR(32) NOT NULL DEFAULT 'AUTO' COMMENT '揭示方式',
    evidence_chain_code VARCHAR(64) DEFAULT NULL COMMENT '证据链编码',
    is_key_clue TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否关键线索',
    is_reusable TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否允许重复展示',
    asset_url VARCHAR(512) DEFAULT NULL COMMENT '附件地址',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_script_clue_version_code (script_version_id, clue_code),
    KEY idx_am_script_clue_key (script_version_id, is_key_clue),
    CONSTRAINT fk_am_script_clue_version
        FOREIGN KEY (script_version_id) REFERENCES am_script_version (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='剧本线索表';

CREATE TABLE IF NOT EXISTS am_script_stage_clue_rel (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    script_stage_id BIGINT NOT NULL COMMENT '阶段ID',
    script_clue_id BIGINT NOT NULL COMMENT '线索ID',
    reveal_order INT NOT NULL DEFAULT 0 COMMENT '揭示顺序',
    reveal_mode_code VARCHAR(32) NOT NULL DEFAULT 'AUTO' COMMENT '该阶段的揭示方式',
    is_default_available TINYINT(1) NOT NULL DEFAULT 1 COMMENT '进入该阶段是否默认可见',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_script_stage_clue_rel (script_stage_id, script_clue_id),
    CONSTRAINT fk_am_script_stage_clue_rel_stage
        FOREIGN KEY (script_stage_id) REFERENCES am_script_stage (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_am_script_stage_clue_rel_clue
        FOREIGN KEY (script_clue_id) REFERENCES am_script_clue (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='阶段可用线索关系表';

CREATE TABLE IF NOT EXISTS am_script_clue_character_rel (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    script_clue_id BIGINT NOT NULL COMMENT '线索ID',
    script_character_id BIGINT NOT NULL COMMENT '角色ID',
    relation_type VARCHAR(32) NOT NULL DEFAULT 'RELATED' COMMENT '关联类型',
    weight_score INT NOT NULL DEFAULT 1 COMMENT '关联权重',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_script_clue_character_rel (script_clue_id, script_character_id, relation_type),
    CONSTRAINT fk_am_script_clue_character_rel_clue
        FOREIGN KEY (script_clue_id) REFERENCES am_script_clue (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_am_script_clue_character_rel_character
        FOREIGN KEY (script_character_id) REFERENCES am_script_character (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='线索角色关联表';

CREATE TABLE IF NOT EXISTS am_script_asset (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    script_version_id BIGINT NOT NULL COMMENT '剧本版本ID',
    asset_code VARCHAR(64) NOT NULL COMMENT '资源编码',
    asset_type VARCHAR(32) NOT NULL COMMENT '资源类型：IMAGE/AUDIO/VIDEO/DOCUMENT',
    asset_title VARCHAR(128) DEFAULT NULL COMMENT '资源标题',
    asset_url VARCHAR(512) NOT NULL COMMENT '资源地址',
    storage_provider VARCHAR(32) DEFAULT 'LOCAL' COMMENT '存储类型',
    mime_type VARCHAR(128) DEFAULT NULL COMMENT 'MIME 类型',
    related_character_code VARCHAR(64) DEFAULT NULL COMMENT '关联角色编码',
    related_clue_code VARCHAR(64) DEFAULT NULL COMMENT '关联线索编码',
    is_public TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否直接对玩家公开',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_script_asset_version_code (script_version_id, asset_code),
    CONSTRAINT fk_am_script_asset_version
        FOREIGN KEY (script_version_id) REFERENCES am_script_version (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='剧本资源表';

CREATE TABLE IF NOT EXISTS am_game_session (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    session_id VARCHAR(64) NOT NULL COMMENT '业务会话ID，对应当前后端 sessionId',
    room_code VARCHAR(64) DEFAULT NULL COMMENT '房间码，预留多人模式',
    session_name VARCHAR(128) DEFAULT NULL COMMENT '会话名称',
    script_id BIGINT NOT NULL COMMENT '剧本ID',
    script_version_id BIGINT NOT NULL COMMENT '剧本版本ID',
    player_user_id BIGINT DEFAULT NULL COMMENT '玩家用户ID，单人模式下可空',
    player_character_code VARCHAR(64) DEFAULT NULL COMMENT '玩家角色编码',
    player_character_name VARCHAR(64) DEFAULT NULL COMMENT '玩家角色名称',
    player_identity_text VARCHAR(128) DEFAULT NULL COMMENT '玩家角色身份',
    player_role_description LONGTEXT DEFAULT NULL COMMENT '玩家角色卡摘要',
    player_objective_text LONGTEXT DEFAULT NULL COMMENT '玩家私密目标',
    host_character_code VARCHAR(64) DEFAULT NULL COMMENT '主持角色编码',
    current_stage_code VARCHAR(64) DEFAULT NULL COMMENT '当前阶段编码',
    current_stage_name VARCHAR(128) DEFAULT NULL COMMENT '当前阶段名称',
    current_stage_order INT NOT NULL DEFAULT 0 COMMENT '当前阶段顺序',
    opening_delivered TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已完成正式开场',
    player_turn_count INT NOT NULL DEFAULT 0 COMMENT '玩家总回合数',
    stage_turn_count INT NOT NULL DEFAULT 0 COMMENT '当前阶段回合数',
    stage_just_changed TINYINT(1) NOT NULL DEFAULT 0 COMMENT '本轮是否刚切阶段',
    current_environment_summary LONGTEXT DEFAULT NULL COMMENT '当前环境摘要',
    current_story_beat LONGTEXT DEFAULT NULL COMMENT '当前剧情拍点',
    player_conclusion LONGTEXT DEFAULT NULL COMMENT '玩家结论/指认摘要',
    session_status VARCHAR(32) NOT NULL DEFAULT 'IN_PROGRESS' COMMENT '会话状态',
    ai_provider_code VARCHAR(32) DEFAULT NULL COMMENT '模型提供方',
    ai_model_name VARCHAR(64) DEFAULT NULL COMMENT '模型名',
    ai_temperature DECIMAL(5,2) DEFAULT NULL COMMENT '温度参数',
    started_at DATETIME DEFAULT NULL COMMENT '开始时间',
    ended_at DATETIME DEFAULT NULL COMMENT '结束时间',
    last_active_at DATETIME DEFAULT NULL COMMENT '最后活跃时间',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_game_session_session_id (session_id),
    KEY idx_am_game_session_status (session_status, last_active_at),
    KEY idx_am_game_session_script (script_id, script_version_id),
    CONSTRAINT fk_am_game_session_script
        FOREIGN KEY (script_id) REFERENCES am_script (id),
    CONSTRAINT fk_am_game_session_script_version
        FOREIGN KEY (script_version_id) REFERENCES am_script_version (id),
    CONSTRAINT fk_am_game_session_player_user
        FOREIGN KEY (player_user_id) REFERENCES am_user_account (id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游戏会话表';

CREATE TABLE IF NOT EXISTS am_game_session_member (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    game_session_id BIGINT NOT NULL COMMENT '会话ID',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    seat_no INT NOT NULL DEFAULT 0 COMMENT '座位号',
    character_code VARCHAR(64) DEFAULT NULL COMMENT '角色编码',
    character_name VARCHAR(64) DEFAULT NULL COMMENT '角色名',
    identity_text VARCHAR(128) DEFAULT NULL COMMENT '身份',
    control_type VARCHAR(32) NOT NULL DEFAULT 'AI' COMMENT '控制类型：PLAYER/AI/SYSTEM',
    join_status VARCHAR(32) NOT NULL DEFAULT 'JOINED' COMMENT '加入状态',
    is_host TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否主持位',
    join_at DATETIME DEFAULT NULL COMMENT '加入时间',
    leave_at DATETIME DEFAULT NULL COMMENT '离开时间',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_game_session_member_unique (game_session_id, seat_no),
    KEY idx_am_game_session_member_user (user_id),
    CONSTRAINT fk_am_game_session_member_session
        FOREIGN KEY (game_session_id) REFERENCES am_game_session (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_am_game_session_member_user
        FOREIGN KEY (user_id) REFERENCES am_user_account (id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游戏会话成员表';

CREATE TABLE IF NOT EXISTS am_game_stage_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    game_session_id BIGINT NOT NULL COMMENT '会话ID',
    from_stage_code VARCHAR(64) DEFAULT NULL COMMENT '原阶段编码',
    to_stage_code VARCHAR(64) DEFAULT NULL COMMENT '目标阶段编码',
    to_stage_name VARCHAR(128) DEFAULT NULL COMMENT '目标阶段名',
    to_stage_order INT DEFAULT NULL COMMENT '目标阶段顺序',
    trigger_type VARCHAR(32) NOT NULL DEFAULT 'RULE' COMMENT '触发类型',
    trigger_message_text LONGTEXT DEFAULT NULL COMMENT '触发消息文本',
    trigger_summary TEXT DEFAULT NULL COMMENT '触发摘要',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_am_game_stage_log_session (game_session_id, created_at),
    CONSTRAINT fk_am_game_stage_log_session
        FOREIGN KEY (game_session_id) REFERENCES am_game_session (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会话阶段推进日志表';

CREATE TABLE IF NOT EXISTS am_game_character_state (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    game_session_id BIGINT NOT NULL COMMENT '会话ID',
    character_code VARCHAR(64) NOT NULL COMMENT '角色编码',
    pressure_level INT NOT NULL DEFAULT 0 COMMENT '压力值',
    suspected_flag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否被重点怀疑',
    loosened_flag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否松口',
    suspicion_reason TEXT DEFAULT NULL COMMENT '怀疑原因摘要',
    last_interacted_at DATETIME DEFAULT NULL COMMENT '最近被追问时间',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_game_character_state_unique (game_session_id, character_code),
    CONSTRAINT fk_am_game_character_state_session
        FOREIGN KEY (game_session_id) REFERENCES am_game_session (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会话角色运行状态表';

CREATE TABLE IF NOT EXISTS am_game_message (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    game_session_id BIGINT NOT NULL COMMENT '会话ID',
    turn_no INT NOT NULL DEFAULT 0 COMMENT '玩家总回合序号',
    stage_round_no INT NOT NULL DEFAULT 0 COMMENT '阶段内回合序号',
    message_batch_id VARCHAR(64) DEFAULT NULL COMMENT '流式消息批次ID',
    message_kind VARCHAR(32) NOT NULL DEFAULT 'DIALOGUE' COMMENT '消息类型',
    speaker_role_code VARCHAR(32) NOT NULL COMMENT '发言角色类型',
    speaker_code VARCHAR(64) DEFAULT NULL COMMENT '发言人编码',
    speaker_name VARCHAR(64) DEFAULT NULL COMMENT '发言人名称',
    tone_code VARCHAR(64) DEFAULT NULL COMMENT '语气标签',
    raw_text LONGTEXT DEFAULT NULL COMMENT '原始文本',
    display_text LONGTEXT DEFAULT NULL COMMENT '前端展示文本',
    structured_payload_json JSON DEFAULT NULL COMMENT '结构化负载',
    stream_completed TINYINT(1) NOT NULL DEFAULT 1 COMMENT '流式片段是否完整',
    model_trace_id VARCHAR(64) DEFAULT NULL COMMENT '模型调用追踪ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_am_game_message_session_turn (game_session_id, turn_no, created_at),
    KEY idx_am_game_message_batch (message_batch_id),
    CONSTRAINT fk_am_game_message_session
        FOREIGN KEY (game_session_id) REFERENCES am_game_session (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游戏消息流水表';

CREATE TABLE IF NOT EXISTS am_game_scene_cue (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    game_session_id BIGINT NOT NULL COMMENT '会话ID',
    stage_code VARCHAR(64) DEFAULT NULL COMMENT '阶段编码',
    cue_type VARCHAR(32) NOT NULL COMMENT '场景提示类型',
    cue_title VARCHAR(128) DEFAULT NULL COMMENT '标题',
    cue_content LONGTEXT NOT NULL COMMENT '内容',
    quick_actions_json JSON DEFAULT NULL COMMENT '快捷追问建议',
    consumed_flag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已消费',
    consumed_at DATETIME DEFAULT NULL COMMENT '消费时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_am_game_scene_cue_session (game_session_id, consumed_flag, created_at),
    CONSTRAINT fk_am_game_scene_cue_session
        FOREIGN KEY (game_session_id) REFERENCES am_game_session (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='场景提示队列表';

CREATE TABLE IF NOT EXISTS am_game_clue_reveal (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    game_session_id BIGINT NOT NULL COMMENT '会话ID',
    clue_code VARCHAR(64) NOT NULL COMMENT '线索编码',
    clue_name VARCHAR(128) DEFAULT NULL COMMENT '线索名称',
    clue_type VARCHAR(32) DEFAULT NULL COMMENT '线索类型',
    revealed_stage_code VARCHAR(64) DEFAULT NULL COMMENT '揭示阶段编码',
    reveal_mode_code VARCHAR(32) NOT NULL DEFAULT 'AUTO' COMMENT '揭示方式',
    reveal_source VARCHAR(32) DEFAULT NULL COMMENT '揭示来源：AUTO/KEYWORD/SCRIPTED',
    is_key_clue TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否关键线索',
    revealed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '揭示时间',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    PRIMARY KEY (id),
    UNIQUE KEY uk_am_game_clue_reveal_unique (game_session_id, clue_code),
    KEY idx_am_game_clue_reveal_stage (game_session_id, revealed_stage_code),
    CONSTRAINT fk_am_game_clue_reveal_session
        FOREIGN KEY (game_session_id) REFERENCES am_game_session (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会话线索揭示表';

CREATE TABLE IF NOT EXISTS am_game_hint_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    game_session_id BIGINT NOT NULL COMMENT '会话ID',
    source_type VARCHAR(32) NOT NULL DEFAULT 'AI' COMMENT '提示来源：AI/FALLBACK',
    prompt_text LONGTEXT DEFAULT NULL COMMENT '提示生成 prompt',
    raw_reply_text LONGTEXT DEFAULT NULL COMMENT '模型原始返回',
    hint_list_json JSON DEFAULT NULL COMMENT '提示列表',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_am_game_hint_log_session (game_session_id, created_at),
    CONSTRAINT fk_am_game_hint_log_session
        FOREIGN KEY (game_session_id) REFERENCES am_game_session (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='追问提示日志表';

CREATE TABLE IF NOT EXISTS am_game_accusation (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    game_session_id BIGINT NOT NULL COMMENT '会话ID',
    accused_character_code VARCHAR(64) NOT NULL COMMENT '被指认角色编码',
    accused_character_name VARCHAR(64) DEFAULT NULL COMMENT '被指认角色名',
    reasoning_text LONGTEXT DEFAULT NULL COMMENT '玩家推理摘要',
    verdict_text LONGTEXT DEFAULT NULL COMMENT '结案判词',
    player_outcome_text LONGTEXT DEFAULT NULL COMMENT '玩家结局说明',
    truth_story LONGTEXT DEFAULT NULL COMMENT '真相故事',
    key_evidence_json JSON DEFAULT NULL COMMENT '关键证据回放',
    success_flag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否成功命中真凶',
    accusation_allowed_flag TINYINT(1) NOT NULL DEFAULT 1 COMMENT '提交时是否满足结案条件',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_am_game_accusation_session (game_session_id, created_at),
    CONSTRAINT fk_am_game_accusation_session
        FOREIGN KEY (game_session_id) REFERENCES am_game_session (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='最终指认记录表';

CREATE TABLE IF NOT EXISTS am_model_call_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    game_session_id BIGINT DEFAULT NULL COMMENT '会话ID，可空',
    request_type VARCHAR(32) NOT NULL COMMENT '调用类型：CHAT/HINT/GUARD/RECAP',
    provider_code VARCHAR(32) DEFAULT NULL COMMENT '模型供应商',
    model_name VARCHAR(64) DEFAULT NULL COMMENT '模型名称',
    trace_id VARCHAR(64) DEFAULT NULL COMMENT '追踪ID',
    system_prompt_text LONGTEXT DEFAULT NULL COMMENT '系统提示词',
    user_prompt_text LONGTEXT DEFAULT NULL COMMENT '用户提示词',
    response_text LONGTEXT DEFAULT NULL COMMENT '模型返回',
    request_tokens INT DEFAULT NULL COMMENT '请求 token 数',
    response_tokens INT DEFAULT NULL COMMENT '响应 token 数',
    total_tokens INT DEFAULT NULL COMMENT '总 token 数',
    latency_ms INT DEFAULT NULL COMMENT '耗时毫秒',
    success_flag TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否成功',
    error_code VARCHAR(64) DEFAULT NULL COMMENT '错误码',
    error_message TEXT DEFAULT NULL COMMENT '错误信息',
    ext_json JSON DEFAULT NULL COMMENT '扩展字段',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_am_model_call_log_session (game_session_id, created_at),
    KEY idx_am_model_call_log_trace (trace_id),
    CONSTRAINT fk_am_model_call_log_session
        FOREIGN KEY (game_session_id) REFERENCES am_game_session (id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='模型调用审计日志表';
