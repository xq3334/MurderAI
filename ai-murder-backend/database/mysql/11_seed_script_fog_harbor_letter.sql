SET NAMES utf8mb4;
USE ai_murder;

INSERT INTO am_script (
    id, tenant_id, script_id, script_code, script_name, genre_code, theme_code,
    player_mode_code, player_mode_name, difficulty_level, total_character_count,
    selectable_character_count, recommended_player_count_min, recommended_player_count_max,
    estimated_duration_minutes, unlock_order, is_random_role_on_start, default_locale,
    current_version_no, publication_status, content_source_type, ext_json, remark
) VALUES (
    2001, 0, 'fog-harbor-letter', 'SCRIPT_FOG_001', '雾港来信', 'MURDER_MYSTERY', 'HARBOR',
    'SINGLE_PLAYER_AI_RANDOM_ROLE', '解锁副本 / 随机身份', 3, 6,
    5, 1, 1,
    55, 2, 1, 'zh-CN',
    1, 'PUBLISHED', 'MANUAL', JSON_OBJECT('seed_file', '11_seed_script_fog_harbor_letter.sql'), '港口旧案副本'
);

INSERT INTO am_script_version (
    id, script_id, version_no, version_label, content_status, script_name, summary_text,
    opening_narration, opening_instruction, narration_instruction, truth_summary,
    ending_title, ending_story, minimum_key_clues_for_accusation, host_character_code,
    content_checksum, ext_json, remark
) VALUES (
    2101, 2001, 1, 'v1.0', 'PUBLISHED', '雾港来信',
    '封港夜里，旧旅馆收到一封迟到二十年的信。馆主正要当众拆信时突然身亡，旧案与新案被迫同时翻开。',
    '港口的雾比雨更让人不安。那封本该在二十年前送达的信，偏偏在今晚抵达旧旅馆，而馆主在众人的视线落到信封之前先一步倒了下去。',
    '先交代封港、迟到来信、馆主之死和玩家身份，再让现场人带着各自的旧案顾虑开口。',
    '旁白负责海雾、旅馆空间、信件和众人反应，不直接替角色公布旧案真相。',
    '真凶是沈迟。二十年前的沉船事故并非单纯天灾，而是与走私掩盖有关。来信会把她父亲和旅馆主人共同掩埋的旧事重新拽回台面，她于是抢在拆信前灭口。',
    '雾散时的收件人',
    '那封信之所以迟到二十年，是因为它原本就不该存在。信里记录着沉船夜真正的货单和港务编号，而沈迟知道，只要馆主当众拆开，自己家与旧案的牵连就再也藏不住。她选择在所有人都盯着信的那一刻，先让唯一能完整说出往事的人闭嘴。',
    2, 'innkeeper', 'fog-harbor-letter-v1', JSON_OBJECT('era', 'modern', 'location', '港口旅馆'), '与当前内存版剧本保持一致'
);

