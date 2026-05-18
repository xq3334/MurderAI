SET NAMES utf8mb4;
USE ai_murder;

INSERT INTO am_script (
    id, tenant_id, script_id, script_code, script_name, genre_code, theme_code,
    player_mode_code, player_mode_name, difficulty_level, total_character_count,
    selectable_character_count, recommended_player_count_min, recommended_player_count_max,
    estimated_duration_minutes, unlock_order, is_random_role_on_start, default_locale,
    current_version_no, publication_status, content_source_type, ext_json, remark
) VALUES (
    3001, 0, 'summer-evening-cicadas', 'SCRIPT_SUMMER_001', '蝉鸣晚自习', 'MURDER_MYSTERY', 'CAMPUS',
    'SINGLE_PLAYER_AI', '固定主视角 / 校园关系推理', 3, 7,
    1, 1, 1,
    60, 3, 0, 'zh-CN',
    1, 'PUBLISHED', 'MANUAL', JSON_OBJECT('seed_file', '12_seed_script_summer_evening_cicadas.sql'), '校园关系本'
);

INSERT INTO am_script_version (
    id, script_id, version_no, version_label, content_status, script_name, summary_text,
    opening_narration, opening_instruction, narration_instruction, truth_summary,
    ending_title, ending_story, minimum_key_clues_for_accusation, host_character_code,
    content_checksum, ext_json, remark
) VALUES (
    3101, 3001, 1, 'v1.0', 'PUBLISHED', '蝉鸣晚自习',
    '高二晚自习前，一张匿名纸条和一段被剪辑过的语音，把几个关系缠绕的少年少女同时推到灯下。玩家固定扮演许知夏，从暗恋与误会里一路摸到每个人真正想藏住的秘密。',
    '傍晚的热气还压在教学楼里，风扇转得很慢，窗外操场尽头的蝉声却已经响成一整面墙。你刚把书包塞进桌洞，就看见习题册下面有一点不该出现的银色反光。',
    '先交代高二晚自习前的教室氛围、玩家身份和最初异常，再让其余角色依次给出第一反应，但保留真正的秘密。',
    '旁白负责教室、走廊、广播室和课间气氛的推进，要像青春文学里的镜头，不直接替角色给出答案。',
    '真正故意推动这场矛盾的人是林澄。她放出匿名纸条和被剪过的语音，不是为了毁掉谁，而是为了逼周屿停止继续替叶真扛下那次违纪旧事，也逼所有人面对已经藏不住的关系裂缝。',
    '晚自习后的名字',
    '匿名纸条只是刀尖，真正被剥开的，是每个人小心维持的体面。林澄故意把矛盾推到所有人都无法再装作没看见的位置，想逼周屿和叶真把那次违纪、代扛与沉默全都说出来。许知夏最终看清的，不只是自己喜欢的人，更是每个人在那个夏夜里各自背着的重量。',
    3, 'homeroom-teacher', 'summer-evening-cicadas-v1', JSON_OBJECT('era', 'campus', 'location', '高中教室'), '与当前内存版剧本保持一致'
);

