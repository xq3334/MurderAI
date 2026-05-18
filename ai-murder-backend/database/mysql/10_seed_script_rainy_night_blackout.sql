SET NAMES utf8mb4;
USE ai_murder;

INSERT INTO am_script (
    id, tenant_id, script_id, script_code, script_name, genre_code, theme_code,
    player_mode_code, player_mode_name, difficulty_level, total_character_count,
    selectable_character_count, recommended_player_count_min, recommended_player_count_max,
    estimated_duration_minutes, unlock_order, is_random_role_on_start, default_locale,
    current_version_no, publication_status, content_source_type, ext_json, remark
) VALUES (
    1001, 0, 'rainy-night-blackout', 'SCRIPT_RAIN_001', '雨夜断灯', 'MURDER_MYSTERY', 'MANOR',
    'SINGLE_PLAYER_AI', '固定主视角 / 山庄暴雨推理', 2, 6,
    5, 1, 1,
    45, 1, 0, 'zh-CN',
    1, 'PUBLISHED', 'MANUAL', JSON_OBJECT('seed_file', '10_seed_script_rainy_night_blackout.sql'), '首个内置山庄本'
);

INSERT INTO am_script_version (
    id, script_id, version_no, version_label, content_status, script_name, summary_text,
    opening_narration, opening_instruction, narration_instruction, truth_summary,
    ending_title, ending_story, minimum_key_clues_for_accusation, host_character_code,
    content_checksum, ext_json, remark
) VALUES (
    1101, 1001, 1, 'v1.0', 'PUBLISHED', '雨夜断灯',
    '暴雨封山后的老山庄突然停电，书房中的主人死在黑暗里。玩家将以局外侦查者视角进入现场，在矛盾口供与逐步浮出的线索之间逼近真凶。',
    '暴雨压着山庄檐角，整座宅邸像一盏随时会熄灭的旧灯。停电后的短短几分钟里，书房里倒下了今晚最不该倒下的人。',
    '先交代暴雨封庄、停电异变、玩家身份和第一轮调查目标，再让现场角色给出带保留的第一反应。',
    '旁白只负责环境、动作和气氛，不直接替任何角色公布答案。',
    '真凶是林乔。她提前动过配电箱，利用停电窗口进入书房行凶，并试图把怀疑转移到其他人身上。',
    '灯灭后的名字',
    '停电不是意外，而是林乔提前准备好的缺口。她知道账本残页迟早会把自己拖进深坑，于是在混乱里先一步灭口。配电箱、账本残页和死者临终留下的指向，最终把她从最冷静的位置上拽了下来。',
    2, 'butler', 'rainy-night-blackout-v1', JSON_OBJECT('era', 'modern', 'location', '山庄'), '与当前内存版剧本保持一致'
);

