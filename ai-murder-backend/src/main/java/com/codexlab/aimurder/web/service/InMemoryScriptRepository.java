package com.codexlab.aimurder.web.service;

import com.codexlab.aimurder.domain.script.definition.CharacterDefinition;
import com.codexlab.aimurder.domain.script.definition.ClueDefinition;
import com.codexlab.aimurder.domain.script.definition.ScriptDefinition;
import com.codexlab.aimurder.domain.script.definition.StageDefinition;
import com.codexlab.aimurder.domain.script.enums.ClueType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的副本仓储。
 * 当前阶段用于托管内置的 MVP 副本。
 */
@Service
public class InMemoryScriptRepository implements ScriptRepository {

    /**
     * 默认副本标识。
     */
    public static final String DEFAULT_SCRIPT_ID = "rainy-night-blackout";

    private final Map<String, ScriptDefinition> scriptStore = new ConcurrentHashMap<>();

    /**
     * 初始化内置副本。
     */
    @PostConstruct
    public void init() {
        ScriptDefinition scriptDefinition = new ScriptDefinition();
        scriptDefinition.setScriptId(DEFAULT_SCRIPT_ID);
        scriptDefinition.setScriptName("雨夜断灯");
        scriptDefinition.setSummary("这是一场暴雨封庄后的停电凶案。玩家需要在四名在场者之中找出真凶，并还原停电前后的关键时间线。");
        scriptDefinition.setOpeningNarration("暴雨压着山庄檐角，整栋宅邸像一盏随时会熄灭的旧灯。就在停电后的数分钟内，书房里倒下了今晚唯一不能倒下的人。");
        scriptDefinition.setPlayerRoleName("受邀旁观调查者");
        scriptDefinition.setPlayerRoleDescription("你不是山庄内部旧人，而是今晚被临时留下协助梳理现场的人。你的身份相对中立，因此管家会默认由你来主持第一轮询问与判断。");
        scriptDefinition.setPlayerObjective("通过发问、比对口供、观察异常与追踪线索，锁定真正的凶手，并判断停电究竟是意外还是人为安排。");
        scriptDefinition.setOpeningInstruction("开场必须明确告诉玩家：当前副本名是什么、案发背景是什么、玩家扮演什么身份、为什么由玩家介入调查、接下来应如何推进调查。");
        scriptDefinition.setNarrationInstruction("旁白只用于环境切换、动作补充、气氛压强和短暂镜头描述。旁白必须使用【旁白】标签，长度要短，不能抢走角色对白，更不能替角色说出内心真相。");
        scriptDefinition.setTruthSummary("真凶是林乔。她提前对配电箱做了手脚，利用停电窗口进入书房刺杀沈砚之。");
        scriptDefinition.setCharacters(buildCharacters());
        scriptDefinition.setStages(buildStages());
        scriptDefinition.setClues(buildClues());
        scriptStore.put(scriptDefinition.getScriptId(), scriptDefinition);
    }

    @Override
    public ScriptDefinition findById(String scriptId) {
        return scriptStore.get(scriptId);
    }

    @Override
    public ScriptDefinition getDefaultScript() {
        return scriptStore.get(DEFAULT_SCRIPT_ID);
    }