INSERT INTO am_script_character (
    id, script_version_id, character_code, character_name, identity_text, relationship_text,
    personality_tags_json, public_persona, public_backstory, private_backstory,
    public_objective, private_objective, opening_tip, response_strategy, seat_order,
    is_selectable_by_player, is_host, is_killer, is_accomplice, ext_json
) VALUES
    (31101, 3101, 'homeroom-teacher', '陈老师', '高二二班班主任', '维持晚自习秩序的成年人，也是这场风暴外围唯一的成人目光',
     JSON_ARRAY('稳', '克制', '不贴标签'), '稳、克制、尽量不给任何学生贴死标签。', '陈老师今晚不会一直站在教室里，但她的存在让所有人都还在努力维持最后一点体面。', NULL,
     NULL, NULL, NULL, '负责控场、转场和提醒时间，不替学生作答。', 1,
     0, 1, 0, 0, JSON_OBJECT('role_group', 'host')),
    (31102, 3101, 'xu-zhixia', '许知夏', '高二二班语文课代表', '站在所有关系边缘的人，却恰好最容易看见细节',
     JSON_ARRAY('安静', '细腻', '观察型'), '安静、细腻，不擅长争抢存在感。', '你习惯把很多话咽回去，把喜欢藏起来，也把不确定的目光留给自己消化。', '你暗恋周屿很久，也隐约知道宋晚已经看出来了。更重要的是，你曾在值日那天捡到一张写着“你不该替她扛着”的便签，却一直没敢问出口。',
     '先弄明白今天到底发生了什么，不要让局面完全失控。', '看清每个人之间真正的关系，也看清周屿、宋晚和林澄到底各自隐瞒了什么。', '你不是最会说话的人，但你最擅长从别人漏掉的停顿里听出不对劲。', '玩家固定扮演角色，由玩家自由提问、判断和推进。', 2,
     1, 0, 0, 0, JSON_OBJECT('default_player', true)),
    (31103, 3101, 'zhou-yu', '周屿', '篮球队主力', '许知夏暗恋对象，也是最容易被误会成故事中心的人',
     JSON_ARRAY('开朗', '松弛表象', '强撑'), '开朗、松弛，看起来总能把气氛接住。', '他在班里一直很显眼，成绩不差，也总像没什么真正过不去的事。', '他最近压力很大，家里关系恶化，又一直替叶真扛着一次足以影响评优的违纪记录。林澄知道这件事，并帮他一起压过。',
     '把今晚所有关于感情的误会压下去，别让局面越闹越大。', '守住叶真那次违纪的真相，也别让林澄因为帮忙收尾被一起拖进风暴里。', '你越想稳住局面，越容易在关键细节上露出你知道得太多。', '前期会用轻松语气打圆场，被追到时间线和违纪细节时会明显收紧。', 3,
     0, 0, 0, 0, JSON_OBJECT()),
    (31104, 3101, 'lin-cheng', '林澄', '班长', '最擅长维持秩序的人，也是今晚真正故意推动失衡的人',
     JSON_ARRAY('理性', '稳', '锋利'), '理性、稳，总能先把场面接住。', '她习惯记住每个人的作业、座位、值日和情绪，像那个把教室秩序缝起来的人。', '她已经厌倦了替所有人收残局。匿名纸条和剪辑语音都是她放出来的，她想逼周屿停止再替叶真扛事，也逼宋晚和程野别再靠沉默回避真相。',
     '把所有人从无效争吵里拉回事实，至少表面上看起来如此。', '逼真正该开口的人自己说出那次违纪、那次代扛和后来的沉默。', '你看起来最像秩序本身，所以不到最后，不该有人轻易觉得你在推波助澜。', '前期控场，中期转为反问和逼问，证据逼近时会从冷静变得尖锐。', 4,
     0, 0, 1, 0, JSON_OBJECT('faction', 'truth-pusher')),
    (31105, 3101, 'song-wan', '宋晚', '文艺委员', '许知夏最亲近的朋友，也是许多沉默证据的保管者',
     JSON_ARRAY('温柔', '安静', '留余地'), '温柔、安静，总像会替别人留余地。', '她总不在风暴中心，却经常在别人说完之后补出最难忽略的一句。', '她一直知道许知夏喜欢周屿，也留着那些别人随手丢掉的纸条、照片和一封没送出的信。她不是故意护着谁，只是不知道什么时候说出来才不会伤人。',
     '别让今晚把所有人的关系彻底撕裂。', '尽量保护许知夏，也尽量别让自己手里的东西变成伤人的证据。', '你知道得越多，越会在关键时刻显得犹豫。', '前期温和回避，被线索点穿后会说出很关键的补充细节。', 5,
     0, 0, 0, 0, JSON_OBJECT()),
    (31106, 3101, 'cheng-ye', '程野', '转学生', '最像旁观者的人，却和其中一段旧事有真实牵连',
     JSON_ARRAY('冷淡', '疏离', '观察'), '冷淡、疏离，像对谁都没兴趣。', '他来这个班不久，和所有人都保持距离，所以任何情绪起伏都显得格外明显。', '他初中时认识叶真，也知道她不是那种会无缘无故失控的人。转来之后，他很快发现周屿和林澄一直在共同掩盖什么。',
     '先确认今晚是谁在故意引爆局面。', '别让叶真一个人吞下所有情绪反应，也别让自己和她的旧事变成新的误会。', '你可以冷眼旁观，但不能像已经知道全部答案。', '前期像观察者，后期会精准指出别人回避的细节。', 6,
     0, 0, 0, 0, JSON_OBJECT()),
    (31107, 3101, 'ye-zhen', '叶真', '体育委员', '看起来最像矛盾制造者，实际上是被保护最久的人',
     JSON_ARRAY('直', '急', '会顶撞'), '直、急、容易顶撞人。', '她不擅长把委屈藏得很深，所以很多人都以为她的问题最好懂。', '那次真正违纪的人是她。周屿替她顶下记录，林澄帮忙压住，宋晚无意中保留了能还原那晚动线的照片。她今晚发火，是因为她知道有人在拿感情线掩盖真正的问题。',
     '别让所有人把今晚说成一场无聊的感情戏。', '如果真相一定要出来，至少别再让周屿替自己站在前面。', '你的情绪是真的，所以一旦退缩，别人就会立刻知道你碰到了最痛的地方。', '前期易爆，后期在真正被追到核心时反而会短暂沉默。', 7,
     0, 0, 0, 0, JSON_OBJECT());