INSERT INTO am_script_character (
    id, script_version_id, character_code, character_name, identity_text, relationship_text,
    personality_tags_json, public_persona, public_backstory, private_backstory,
    public_objective, private_objective, opening_tip, response_strategy, seat_order,
    is_selectable_by_player, is_host, is_killer, is_accomplice, ext_json
) VALUES
    (21101, 2101, 'innkeeper', '许掌柜', '旧旅馆掌柜', '封港夜里唯一还能勉强维持秩序的人',
     JSON_ARRAY('见过风浪', '沉稳', '压场'), '见过风浪，话少，但能镇场。', '这家旧旅馆在港口撑了很多年，掌柜知道太多人的往来，也知道什么时候该把话收住。', NULL,
     NULL, NULL, NULL, '负责控场和催问，不替人作答。', 1,
     0, 1, 0, 0, JSON_OBJECT('role_group', 'host')),
    (21102, 2101, 'shen-chi', '沈迟', '港务档案员', '馆主多年替她家遮掩过一段旧账',
     JSON_ARRAY('清醒', '冷白', '压情绪'), '清醒、冷白、很会把情绪压成礼貌。', '她是港务网里最熟悉旧档的人之一，因此也最容易成为那封来信的天然解读者。', '她早就知道二十年前的沉船夜不是意外，而是与走私和掩盖有关。来信一旦被拆开，她父亲与馆主共同埋下的旧事就会重新浮出水面。',
     '以专业身份参与梳理来信和档案，别让别人看出你比所有人都更怕信被拆开。', '把调查引向别人的旧怨和财务动机，拖走对来信源头的追问。', '你最强的武器是冷静和专业感，不是抢先辩解。', '前期以档案员姿态引导视线，中后期在旧案细节上会变得异常敏感。', 2,
     1, 0, 1, 0, JSON_OBJECT('faction', 'killer')),
    (21103, 2101, 'yu-lan', '余岚', '调查记者', '追查沉船旧闻多年，今晚以普通住客身份潜入',
     JSON_ARRAY('敏锐', '克制', '归纳'), '敏锐、克制，总在等别人先说漏一句。', '她长期追踪港口旧案，知道死者和很多旧名字之间并不干净。', '她收到过匿名线索才赶来旅馆，但不能暴露自己的消息源，否则整条线会立刻断掉。',
     '把今晚的局面推到能说真话的位置。', '保护线索来源，同时确认那封信到底指向谁。', '你说话可以像在采访，但别让人觉得你早有准备。', '擅长追问和归纳别人口供，不轻易摊出全部底牌。', 3,
     1, 0, 0, 0, JSON_OBJECT()),
    (21104, 2101, 'he-mu', '何沐', '馆主养女', '从小在旅馆长大，与馆主关系最复杂',
     JSON_ARRAY('柔和', '安静', '迟疑'), '柔和、安静，总像在替别人留情面。', '她几乎把旅馆当成唯一的家，也最清楚馆主这些年为什么不愿离开港口。', '她怀疑自己的身世和沉船夜有关，但一直没敢真的拆开那层关系。',
     '守住旅馆和养父最后的体面。', '确认自己与旧案之间到底有没有更深的血缘牵连。', '你知道的不是最多，但你的沉默最容易被误读。', '前期回避旧案细节，越被逼近越容易露出情绪停顿。', 4,
     1, 0, 0, 0, JSON_OBJECT()),
    (21105, 2101, 'duan-lin', '段临', '退休警员', '当年沉船案外围调查者之一',
     JSON_ARRAY('老练', '怀疑心重', '谨慎'), '老练、怀疑心重，不爱被人拿旧年资压住。', '他曾短暂接触过沉船事故的外围调查，却在关键时刻被请出局。', '他并非全然无辜，当年接受过一笔不该收的封口礼，因此这些年始终不愿再提港口旧案。',
     '把怀疑拉回证据，别让旧案情绪直接吞掉今晚判断。', '别让任何人追到你当年的失守，否则你会和旧案一起沉下去。', '你可以像办案的人，但不能像已经知道全部的人。', '擅长盯细节，但提到当年办案流程时会明显谨慎。', 5,
     1, 0, 0, 0, JSON_OBJECT()),
    (21106, 2101, 'qiao-yue', '乔月', '码头调度员', '负责今夜封港期间的货船登记与去留',
     JSON_ARRAY('利落', '嘴硬', '熟规则'), '利落、嘴硬、对港口规则极熟。', '她常年在码头调度进出船次，对谁该出港、谁不该靠岸有近乎本能的敏感。', '她并不直接参与旧案，但发现今晚有人试图借封港掩护处理一份旧货单。',
     '守住自己在今晚流程上的专业权威。', '确认是谁试图在封港夜里动旧货单，再决定要不要交出编号。', '你不是旧案当事人，但你手里可能有今晚最硬的流程线索。', '对流程问题很强势，对旧案情感纠葛则显得格外不耐烦。', 6,
     1, 0, 0, 0, JSON_OBJECT());