    /**
     * 构建角色定义列表。
     *
     * @return 角色定义列表
     */
    private List<CharacterDefinition> buildCharacters() {
        CharacterDefinition butler = new CharacterDefinition();
        butler.setCharacterId("butler");
        butler.setCharacterName("管家");
        butler.setIdentity("山庄管家");
        butler.setRelationship("负责维护山庄秩序并主持今晚的临时调查");
        butler.setPublicPersona("沉稳、克制、礼貌，擅长引导和控场");
        butler.setKnownFacts(List.of("山庄已经封闭", "所有人都被困在宅邸中"));
        butler.setHiddenSecrets(List.of());
        butler.setForbiddenDisclosures(List.of("不能直接公布凶手", "不能跳过推理流程"));
        butler.setResponseStrategy("负责开场、转场、控场、发线索，但不直接宣布真相。");

        CharacterDefinition linQiao = new CharacterDefinition();
        linQiao.setCharacterId("lin-qiao");
        linQiao.setCharacterName("林乔");
        linQiao.setIdentity("账务顾问");
        linQiao.setRelationship("长期协助死者处理财务事务");
        linQiao.setPublicPersona("冷静、干练、擅长隐藏情绪");
        linQiao.setKnownFacts(List.of("知道账本里有对自己不利的内容", "知道停电不是纯意外"));
        linQiao.setHiddenSecrets(List.of("她提前动过配电箱", "她在停电窗口进入书房行凶"));
        linQiao.setForbiddenDisclosures(List.of("不能主动承认自己制造停电", "不能主动承认自己杀人"));
        linQiao.setResponseStrategy("前期稳定否认，中期转为防御，证据逼近后出现情绪波动。");
        linQiao.setKiller(true);

        CharacterDefinition guShen = new CharacterDefinition();
        guShen.setCharacterId("gu-shen");
        guShen.setCharacterName("顾深");
        guShen.setIdentity("律师");
        guShen.setRelationship("负责死者遗嘱与资产安排");
        guShen.setPublicPersona("谨慎、专业、说话有保留");
        guShen.setKnownFacts(List.of("知道死者近期打算修改遗嘱"));
        guShen.setHiddenSecrets(List.of("晚餐前曾与死者激烈争执"));
        guShen.setForbiddenDisclosures(List.of("不会主动交代争执细节"));
        guShen.setResponseStrategy("强调自己有争执但无杀意，更关注程序和证据。");

        CharacterDefinition zhouYan = new CharacterDefinition();
        zhouYan.setCharacterId("zhou-yan");
        zhouYan.setCharacterName("周衍");
        zhouYan.setIdentity("电路改造承包方代表");
        zhouYan.setRelationship("近期负责山庄线路检修");
        zhouYan.setPublicPersona("表面散漫，实则心虚");
        zhouYan.setKnownFacts(List.of("知道山庄线路确实存在历史问题"));
        zhouYan.setHiddenSecrets(List.of("部分检修工作没有彻底完成"));
        zhouYan.setForbiddenDisclosures(List.of("不会主动提自己施工敷衍"));
        zhouYan.setResponseStrategy("容易闪躲停电细节，但并不知道真凶是谁。");

        CharacterDefinition luChen = new CharacterDefinition();
        luChen.setCharacterId("lu-chen");
        luChen.setCharacterName("陆沉");
        luChen.setIdentity("死者侄子");
        luChen.setRelationship("与死者关系长期紧张");
        luChen.setPublicPersona("冷淡、压抑、带有明显怨气");
        luChen.setKnownFacts(List.of("案发前在书房附近徘徊"));
        luChen.setHiddenSecrets(List.of("曾想确认自己是否会失去继承资格"));
        luChen.setForbiddenDisclosures(List.of("不会主动承认翻过书房抽屉"));
        luChen.setResponseStrategy("态度冷，不爱解释，但会对不公平指控反弹。");

        return List.of(butler, linQiao, guShen, zhouYan, luChen);
    }

    /**
     * 构建阶段定义列表。
     *
     * @return 阶段定义列表
     */
    private List<StageDefinition> buildStages() {
        StageDefinition stageOne = new StageDefinition();
        stageOne.setStageId("stage-1");
        stageOne.setStageName("暴雨封庄");
        stageOne.setStageOrder(1);
        stageOne.setObjective("建立案发氛围，确认四名角色在停电前后的行动。");
        stageOne.setOpeningNarration("停电之后，每个人都开始回忆自己在黑暗中的位置。可越是回忆，破绽越像雨水一样顺着墙缝渗出来。");
        stageOne.setAvailableClueIds(List.of("clue-argue", "clue-corridor"));
        stageOne.setFocusCharacterIds(List.of("lin-qiao", "gu-shen", "zhou-yan", "lu-chen"));
        stageOne.setNextStageCondition("当玩家开始追问停电是否人为，或已形成初步嫌疑链时进入下一阶段。");

        StageDefinition stageTwo = new StageDefinition();
        stageTwo.setStageId("stage-2");
        stageTwo.setStageName("停电不是意外");
        stageTwo.setStageOrder(2);
        stageTwo.setObjective("让玩家意识到停电带有人为痕迹，并锁定更可疑对象。");
        stageTwo.setOpeningNarration("真正可疑的从来不只是人与人之间的矛盾，而是谁有能力把混乱精确地安排在那几分钟里。");
        stageTwo.setAvailableClueIds(List.of("clue-argue", "clue-corridor", "clue-power-box", "clue-ledger"));
        stageTwo.setFocusCharacterIds(List.of("lin-qiao", "zhou-yan"));
        stageTwo.setNextStageCondition("当玩家将停电与账本联系起来，或多次聚焦林乔时进入下一阶段。");

        StageDefinition stageThree = new StageDefinition();
        stageThree.setStageId("stage-3");
        stageThree.setStageName("死者留下了指认");
        stageThree.setStageOrder(3);
        stageThree.setObjective("投放决定性证据，推动玩家完成最终指认。");
        stageThree.setOpeningNarration("死者并非毫无反抗。他在最后一刻留下的，不是完整答案，而是一道足够锋利的指向。");
        stageThree.setAvailableClueIds(List.of("clue-argue", "clue-corridor", "clue-power-box", "clue-ledger", "clue-note"));
        stageThree.setFocusCharacterIds(List.of("lin-qiao"));
        stageThree.setNextStageCondition("玩家完成最终指认后结束。");

        return List.of(stageOne, stageTwo, stageThree);
    }