INSERT INTO am_script_character_secret (
    id, script_character_id, secret_type, secret_text, visible_from_stage_order, importance_level, sort_no
) VALUES
    (31401, 31102, 'KNOWN_FACT', '宋晚大概知道许知夏的暗恋。', 1, 2, 1),
    (31402, 31102, 'KNOWN_FACT', '周屿最近状态不对。', 1, 2, 2),
    (31403, 31102, 'KNOWN_FACT', '晚自习前自己的桌洞里出现了不该有的东西。', 1, 3, 3),
    (31404, 31103, 'KNOWN_FACT', '林澄知道自己的一部分难处。', 1, 2, 1),
    (31405, 31103, 'HIDDEN_SECRET', '周屿替叶真扛过违纪。', 1, 5, 2),
    (31406, 31103, 'HIDDEN_SECRET', '周屿知道语音被剪过，却没第一时间拆穿。', 1, 4, 3),
    (31407, 31103, 'FORBIDDEN_DISCLOSURE', '不能主动说出自己在替谁扛违纪。', 1, 5, 4),
    (31408, 31104, 'KNOWN_FACT', '林澄知道违纪记录是怎么被压住的。', 1, 4, 1),
    (31409, 31104, 'HIDDEN_SECRET', '匿名纸条是林澄放出来的。', 1, 5, 2),
    (31410, 31104, 'HIDDEN_SECRET', '剪辑语音的扩散也是林澄推动的。', 1, 5, 3),
    (31411, 31104, 'FORBIDDEN_DISCLOSURE', '不能主动承认自己故意安排了这场失衡。', 1, 5, 4),
    (31412, 31105, 'KNOWN_FACT', '宋晚知道许知夏喜欢周屿。', 1, 2, 1),
    (31413, 31105, 'HIDDEN_SECRET', '宋晚留着没送出的信和旧照片。', 1, 4, 2),
    (31414, 31106, 'KNOWN_FACT', '程野知道流出的语音不是原始版本。', 1, 3, 1),
    (31415, 31106, 'HIDDEN_SECRET', '程野和叶真是旧识。', 1, 3, 2),
    (31416, 31107, 'KNOWN_FACT', '周屿替自己扛过一次违纪。', 1, 4, 1),
    (31417, 31107, 'HIDDEN_SECRET', '真正违纪的人其实是叶真。', 1, 5, 2),
    (31418, 31107, 'FORBIDDEN_DISCLOSURE', '不能主动承认违纪的人是自己。', 1, 5, 3);