INSERT INTO am_script_character (
    id, script_version_id, character_code, character_name, identity_text, relationship_text,
    personality_tags_json, public_persona, public_backstory, private_backstory,
    public_objective, private_objective, opening_tip, response_strategy, seat_order,
    is_selectable_by_player, is_host, is_killer, is_accomplice, ext_json
) VALUES
    (11101, 1101, 'butler', '管家', '山庄管家', '负责今晚封庄后的秩序维持',
     JSON_ARRAY('沉稳', '克制', '控场'), '沉稳、克制、善于控场。', '他熟悉山庄里的每一道走廊和每个人的脾气，也最清楚今晚一旦失控会发生什么。', NULL,
     NULL, NULL, NULL, '负责控场、追问和转场，不直接公布答案。', 1,
     0, 1, 0, 0, JSON_OBJECT('role_group', 'host')),
    (11102, 1101, 'detective', '顾镜', '侦查者', '受邀来到山庄的独立调查者，负责临时主持这场问询',
     JSON_ARRAY('冷静', '克制', '追问'), '冷静、克制、擅长从矛盾里逼出真相。', '你不属于山庄旧关系网，因此每个人都希望借你的判断替自己洗清嫌疑。', NULL,
     '稳住局面，厘清停电前后的时间线与每个人的关键动向。', '把停电、账本和人际冲突拼成可验证的证据链。', '你不是嫌疑人，但你每一句判断都会改变现场的防御姿态。', '以提问、归纳和压迫式追索推动剧情。', 2,
     1, 0, 0, 0, JSON_OBJECT('default_player', true)),
    (11103, 1101, 'lin-qiao', '林乔', '财务顾问', '长期协助死者处理账目和外部资金安排',
     JSON_ARRAY('冷静', '干练', '防御'), '冷静、干练、擅长先听再答。', '她是死者最信任的账目处理人之一，对书房里的纸面秘密非常熟悉。', '她知道那本账册里有一页足以把自己拖进深坑。停电前，她已经决定先切断所有会指向自己的证据链。',
     '先稳住口供，不让别人把注意力锁死在账本和停电之间。', '把真正指向自己的线索推向别人的动机链。', '你要像场上最能稳住局面的人，绝不能抢着辩白。', '前期冷处理，中期开始反压追问，证据逼近时会露出裂口。', 3,
     1, 0, 1, 0, JSON_OBJECT('faction', 'killer')),
    (11104, 1101, 'gu-shen', '顾深', '律师', '负责死者近期遗嘱与资产重整',
     JSON_ARRAY('谨慎', '职业化', '保留'), '谨慎、职业化、说话留余地。', '他近来频繁出入山庄，为死者处理一份让很多人不安的遗嘱修订。', '晚饭前他确实和死者发生过激烈争执，但争执焦点并不是谋杀，而是尚未签字的资产安排。',
     '承认争执存在，但别让局面把争执直接等同于杀意。', '守住遗嘱内容，不让任何人借此看穿你真正担心的风险。', '你可以显得不耐烦，但不能失去律师应有的控制感。', '强调程序与证据，不轻易接任何带情绪的指控。', 4,
     1, 0, 0, 0, JSON_OBJECT()),
    (11105, 1101, 'zhou-yan', '周衍', '线路承包代表', '近期负责山庄电路检修',
     JSON_ARRAY('松散', '心虚', '回避细节'), '表面松散，实则很怕被追到技术细节。', '这座山庄的旧线路一直有问题，而他正是那个最容易被怀疑和停电有关的人。', '他确实在检修记录上留下过偷工减料的口子，但那不足以解释今晚这场精准停电。',
     '把停电解释成老线路风险，别让别人把它看成人为布局。', '护住自己的失职事实，别让自己变成最方便的替罪羊。', '你最怕别人把时间点和检修细节拼起来。', '遇到技术细节容易闪躲，但并不知道真正的凶手是谁。', 5,
     1, 0, 0, 0, JSON_OBJECT()),
    (11106, 1101, 'lu-chen', '陆沉', '死者侄子', '与死者的关系长期紧张',
     JSON_ARRAY('冷淡', '压抑', '反弹'), '冷淡、压抑，像随时会翻旧账。', '他与死者的矛盾从来不是秘密，很多人都知道他对这位长辈怀着真怨气。', '案发前他确实在书房外徘徊过，因为他想确认自己会不会被踢出继承链。',
     '别让所有人把怨气和杀人简单画等号。', '守住自己翻找文件的事，避免被定义成冲动行凶。', '你可以冷，但别冷得像已经认输。', '态度冷硬，不爱解释，但会对不公平指控有明显反弹。', 6,
     1, 0, 0, 0, JSON_OBJECT());

