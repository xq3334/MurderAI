# AI Murder Backend Database

这套 SQL 以 MySQL 8.0 为目标，按“剧本模板层 + 游戏运行层”设计，目的是把当前内存版后端未来平滑迁移到数据库。

## 执行顺序

1. `00_create_database.sql`
2. `01_schema.sql`
3. `10_seed_script_rainy_night_blackout.sql`
4. `11_seed_script_fog_harbor_letter.sql`
5. `12_seed_script_summer_evening_cicadas.sql`

## 设计说明

- `am_script` / `am_script_version`：
  剧本主数据与版本数据分离，便于以后做剧本修订、灰度发布、AB 版本、回滚。
- `am_script_character` / `am_script_character_secret`：
  角色基础信息与秘密信息分离，方便后续做阶段可见性控制、多人模式、运营编辑。
- `am_script_stage` / `am_script_clue`：
  阶段、线索、推进关键词、聚焦角色全部显式建模，贴合当前后端状态机。
- `am_game_*`：
  运行态会话、消息流、线索揭示、场景旁白、提示词、最终指认、模型调用日志拆表保存，便于回放、审计、分析和重试。
- `ext_json`：
  绝大多数核心表都预留了 JSON 扩展字段，方便以后挂载运营配置、埋点、AI 配置、前端展示附加信息。

## 当前状态

目前 Java 代码仍然使用 `InMemoryScriptRepository` 和内存会话。本目录先提供数据库设计与初始化脚本，不改动现有运行逻辑。