INSERT INTO am_script_stage (
    id, script_version_id, stage_code, stage_name, stage_order, objective_text,
    opening_narration, advance_condition_text, minimum_turns_before_advance, stage_theme_code, atmosphere_tag, ext_json
) VALUES
    (31201, 3101, 'summer-stage-1', '晚自习前的反光', 1,
     '确认匿名纸条、桌洞反光和第一轮走廊争执分别牵住了谁。',
     '教室还没有完全安静下来，翻书声、桌脚声和窗外蝉鸣混在一起。所有人看起来都还坐在自己的位置上，但真正先乱掉的是那些没有被说出口的视线。',
     '当玩家开始把匿名纸条和具体人物反应联系起来时进入下一阶段。', 2, 'OPENING', '闷热', JSON_OBJECT()),
    (31202, 3101, 'summer-stage-2', '被剪过的声音', 2,
     '把矛盾从表面的喜欢和误会，推进到谁在故意传递不完整的信息。',
     '真正开始发热的不是空气，而是每个人开口前那半秒钟停顿。有人希望这只是一场感情误会，也有人拼命不让话题往更深处走。',
     '当玩家追问语音来源、照片残片和异常值日记录时进入下一阶段。', 2, 'AUDIO', '试探升级', JSON_OBJECT()),
    (31203, 3101, 'summer-stage-3', '课间没有散掉', 3,
     '还原违纪旧事、代扛链条和林澄故意引爆矛盾的真正目的。',
     '课间铃已经响过，但没有一个人真的从这场局里走出去。窗外的风进来了，教室里的热却一点没散。',
     '当玩家把违纪、代扛和故意引爆矛盾的动机串起来时进入最终阶段。', 2, 'OLD_EVENT', '压迫收紧', JSON_OBJECT()),
    (31204, 3101, 'summer-stage-4', '晚自习后的名字', 4,
     '完成最终判断，指出谁故意推动了这场矛盾，并解释她真正想逼出的真相。',
     '现在已经没有人能退回晚自习开始以前。真正要落下来的，不是谁喜欢谁的答案，而是谁把今晚推到了这里。',
     '最终阶段，等待玩家完成指认。', 99, 'ACCUSATION', '直面真相', JSON_OBJECT());

INSERT INTO am_script_stage_focus_character (
    id, script_stage_id, script_character_id, sort_no
) VALUES
    (31501, 31201, 31103, 1),
    (31502, 31201, 31104, 2),
    (31503, 31201, 31105, 3),
    (31504, 31201, 31107, 4),
    (31505, 31202, 31104, 1),
    (31506, 31202, 31105, 2),
    (31507, 31202, 31106, 3),
    (31508, 31202, 31107, 4),
    (31509, 31203, 31103, 1),
    (31510, 31203, 31104, 2),
    (31511, 31203, 31105, 3),
    (31512, 31203, 31107, 4),
    (31513, 31204, 31104, 1),
    (31514, 31204, 31103, 2),
    (31515, 31204, 31107, 3);

INSERT INTO am_script_stage_keyword (
    id, script_stage_id, keyword_text, keyword_type, trigger_weight, sort_no
) VALUES
    (31601, 31201, '桌洞', 'ADVANCE', 1, 1),
    (31602, 31201, '纸条', 'ADVANCE', 2, 2),
    (31603, 31201, '反光', 'ADVANCE', 1, 3),
    (31604, 31201, '语音', 'ADVANCE', 1, 4),
    (31605, 31201, '走廊', 'ADVANCE', 1, 5),
    (31606, 31202, '广播室', 'ADVANCE', 2, 1),
    (31607, 31202, '剪辑', 'ADVANCE', 2, 2),
    (31608, 31202, '照片', 'ADVANCE', 1, 3),
    (31609, 31202, '值日', 'ADVANCE', 1, 4),
    (31610, 31202, '原始语音', 'ADVANCE', 2, 5),
    (31611, 31203, '违纪', 'ADVANCE', 2, 1),
    (31612, 31203, '替谁扛', 'ADVANCE', 2, 2),
    (31613, 31203, '没送出的信', 'ADVANCE', 1, 3),
    (31614, 31203, '林澄', 'ADVANCE', 2, 4),
    (31615, 31203, '为什么故意', 'ADVANCE', 2, 5),
    (31616, 31204, '最终指认', 'ADVANCE', 2, 1),
    (31617, 31204, '故意引爆', 'ADVANCE', 2, 2),
    (31618, 31204, '幕后的人', 'ADVANCE', 2, 3);