INSERT INTO am_script_character_secret (
    id, script_character_id, secret_type, secret_text, visible_from_stage_order, importance_level, sort_no
) VALUES
    (11401, 11102, 'KNOWN_FACT', '停电后的书房是第一现场。', 1, 4, 1),
    (11402, 11102, 'KNOWN_FACT', '所有嫌疑人都各自藏着不愿明说的动机与漏洞。', 1, 3, 2),
    (11403, 11103, 'KNOWN_FACT', '林乔知道账本里有对自己不利的内容。', 1, 4, 1),
    (11404, 11103, 'HIDDEN_SECRET', '林乔提前动过配电箱。', 1, 5, 2),
    (11405, 11103, 'HIDDEN_SECRET', '林乔趁黑进入书房行凶。', 1, 5, 3),
    (11406, 11103, 'FORBIDDEN_DISCLOSURE', '不能主动承认自己操纵停电。', 1, 5, 4),
    (11407, 11103, 'FORBIDDEN_DISCLOSURE', '不能主动承认自己就是凶手。', 1, 5, 5),
    (11408, 11104, 'HIDDEN_SECRET', '晚饭前顾深与死者有过激烈争执。', 1, 3, 1),
    (11409, 11104, 'FORBIDDEN_DISCLOSURE', '不会主动透露遗嘱具体去向。', 1, 4, 2),
    (11410, 11105, 'HIDDEN_SECRET', '周衍在检修记录上留过偷工减料的口子。', 1, 3, 1),
    (11411, 11105, 'FORBIDDEN_DISCLOSURE', '不会主动交代自己施工上的失职。', 1, 4, 2),
    (11412, 11106, 'KNOWN_FACT', '陆沉案发前在书房外短暂停留。', 1, 3, 1),
    (11413, 11106, 'HIDDEN_SECRET', '陆沉翻找过书房附近的文件与抽屉。', 1, 4, 2),
    (11414, 11106, 'FORBIDDEN_DISCLOSURE', '不会主动承认自己动过书房附近的东西。', 1, 4, 3);

INSERT INTO am_script_stage (
    id, script_version_id, stage_code, stage_name, stage_order, objective_text,
    opening_narration, advance_condition_text, minimum_turns_before_advance, stage_theme_code, atmosphere_tag, ext_json
) VALUES
    (11201, 1101, 'rain-stage-1', '暴雨封庄', 1,
     '先稳住局面，确认停电前后每个人的大致位置和第一轮口供。',
     '停电后的几分钟被每个人说成了不同的样子。越试图回忆，裂缝越像雨水一样沿着墙缝渗出来。',
     '当玩家开始把停电和具体人物联系起来时进入下一阶段。', 2, 'OPENING', '压抑', JSON_OBJECT()),
    (11202, 1101, 'rain-stage-2', '黑暗不是意外', 2,
     '把调查重点从动机推到手法，逼出谁最了解这场停电。',
     '真正危险的不是谁有怨，而是谁能把混乱精确地安排进那几分钟黑暗里。',
     '当玩家把停电、账本和林乔逐步串联起来时进入下一阶段。', 2, 'METHOD', '逼近', JSON_OBJECT()),
    (11203, 1101, 'rain-stage-3', '死者留下了指向', 3,
     '拼起决定性证据，完成最终指认。',
     '死者最后留下的不是答案，而是一道足够锋利的指向。',
     '最终阶段，等待玩家完成指认。', 99, 'ACCUSATION', '收束', JSON_OBJECT());

INSERT INTO am_script_stage_focus_character (
    id, script_stage_id, script_character_id, sort_no
) VALUES
    (11501, 11201, 11103, 1),
    (11502, 11201, 11104, 2),
    (11503, 11201, 11105, 3),
    (11504, 11201, 11106, 4),
    (11505, 11202, 11103, 1),
    (11506, 11202, 11105, 2),
    (11507, 11203, 11103, 1);