INSERT INTO am_script_character_secret (
    id, script_character_id, secret_type, secret_text, visible_from_stage_order, importance_level, sort_no
) VALUES
    (21401, 21102, 'KNOWN_FACT', '沈迟熟悉旧港务档案。', 1, 3, 1),
    (21402, 21102, 'KNOWN_FACT', '沉船案档案有缺页。', 1, 3, 2),
    (21403, 21102, 'HIDDEN_SECRET', '沈迟提前接触过那封来信。', 1, 5, 3),
    (21404, 21102, 'HIDDEN_SECRET', '沈迟在众人拆信前先下手灭口。', 1, 5, 4),
    (21405, 21102, 'FORBIDDEN_DISCLOSURE', '不能主动承认自己和旧案掩盖有关。', 1, 5, 5),
    (21406, 21103, 'HIDDEN_SECRET', '余岚是带着匿名线索潜入旅馆的。', 1, 4, 1),
    (21407, 21103, 'FORBIDDEN_DISCLOSURE', '不能主动曝光消息源。', 1, 4, 2),
    (21408, 21104, 'HIDDEN_SECRET', '何沐见过寄信人留下的一张旧照片。', 1, 3, 1),
    (21409, 21105, 'HIDDEN_SECRET', '段临当年收过封口礼。', 1, 4, 1),
    (21410, 21105, 'FORBIDDEN_DISCLOSURE', '不会主动承认自己收过钱。', 1, 4, 2),
    (21411, 21106, 'KNOWN_FACT', '封港前有一条异常登记被改写。', 1, 3, 1),
    (21412, 21106, 'HIDDEN_SECRET', '乔月私下抄下过异常货单编号。', 1, 4, 2);

INSERT INTO am_script_stage (
    id, script_version_id, stage_code, stage_name, stage_order, objective_text,
    opening_narration, advance_condition_text, minimum_turns_before_advance, stage_theme_code, atmosphere_tag, ext_json
) VALUES
    (21201, 2101, 'fog-stage-1', '封港夜的第一封信', 1,
     '厘清来信到场、馆主倒下与众人第一反应之间的顺序。',
     '旅馆门窗都关着，海雾却像从缝里挤了进来。所有人都盯着那封信，却又比看信更在意别人的表情。',
     '当玩家开始围绕来信触碰顺序和封港流程追问时进入下一阶段。', 2, 'OPENING', '潮湿压迫', JSON_OBJECT()),
    (21202, 2101, 'fog-stage-2', '旧案浮出水面', 2,
     '把来信与二十年前的沉船旧案连接起来，锁定谁最怕旧档被翻出。',
     '真正让人沉不住气的不是馆主的死，而是那封信证明旧案从来没有真正沉下去。',
     '当玩家把档案缺页、旧照片和沈迟的异常反应串起来时进入下一阶段。', 2, 'OLD_CASE', '翻涌', JSON_OBJECT()),
    (21203, 2101, 'fog-stage-3', '真正的收件人', 3,
     '用决定性证据完成最终指认。',
     '那封信不是寄给旅馆的，而是寄给过去。现在只剩最后一步，要看谁会被名字重新拖回海雾里。',
     '最终阶段，等待玩家完成指认。', 99, 'ACCUSATION', '冷收束', JSON_OBJECT());

INSERT INTO am_script_stage_focus_character (
    id, script_stage_id, script_character_id, sort_no
) VALUES
    (21501, 21201, 21102, 1),
    (21502, 21201, 21103, 2),
    (21503, 21201, 21104, 3),
    (21504, 21201, 21105, 4),
    (21505, 21201, 21106, 5),
    (21506, 21202, 21102, 1),
    (21507, 21202, 21105, 2),
    (21508, 21202, 21104, 3),
    (21509, 21203, 21102, 1);

INSERT INTO am_script_stage_keyword (
    id, script_stage_id, keyword_text, keyword_type, trigger_weight, sort_no
) VALUES
    (21601, 21201, '来信', 'ADVANCE', 2, 1),
    (21602, 21201, '封港', 'ADVANCE', 1, 2),
    (21603, 21201, '第一反应', 'ADVANCE', 1, 3),
    (21604, 21201, '钟声', 'ADVANCE', 1, 4),
    (21605, 21202, '旧案', 'ADVANCE', 2, 1),
    (21606, 21202, '沉船', 'ADVANCE', 2, 2),
    (21607, 21202, '档案', 'ADVANCE', 1, 3),
    (21608, 21202, '照片', 'ADVANCE', 1, 4),
    (21609, 21202, '沈迟', 'ADVANCE', 2, 5),
    (21610, 21203, '最终指认', 'ADVANCE', 2, 1),
    (21611, 21203, '收件人', 'ADVANCE', 2, 2);