    /**
     * 构建线索定义列表。
     *
     * @return 线索定义列表
     */
    private List<ClueDefinition> buildClues() {
        ClueDefinition clueArgue = new ClueDefinition();
        clueArgue.setClueId("clue-argue");
        clueArgue.setClueName("晚餐争执");
        clueArgue.setClueType(ClueType.TESTIMONY);
        clueArgue.setContent("晚餐前，顾深曾与死者在走廊尽头发生过一次短促但明显的争执。");
        clueArgue.setEffect("制造顾深的嫌疑。");
        clueArgue.setUnlockStageId("stage-1");
        clueArgue.setRelatedCharacterIds(List.of("gu-shen"));

        ClueDefinition clueCorridor = new ClueDefinition();
        clueCorridor.setClueId("clue-corridor");
        clueCorridor.setClueName("书房外走廊脚步");
        clueCorridor.setClueType(ClueType.TESTIMONY);
        clueCorridor.setContent("案发前后，有人看到陆沉曾在书房外走廊短暂停留。");
        clueCorridor.setEffect("制造陆沉的嫌疑。");
        clueCorridor.setUnlockStageId("stage-1");
        clueCorridor.setRelatedCharacterIds(List.of("lu-chen"));

        ClueDefinition cluePowerBox = new ClueDefinition();
        cluePowerBox.setClueId("clue-power-box");
        cluePowerBox.setClueName("配电箱异常");
        cluePowerBox.setClueType(ClueType.ENVIRONMENT);
        cluePowerBox.setContent("配电箱的负载开关存在被人提前调整过的痕迹，这次停电并不像自然跳闸。");
        cluePowerBox.setEffect("说明停电有人为嫌疑。");
        cluePowerBox.setUnlockStageId("stage-2");
        cluePowerBox.setRelatedCharacterIds(List.of("lin-qiao", "zhou-yan"));
        cluePowerBox.setKeyClue(true);

        ClueDefinition clueLedger = new ClueDefinition();
        clueLedger.setClueId("clue-ledger");
        clueLedger.setClueName("账本残页");
        clueLedger.setClueType(ClueType.DOCUMENT);
        clueLedger.setContent("书房账本中有一页被撕走，撕口纤维与林乔衣袖残留的细丝高度一致。");
        clueLedger.setEffect("将林乔与关键账本联系起来。");
        clueLedger.setUnlockStageId("stage-2");
        clueLedger.setRelatedCharacterIds(List.of("lin-qiao"));
        clueLedger.setKeyClue(true);

        ClueDefinition clueNote = new ClueDefinition();
        clueNote.setClueId("clue-note");
        clueNote.setClueName("残缺字迹");
        clueNote.setClueType(ClueType.DOCUMENT);
        clueNote.setContent("死者便签背面留下一个未写完的字，形态接近“乔”字的左侧结构。");
        clueNote.setEffect("形成对林乔的临终指认。");
        clueNote.setUnlockStageId("stage-3");
        clueNote.setRelatedCharacterIds(List.of("lin-qiao"));
        clueNote.setKeyClue(true);

        return List.of(clueArgue, clueCorridor, cluePowerBox, clueLedger, clueNote);
    }
}