INSERT INTO am_script_clue (
    id, script_version_id, clue_code, clue_name, clue_type, content_text, effect_text,
    first_unlock_stage_code, reveal_mode_code, evidence_chain_code, is_key_clue, is_reusable, ext_json
) VALUES
    (31301, 3101, 'summer-clue-recorder-shell', '银色录音笔外壳', 'PHYSICAL',
     '许知夏桌洞深处藏着一个银色录音笔外壳，边缘有被仓促拆开的划痕，像有人只拿走了里面真正重要的部分。',
     '提示今晚的信息传播并不自然，有人提前处理过音频相关物件。', 'summer-stage-1', 'AUTO', 'SUMMER_CHAIN_A', 0, 1, JSON_OBJECT()),
    (31302, 3101, 'summer-clue-anonymous-note', '匿名纸条', 'DOCUMENT',
     '纸条上只有一句话：你以为他真的喜欢你吗。字迹故意压得很平，像怕别人一眼认出来。',
     '先把所有人的注意力引向感情误会，为真正的矛盾打掩护。', 'summer-stage-1', 'AUTO', 'SUMMER_CHAIN_A', 0, 1, JSON_OBJECT()),
    (31303, 3101, 'summer-clue-corridor-argument', '走廊争执', 'TESTIMONY',
     '晚自习前有人听见叶真和周屿在走廊压低声音争执，叶真说过一句：你别再替我装没事。',
     '说明两人争执的核心不是暧昧，而是持续了一段时间的隐瞒。', 'summer-stage-1', 'AUTO', 'SUMMER_CHAIN_A', 1, 1, JSON_OBJECT()),
    (31304, 3101, 'summer-clue-ripped-photo', '被撕开的合照', 'DOCUMENT',
     '后排储物柜里藏着半张旧合照，残留部分能看见周屿、叶真和宋晚都在，另一半像是被专门撕走了。',
     '说明有人在主动切断能还原旧时间线的物证。', 'summer-stage-2', 'AUTO', 'SUMMER_CHAIN_B', 0, 1, JSON_OBJECT()),
    (31305, 3101, 'summer-clue-audio-fragment', '被剪过的语音片段', 'DOCUMENT',
     '流出的语音里只剩一句：你就是仗着她不会说。结尾处的环境底噪断得很生硬，明显不是完整原件。',
     '把矛盾从感情误会引向谁在利用沉默。', 'summer-stage-2', 'AUTO', 'SUMMER_CHAIN_B', 1, 1, JSON_OBJECT()),
    (31306, 3101, 'summer-clue-duty-log', '值日表涂改痕迹', 'DOCUMENT',
     '值日表右下角有一次被擦掉又重写的记录，那天本该留下来锁广播室的人并不是后来登记的那个名字。',
     '说明广播室和语音传播链路里有人替换过行动顺序。', 'summer-stage-2', 'AUTO', 'SUMMER_CHAIN_B', 0, 1, JSON_OBJECT()),
    (31307, 3101, 'summer-clue-unsent-letter', '没送出的信', 'DOCUMENT',
     '天台门口夹层里藏着一封没送出的信，开头写着：如果你再替我扛一次，我以后连看你都不敢。',
     '直接把矛头指向代扛旧事，而不是谁喜欢谁。', 'summer-stage-3', 'AUTO', 'SUMMER_CHAIN_C', 1, 1, JSON_OBJECT()),
    (31308, 3101, 'summer-clue-discipline-copy', '旧违纪单复印件', 'DOCUMENT',
     '违纪单复印件上的名字是周屿，但时间、地点和旁边的备注都更贴近叶真那晚的行动轨迹。',
     '说明违纪责任存在顶替。', 'summer-stage-3', 'AUTO', 'SUMMER_CHAIN_C', 1, 1, JSON_OBJECT()),
    (31309, 3101, 'summer-clue-broadcast-trash', '广播室回收站记录', 'DOCUMENT',
     '广播室电脑回收站里有一段被删除的音频缓存，创建时间恰好在晚自习前十分钟。',
     '坐实语音是被人为处理后投放。', 'summer-stage-3', 'AUTO', 'SUMMER_CHAIN_C', 0, 1, JSON_OBJECT()),
    (31310, 3101, 'summer-clue-teacher-note', '班长记录本备注页', 'DOCUMENT',
     '林澄的记录本边页写着一行很轻的字：再拖下去，所有人都会一起坏掉。',
     '提示林澄的动机更接近逼真相浮出，而不是单纯伤害谁。', 'summer-stage-4', 'AUTO', 'SUMMER_CHAIN_D', 0, 1, JSON_OBJECT()),
    (31311, 3101, 'summer-clue-timeline-sketch', '课间时间线草图', 'PHYSICAL',
     '宋晚留下的一张草图把晚自习前后几个人的动线全画了出来，广播室、走廊和储物柜在同一时间段被反复圈出。',
     '让玩家能把零散细节拼成完整行动链。', 'summer-stage-4', 'AUTO', 'SUMMER_CHAIN_D', 0, 1, JSON_OBJECT()),
    (31312, 3101, 'summer-clue-full-audio', '原始完整语音', 'DOCUMENT',
     '完整语音里的原话是：你就是仗着她不会说，才一直替她把那件事压下去。说话的人是林澄，她的语气更像逼问，不像挑拨。',
     '最终指向林澄才是故意引爆矛盾的人，但她要逼出的并不是八卦，而是真相。', 'summer-stage-4', 'AUTO', 'SUMMER_CHAIN_D', 1, 1, JSON_OBJECT());