INSERT INTO am_script_clue (
    id, script_version_id, clue_code, clue_name, clue_type, content_text, effect_text,
    first_unlock_stage_code, reveal_mode_code, evidence_chain_code, is_key_clue, is_reusable, ext_json
) VALUES
    (21301, 2101, 'fog-clue-register', '异常登记', 'DOCUMENT',
     '封港前最后一份进出港登记有被人二次改写的痕迹，改写时间就在来信送达前后。',
     '说明今夜有人先在流程层面动过手。', 'fog-stage-1', 'AUTO', 'FOG_CHAIN_A', 0, 1, JSON_OBJECT()),
    (21302, 2101, 'fog-clue-bell', '钟声证词', 'TESTIMONY',
     '旅馆楼梯口的旧钟在馆主倒下前刚敲过一次半点，至少有一人的口供比钟声提前了。',
     '撬开第一轮时间线矛盾。', 'fog-stage-1', 'AUTO', 'FOG_CHAIN_A', 0, 1, JSON_OBJECT()),
    (21303, 2101, 'fog-clue-file', '缺页档案', 'DOCUMENT',
     '港务旧档中沉船案编号对应页缺失，而缺页边角残留了档案专用封签纤维。',
     '把旧案缺页与档案系统内部人员联系起来。', 'fog-stage-2', 'AUTO', 'FOG_CHAIN_B', 1, 1, JSON_OBJECT()),
    (21304, 2101, 'fog-clue-photo', '湿痕旧照', 'DOCUMENT',
     '一张多年前的码头合影背面残留潮湿指痕，馆主、沈迟父亲和沉船夜的货主都在照片里。',
     '证明旧案关联远比众人承认的更直接。', 'fog-stage-2', 'AUTO', 'FOG_CHAIN_B', 1, 1, JSON_OBJECT()),
    (21305, 2101, 'fog-clue-letter', '迟到来信', 'DOCUMENT',
     '拆开的信里写着完整货单编号和一句话：真正该收到这封信的人，一直住在档案里。',
     '把来信矛头直接引向沈迟。', 'fog-stage-3', 'AUTO', 'FOG_CHAIN_C', 1, 1, JSON_OBJECT());

INSERT INTO am_script_stage_clue_rel (
    id, script_stage_id, script_clue_id, reveal_order, reveal_mode_code, is_default_available
) VALUES
    (21701, 21201, 21301, 1, 'AUTO', 1),
    (21702, 21201, 21302, 2, 'AUTO', 1),
    (21703, 21202, 21301, 1, 'AUTO', 1),
    (21704, 21202, 21302, 2, 'AUTO', 1),
    (21705, 21202, 21303, 3, 'AUTO', 1),
    (21706, 21202, 21304, 4, 'AUTO', 1),
    (21707, 21203, 21301, 1, 'AUTO', 1),
    (21708, 21203, 21302, 2, 'AUTO', 1),
    (21709, 21203, 21303, 3, 'AUTO', 1),
    (21710, 21203, 21304, 4, 'AUTO', 1),
    (21711, 21203, 21305, 5, 'AUTO', 1);

INSERT INTO am_script_clue_character_rel (
    id, script_clue_id, script_character_id, relation_type, weight_score
) VALUES
    (21801, 21301, 21106, 'RELATED', 5),
    (21802, 21302, 21102, 'RELATED', 4),
    (21803, 21302, 21105, 'RELATED', 4),
    (21804, 21303, 21102, 'RELATED', 5),
    (21805, 21304, 21102, 'RELATED', 5),
    (21806, 21304, 21104, 'RELATED', 3),
    (21807, 21305, 21102, 'RELATED', 5);