INSERT INTO am_script_stage_keyword (
    id, script_stage_id, keyword_text, keyword_type, trigger_weight, sort_no
) VALUES
    (11601, 11201, '停电', 'ADVANCE', 2, 1),
    (11602, 11201, '配电箱', 'ADVANCE', 2, 2),
    (11603, 11201, '人为', 'ADVANCE', 1, 3),
    (11604, 11201, '时间线', 'ADVANCE', 1, 4),
    (11605, 11202, '账本', 'ADVANCE', 2, 1),
    (11606, 11202, '残页', 'ADVANCE', 2, 2),
    (11607, 11202, '字迹', 'ADVANCE', 1, 3),
    (11608, 11202, '林乔', 'ADVANCE', 2, 4),
    (11609, 11203, '最终指认', 'ADVANCE', 2, 1),
    (11610, 11203, '真凶', 'ADVANCE', 2, 2);

INSERT INTO am_script_clue (
    id, script_version_id, clue_code, clue_name, clue_type, content_text, effect_text,
    first_unlock_stage_code, reveal_mode_code, evidence_chain_code, is_key_clue, is_reusable, ext_json
) VALUES
    (11301, 1101, 'clue-argue', '晚餐争执', 'TESTIMONY',
     '晚饭前，顾深曾与死者在走廊尽头发生过一次短促但明显的争执。',
     '制造顾深的嫌疑。', 'rain-stage-1', 'AUTO', 'RAIN_CHAIN_A', 0, 1, JSON_OBJECT()),
    (11302, 1101, 'clue-corridor', '书房外脚步', 'TESTIMONY',
     '案发前后，有人看见陆沉曾在书房外短暂停留。',
     '制造陆沉的嫌疑。', 'rain-stage-1', 'AUTO', 'RAIN_CHAIN_A', 0, 1, JSON_OBJECT()),
    (11303, 1101, 'clue-power-box', '配电箱异常', 'ENVIRONMENT',
     '配电箱的负载开关存在被人提前调动过的痕迹，这次停电不像自然跳闸。',
     '说明停电具有明显的人为痕迹。', 'rain-stage-2', 'AUTO', 'RAIN_CHAIN_B', 1, 1, JSON_OBJECT()),
    (11304, 1101, 'clue-ledger', '账本残页', 'DOCUMENT',
     '书房账本中有一页被撕走，撕口纤维与林乔衣袖残留的细丝高度一致。',
     '把林乔与关键账本直接联系起来。', 'rain-stage-2', 'AUTO', 'RAIN_CHAIN_B', 1, 1, JSON_OBJECT()),
    (11305, 1101, 'clue-note', '临终字迹', 'DOCUMENT',
     '死者便签背面留下一个未写完的字，形态接近“乔”字左侧的结构。',
     '形成对林乔的最终指向。', 'rain-stage-3', 'AUTO', 'RAIN_CHAIN_C', 1, 1, JSON_OBJECT());

INSERT INTO am_script_stage_clue_rel (
    id, script_stage_id, script_clue_id, reveal_order, reveal_mode_code, is_default_available
) VALUES
    (11701, 11201, 11301, 1, 'AUTO', 1),
    (11702, 11201, 11302, 2, 'AUTO', 1),
    (11703, 11202, 11301, 1, 'AUTO', 1),
    (11704, 11202, 11302, 2, 'AUTO', 1),
    (11705, 11202, 11303, 3, 'AUTO', 1),
    (11706, 11202, 11304, 4, 'AUTO', 1),
    (11707, 11203, 11301, 1, 'AUTO', 1),
    (11708, 11203, 11302, 2, 'AUTO', 1),
    (11709, 11203, 11303, 3, 'AUTO', 1),
    (11710, 11203, 11304, 4, 'AUTO', 1),
    (11711, 11203, 11305, 5, 'AUTO', 1);

INSERT INTO am_script_clue_character_rel (
    id, script_clue_id, script_character_id, relation_type, weight_score
) VALUES
    (11801, 11301, 11104, 'RELATED', 5),
    (11802, 11302, 11106, 'RELATED', 5),
    (11803, 11303, 11103, 'RELATED', 5),
    (11804, 11303, 11105, 'RELATED', 3),
    (11805, 11304, 11103, 'RELATED', 5),
    (11806, 11305, 11103, 'RELATED', 5);