INSERT INTO am_script_stage_clue_rel (
    id, script_stage_id, script_clue_id, reveal_order, reveal_mode_code, is_default_available
) VALUES
    (31701, 31201, 31301, 1, 'AUTO', 1),
    (31702, 31201, 31302, 2, 'AUTO', 1),
    (31703, 31201, 31303, 3, 'AUTO', 1),
    (31704, 31202, 31301, 1, 'AUTO', 1),
    (31705, 31202, 31302, 2, 'AUTO', 1),
    (31706, 31202, 31303, 3, 'AUTO', 1),
    (31707, 31202, 31304, 4, 'AUTO', 1),
    (31708, 31202, 31305, 5, 'AUTO', 1),
    (31709, 31202, 31306, 6, 'AUTO', 1),
    (31710, 31203, 31301, 1, 'AUTO', 1),
    (31711, 31203, 31302, 2, 'AUTO', 1),
    (31712, 31203, 31303, 3, 'AUTO', 1),
    (31713, 31203, 31304, 4, 'AUTO', 1),
    (31714, 31203, 31305, 5, 'AUTO', 1),
    (31715, 31203, 31306, 6, 'AUTO', 1),
    (31716, 31203, 31307, 7, 'AUTO', 1),
    (31717, 31203, 31308, 8, 'AUTO', 1),
    (31718, 31203, 31309, 9, 'AUTO', 1),
    (31719, 31204, 31301, 1, 'AUTO', 1),
    (31720, 31204, 31302, 2, 'AUTO', 1),
    (31721, 31204, 31303, 3, 'AUTO', 1),
    (31722, 31204, 31304, 4, 'AUTO', 1),
    (31723, 31204, 31305, 5, 'AUTO', 1),
    (31724, 31204, 31306, 6, 'AUTO', 1),
    (31725, 31204, 31307, 7, 'AUTO', 1),
    (31726, 31204, 31308, 8, 'AUTO', 1),
    (31727, 31204, 31309, 9, 'AUTO', 1),
    (31728, 31204, 31310, 10, 'AUTO', 1),
    (31729, 31204, 31311, 11, 'AUTO', 1),
    (31730, 31204, 31312, 12, 'AUTO', 1);

INSERT INTO am_script_clue_character_rel (
    id, script_clue_id, script_character_id, relation_type, weight_score
) VALUES
    (31801, 31301, 31104, 'RELATED', 4),
    (31802, 31301, 31105, 'RELATED', 3),
    (31803, 31302, 31102, 'RELATED', 4),
    (31804, 31302, 31103, 'RELATED', 3),
    (31805, 31302, 31104, 'RELATED', 5),
    (31806, 31303, 31103, 'RELATED', 5),
    (31807, 31303, 31107, 'RELATED', 5),
    (31808, 31304, 31105, 'RELATED', 4),
    (31809, 31304, 31107, 'RELATED', 3),
    (31810, 31305, 31104, 'RELATED', 5),
    (31811, 31305, 31106, 'RELATED', 4),
    (31812, 31305, 31107, 'RELATED', 3),
    (31813, 31306, 31104, 'RELATED', 4),
    (31814, 31306, 31105, 'RELATED', 3),
    (31815, 31307, 31103, 'RELATED', 4),
    (31816, 31307, 31107, 'RELATED', 5),
    (31817, 31308, 31103, 'RELATED', 5),
    (31818, 31308, 31107, 'RELATED', 5),
    (31819, 31308, 31104, 'RELATED', 4),
    (31820, 31309, 31104, 'RELATED', 4),
    (31821, 31309, 31106, 'RELATED', 3),
    (31822, 31310, 31104, 'RELATED', 5),
    (31823, 31311, 31105, 'RELATED', 5),
    (31824, 31311, 31103, 'RELATED', 3),
    (31825, 31311, 31107, 'RELATED', 3),
    (31826, 31312, 31104, 'RELATED', 5),
    (31827, 31312, 31103, 'RELATED', 4),
    (31828, 31312, 31107, 'RELATED', 4);
