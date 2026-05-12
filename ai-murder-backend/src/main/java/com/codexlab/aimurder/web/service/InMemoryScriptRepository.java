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

@Service
public class InMemoryScriptRepository implements ScriptRepository {

    public static final String DEFAULT_SCRIPT_ID = "rainy-night-blackout";
    public static final String SECOND_SCRIPT_ID = "fog-harbor-letter";

    private final Map<String, ScriptDefinition> scriptStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        ScriptDefinition rainyNight = buildRainyNightBlackout();
        ScriptDefinition fogHarbor = buildFogHarborLetter();
        scriptStore.put(rainyNight.getScriptId(), rainyNight);
        scriptStore.put(fogHarbor.getScriptId(), fogHarbor);
    }

    @Override
    public ScriptDefinition findById(String scriptId) {
        return scriptStore.get(scriptId);
    }

    @Override
    public ScriptDefinition getDefaultScript() {
        return scriptStore.get(DEFAULT_SCRIPT_ID);
    }

    @Override
    public List<ScriptDefinition> findAll() {
        return scriptStore.values().stream()
                .sorted((left, right) -> Integer.compare(left.getUnlockOrder(), right.getUnlockOrder()))
                .toList();
    }

    private ScriptDefinition buildRainyNightBlackout() {
        ScriptDefinition scriptDefinition = new ScriptDefinition();
        scriptDefinition.setScriptId(DEFAULT_SCRIPT_ID);
        scriptDefinition.setScriptName("雨夜断灯");
        scriptDefinition.setSummary("暴雨封庄后的山庄里突发停电，书房中的主人在黑暗里被杀。你将被随机分到在场嫌疑人之一，在失真口供和逐步浮出的线索中逼近真凶。");
        scriptDefinition.setOpeningNarration("暴雨压着山庄檐角，整栋宅邸像一盏随时会熄灭的旧灯。停电后的短短几分钟里，书房里倒下了今晚最不该倒下的人。");
        scriptDefinition.setPlayerModeName("随机身份入局");
        scriptDefinition.setPlayerModeDescription("玩家会在开局被随机分配为现场嫌疑人之一，以局中人的视角参与盘问、辩解和误导。");
        scriptDefinition.setUnlockOrder(1);
        scriptDefinition.setRandomRoleOnStart(true);
        scriptDefinition.setHostCharacterId("butler");
        scriptDefinition.setOpeningInstruction("先说明暴雨封庄、停电行凶、玩家身份和第一轮调查目标，再让其他角色依次给出第一反应。");
        scriptDefinition.setNarrationInstruction("旁白只负责环境、动作和气氛，不直接交代真相。");
        scriptDefinition.setTruthSummary("真凶是林乔。她提前动过配电箱，利用停电窗口进入书房杀人，并试图把怀疑转移到其他人身上。");
        scriptDefinition.setEndingTitle("灯灭后的名字");
        scriptDefinition.setEndingStory("停电并不是意外，而是林乔提前做好的缺口。她知道账本残页迟早会牵出自己，于是在混乱里先一步灭口。配电箱、账本残页和死者临终留下的指向，最终把她从看似最冷静的位置上拽了下来。");
        scriptDefinition.setMinimumKeyCluesForAccusation(2);
        scriptDefinition.setCharacters(buildRainyNightCharacters());
        scriptDefinition.setStages(buildRainyNightStages());
        scriptDefinition.setClues(buildRainyNightClues());
        return scriptDefinition;
    }

    private List<CharacterDefinition> buildRainyNightCharacters() {
        CharacterDefinition butler = new CharacterDefinition();
        butler.setCharacterId("butler");
        butler.setCharacterName("管家");
        butler.setIdentity("山庄管家");
        butler.setRelationship("负责今晚封庄后的秩序维持");
        butler.setPublicPersona("沉稳、克制、擅长控场");
        butler.setPublicBackstory("他熟悉山庄里每一处走廊和每个人的脾气，也最清楚今晚一旦失控会发生什么。");
        butler.setResponseStrategy("负责控场、追问和转场，不直接公布答案。");

        CharacterDefinition linQiao = new CharacterDefinition();
        linQiao.setCharacterId("lin-qiao");
        linQiao.setCharacterName("林乔");
        linQiao.setIdentity("财务顾问");
        linQiao.setRelationship("长期协助死者处理账目和外部资金安排");
        linQiao.setPublicPersona("冷静、干练、擅长先听再说");
        linQiao.setPublicBackstory("她是死者最信任的账目处理人之一，对书房里的纸面秘密非常熟悉。");
        linQiao.setPrivateBackstory("她知道那本账册里有一页足以把自己拖进深坑。停电前，她已经决定先一步切断所有会指向自己的证据链。");
        linQiao.setKnownFacts(List.of("知道账本有对自己不利的内容", "知道这场停电不像自然故障"));
        linQiao.setHiddenSecrets(List.of("提前动过配电箱", "趁黑进入书房行凶"));
        linQiao.setForbiddenDisclosures(List.of("不能主动承认操纵停电", "不能主动承认自己行凶"));
        linQiao.setPublicObjective("先稳住口供，不让别人把注意力锁定在账本和停电之间。");
        linQiao.setPrivateObjective("把真正指向自己的线索推向别人的动机链。");
        linQiao.setOpeningTip("你要表现得像最能镇住场面的人，绝不能抢着辩白。");
        linQiao.setResponseStrategy("前期冷处理，中期开始反压追问，证据逼近时情绪会出现裂口。");
        linQiao.setSelectableByPlayer(true);
        linQiao.setKiller(true);

        CharacterDefinition guShen = new CharacterDefinition();
        guShen.setCharacterId("gu-shen");
        guShen.setCharacterName("顾深");
        guShen.setIdentity("律师");
        guShen.setRelationship("负责死者近期遗嘱与资产重整");
        guShen.setPublicPersona("谨慎、职业化、说话保留余地");
        guShen.setPublicBackstory("他近来频繁出入山庄，为死者处理一份让很多人心神不宁的遗嘱修订。");
        guShen.setPrivateBackstory("晚餐前他和死者确实发生了激烈争执，但争执的焦点并非谋杀，而是某份尚未签字的财产安排。");
        guShen.setKnownFacts(List.of("知道死者近期打算修改遗嘱"));
        guShen.setHiddenSecrets(List.of("晚餐前和死者激烈争执"));
        guShen.setForbiddenDisclosures(List.of("不会主动透露遗嘱的具体去向"));
        guShen.setPublicObjective("承认争执存在，但别让局面把争执直接等同于杀意。");
        guShen.setPrivateObjective("守住遗嘱内容，不让任何人借此看穿你今晚最在意的风险。");
        guShen.setOpeningTip("你可以显得不耐烦，但不能失去律师该有的控制感。");
        guShen.setResponseStrategy("强调程序和证据，不轻易接任何带情绪的指控。");
        guShen.setSelectableByPlayer(true);

        CharacterDefinition zhouYan = new CharacterDefinition();
        zhouYan.setCharacterId("zhou-yan");
        zhouYan.setCharacterName("周衍");
        zhouYan.setIdentity("线路承包代表");
        zhouYan.setRelationship("近期负责山庄电路检修");
        zhouYan.setPublicPersona("表面松散，实则怕被追到细节");
        zhouYan.setPublicBackstory("这座山庄的旧线路一直有毛病，而他正好是那个最容易被怀疑和电路有关的人。");
        zhouYan.setPrivateBackstory("他确实在检修记录上留了偷工减料的口子，但那只会让他惹上麻烦，不足以解释今晚这场精准的停电。");
        zhouYan.setKnownFacts(List.of("知道山庄线路确有历史问题"));
        zhouYan.setHiddenSecrets(List.of("部分检修工作没有彻底做完"));
        zhouYan.setForbiddenDisclosures(List.of("不会主动交代自己施工上的缺口"));
        zhouYan.setPublicObjective("把停电解释成老线路风险，别让别人把它看成有预谋的布局。");
        zhouYan.setPrivateObjective("护住自己的失职事实，别让自己变成最方便的替罪羊。");
        zhouYan.setOpeningTip("你最怕的是别人追到时间点和检修细节。");
        zhouYan.setResponseStrategy("遇到技术细节容易闪躲，但并不知道真正的凶手是谁。");
        zhouYan.setSelectableByPlayer(true);

        CharacterDefinition luChen = new CharacterDefinition();
        luChen.setCharacterId("lu-chen");
        luChen.setCharacterName("陆沉");
        luChen.setIdentity("死者侄子");
        luChen.setRelationship("与死者的关系长期紧张");
        luChen.setPublicPersona("冷淡、压抑、像随时会翻旧账");
        luChen.setPublicBackstory("他与死者的矛盾从来不是秘密，很多人都知道他对这位长辈有过真正的怨气。");
        luChen.setPrivateBackstory("案发前他确实在书房外徘徊过，因为他想确认自己会不会被踢出继承链。");
        luChen.setKnownFacts(List.of("案发前曾在书房外停留"));
        luChen.setHiddenSecrets(List.of("翻看过书房附近的抽屉和文件"));
        luChen.setForbiddenDisclosures(List.of("不会主动承认自己动过书房附近的东西"));
        luChen.setPublicObjective("别让所有人把怨气和杀人简单画等号。");
        luChen.setPrivateObjective("守住自己翻找文件的事实，避免被定义成冲动行凶。");
        luChen.setOpeningTip("你可以冷，但别冷到像已经认输。");
        luChen.setResponseStrategy("态度冷硬，不爱解释，但会对不公平指控产生明显反弹。");
        luChen.setSelectableByPlayer(true);

        return List.of(butler, linQiao, guShen, zhouYan, luChen);
    }

    private List<StageDefinition> buildRainyNightStages() {
        StageDefinition stageOne = new StageDefinition();
        stageOne.setStageId("rain-stage-1");
        stageOne.setStageName("暴雨封庄");
        stageOne.setStageOrder(1);
        stageOne.setObjective("先稳住局面，确认停电前后每个人的大致位置和第一轮口供。");
        stageOne.setOpeningNarration("停电后的几分钟被每个人说成了不同的样子。越是试图回忆，破绽越像雨水一样沿着墙缝渗出来。");
        stageOne.setAvailableClueIds(List.of("clue-argue", "clue-corridor"));
        stageOne.setFocusCharacterIds(List.of("lin-qiao", "gu-shen", "zhou-yan", "lu-chen"));
        stageOne.setAdvanceKeywords(List.of("停电", "配电箱", "人为", "谁动了电", "时间线"));
        stageOne.setMinimumTurnsBeforeAdvance(2);
        stageOne.setNextStageCondition("当玩家开始把停电和具体人物联系起来时进入下一阶段。");

        StageDefinition stageTwo = new StageDefinition();
        stageTwo.setStageId("rain-stage-2");
        stageTwo.setStageName("黑暗不是意外");
        stageTwo.setStageOrder(2);
        stageTwo.setObjective("把调查重点从动机推到手法，逼出谁最了解这场停电。");
        stageTwo.setOpeningNarration("真正危险的不是谁有怨，而是谁能把混乱精确地安排进那几分钟黑暗里。");
        stageTwo.setAvailableClueIds(List.of("clue-argue", "clue-corridor", "clue-power-box", "clue-ledger"));
        stageTwo.setFocusCharacterIds(List.of("lin-qiao", "zhou-yan"));
        stageTwo.setAdvanceKeywords(List.of("账本", "残页", "字迹", "便签", "遗嘱", "林乔"));
        stageTwo.setMinimumTurnsBeforeAdvance(2);
        stageTwo.setNextStageCondition("当玩家把停电、账本和林乔联系起来时进入下一阶段。");

        StageDefinition stageThree = new StageDefinition();
        stageThree.setStageId("rain-stage-3");
        stageThree.setStageName("死者留下了指向");
        stageThree.setStageOrder(3);
        stageThree.setObjective("拼起决定性证据，完成最终指认。");
        stageThree.setOpeningNarration("死者并没有完全来不及反应。他最后留下的不是答案，而是一道足够锋利的指向。");
        stageThree.setAvailableClueIds(List.of("clue-argue", "clue-corridor", "clue-power-box", "clue-ledger", "clue-note"));
        stageThree.setFocusCharacterIds(List.of("lin-qiao"));
        stageThree.setAdvanceKeywords(List.of("最终指认", "真凶", "就是你"));
        stageThree.setMinimumTurnsBeforeAdvance(99);
        stageThree.setNextStageCondition("玩家完成最终指认后结束。");

        return List.of(stageOne, stageTwo, stageThree);
    }

    private List<ClueDefinition> buildRainyNightClues() {
        ClueDefinition clueArgue = clue(
                "clue-argue", "晚餐争执", ClueType.TESTIMONY,
                "晚餐前，顾深曾与死者在走廊尽头发生过一次短促但明显的争执。",
                "制造顾深的嫌疑。", "rain-stage-1", List.of("gu-shen"), false
        );

        ClueDefinition clueCorridor = clue(
                "clue-corridor", "书房外脚步", ClueType.TESTIMONY,
                "案发前后，有人看见陆沉曾在书房外短暂停留。",
                "制造陆沉的嫌疑。", "rain-stage-1", List.of("lu-chen"), false
        );

        ClueDefinition cluePowerBox = clue(
                "clue-power-box", "配电箱异常", ClueType.ENVIRONMENT,
                "配电箱的负载开关存在被人提前调整过的痕迹，这次停电不像自然跳闸。",
                "说明停电具有人为痕迹。", "rain-stage-2", List.of("lin-qiao", "zhou-yan"), true
        );

        ClueDefinition clueLedger = clue(
                "clue-ledger", "账本残页", ClueType.DOCUMENT,
                "书房账本中有一页被撕走，撕口纤维与林乔衣袖残留的细丝高度一致。",
                "把林乔与关键账本直接联系起来。", "rain-stage-2", List.of("lin-qiao"), true
        );

        ClueDefinition clueNote = clue(
                "clue-note", "临终字迹", ClueType.DOCUMENT,
                "死者便签背面留下一个未写完的字，形态接近“乔”字左侧的结构。",
                "形成对林乔的最终指向。", "rain-stage-3", List.of("lin-qiao"), true
        );

        return List.of(clueArgue, clueCorridor, cluePowerBox, clueLedger, clueNote);
    }

    private ScriptDefinition buildFogHarborLetter() {
        ScriptDefinition scriptDefinition = new ScriptDefinition();
        scriptDefinition.setScriptId(SECOND_SCRIPT_ID);
        scriptDefinition.setScriptName("雾港来信");
        scriptDefinition.setSummary("封港夜里，旧旅馆收到一封迟到二十年的信。馆主正准备当众拆信时突然身亡，旧案和新案被迫同时翻开。");
        scriptDefinition.setOpeningNarration("港口的雾比雨更让人不安。那封本该在二十年前送达的信，偏偏在今晚抵达旧旅馆，而馆主在众人目光落到信封之前先一步倒了下去。");
        scriptDefinition.setPlayerModeName("解锁副本 / 随机身份");
        scriptDefinition.setPlayerModeDescription("通关第一部后解锁。开局随机分配相关者身份，玩家带着不同的旧案知情范围进入当晚局面。");
        scriptDefinition.setUnlockOrder(2);
        scriptDefinition.setRandomRoleOnStart(true);
        scriptDefinition.setHostCharacterId("innkeeper");
        scriptDefinition.setOpeningInstruction("先交代封港、迟到来信、馆主死亡和旧案翻涌，再公布玩家身份与隐秘目标。");
        scriptDefinition.setNarrationInstruction("旁白负责海雾、旅馆空间、信件和众人反应，不直接替任何角色交代内心。");
        scriptDefinition.setTruthSummary("真凶是沈迟。二十年前的沉船事故并非天灾，而是她父亲与馆主共同掩盖的走私案。她在今晚收到来信预告后，抢在信被公开前下手灭口。");
        scriptDefinition.setEndingTitle("雾散时的收件人");
        scriptDefinition.setEndingStory("那封信之所以迟到二十年，是因为它原本就不该存在。信里记录着沉船夜真正的货单和港务登记编号，而沈迟知道，只要馆主当众拆开，自己家与旧案的牵连就再也藏不住。她选择在雾最重、众人注意力都被信封吸走之前，先让唯一能完整说出往事的人闭嘴。");
        scriptDefinition.setMinimumKeyCluesForAccusation(2);
        scriptDefinition.setCharacters(buildFogHarborCharacters());
        scriptDefinition.setStages(buildFogHarborStages());
        scriptDefinition.setClues(buildFogHarborClues());
        return scriptDefinition;
    }

    private List<CharacterDefinition> buildFogHarborCharacters() {
        CharacterDefinition innkeeper = new CharacterDefinition();
        innkeeper.setCharacterId("innkeeper");
        innkeeper.setCharacterName("许掌柜");
        innkeeper.setIdentity("旧旅馆掌柜");
        innkeeper.setRelationship("封港夜里唯一还能勉强维持秩序的人");
        innkeeper.setPublicPersona("见惯风浪，话少但能镇场");
        innkeeper.setPublicBackstory("这家旧旅馆在港口已经撑了很多年，掌柜知道太多人的往来，也知道什么时候该把话收住。");
        innkeeper.setResponseStrategy("负责控场和催问，不替人作答。");

        CharacterDefinition shenChi = new CharacterDefinition();
        shenChi.setCharacterId("shen-chi");
        shenChi.setCharacterName("沈迟");
        shenChi.setIdentity("港务署档案员");
        shenChi.setRelationship("馆主多年来替她家遮掩过一段旧账");
        shenChi.setPublicPersona("清醒、冷白、很会把情绪压成礼貌");
        shenChi.setPublicBackstory("她是港务署里最熟悉旧档的人之一，因此也最容易成为那封来信的天然解释者。");
        shenChi.setPrivateBackstory("她早已知道二十年前沉船夜不是意外，而是走私翻船。她父亲和馆主都参与过掩盖，而今夜那封信会把一切再拉回桌面。");
        shenChi.setKnownFacts(List.of("熟悉旧港务档案", "知道沉船案档案有缺页"));
        shenChi.setHiddenSecrets(List.of("提前看过寄信人身份", "在众人拆信前下手灭口"));
        shenChi.setForbiddenDisclosures(List.of("不能主动承认自己提前接触过来信", "不能主动承认和旧案掩盖有关"));
        shenChi.setPublicObjective("以专业身份参与梳理来信和档案，别让别人看出你比所有人都更怕信被拆开。");
        shenChi.setPrivateObjective("把调查引向别人的旧怨和财务动机，拖走对来信源头的追问。");
        shenChi.setOpeningTip("你最强的武器是冷静和专业感，不是抢先辩解。");
        shenChi.setResponseStrategy("前期以档案员姿态引导视线，中后期在旧案细节上会变得异常敏感。");
        shenChi.setSelectableByPlayer(true);
        shenChi.setKiller(true);

        CharacterDefinition yuLan = new CharacterDefinition();
        yuLan.setCharacterId("yu-lan");
        yuLan.setCharacterName("余岚");
        yuLan.setIdentity("调查记者");
        yuLan.setRelationship("追查沉船旧闻多年，今晚以普通住客身份潜入");
        yuLan.setPublicPersona("敏锐、克制、总在等别人先说漏一句");
        yuLan.setPublicBackstory("她长期追踪港口旧案，知道死者和很多旧名字之间并不干净。");
        yuLan.setPrivateBackstory("她收到过匿名线索才赶来旅馆，但她不能暴露自己的消息源，否则整条线会立刻断掉。");
        yuLan.setKnownFacts(List.of("二十年前沉船事故疑点重重"));
        yuLan.setHiddenSecrets(List.of("以普通住客身份混入旅馆", "手里还有未公开的采访录音"));
        yuLan.setForbiddenDisclosures(List.of("不能主动暴露匿名线索来源"));
        yuLan.setPublicObjective("把今晚的局面推到能说真话的位置。");
        yuLan.setPrivateObjective("保护线索来源，同时确认那封信到底指向谁。");
        yuLan.setOpeningTip("你说话可以像在采访，但别让人觉得你早有准备。");
        yuLan.setResponseStrategy("擅长追问和归纳别人口供，但不会轻易摊出全部底牌。");
        yuLan.setSelectableByPlayer(true);

        CharacterDefinition heMu = new CharacterDefinition();
        heMu.setCharacterId("he-mu");
        heMu.setCharacterName("何沐");
        heMu.setIdentity("馆主养女");
        heMu.setRelationship("从小在旅馆长大，与馆主关系最复杂");
        heMu.setPublicPersona("柔和、安静、总像在替别人留情面");
        heMu.setPublicBackstory("她几乎把旅馆当成自己唯一的家，也最清楚馆主这些年为什么一直不肯离开港口。");
        heMu.setPrivateBackstory("她怀疑自己的亲生父母与沉船夜有关，但一直没敢真正拆开那层关系。");
        heMu.setKnownFacts(List.of("馆主每年都会在沉船夜前后情绪异常"));
        heMu.setHiddenSecrets(List.of("偷偷见过寄信人留下的旧照片"));
        heMu.setForbiddenDisclosures(List.of("不会主动承认自己翻看过馆主旧柜子"));
        heMu.setPublicObjective("守住旅馆和养父最后的体面。");
        heMu.setPrivateObjective("确认自己与旧案之间到底有没有更深的血缘牵连。");
        heMu.setOpeningTip("你知道的不是最多，但你的沉默最容易被误读。");
        heMu.setResponseStrategy("前期回避旧案细节，越被逼近越容易露出情绪性停顿。");
        heMu.setSelectableByPlayer(true);

        CharacterDefinition duanLin = new CharacterDefinition();
        duanLin.setCharacterId("duan-lin");
        duanLin.setCharacterName("段临");
        duanLin.setIdentity("退职警员");
        duanLin.setRelationship("当年沉船案的外围调查人之一");
        duanLin.setPublicPersona("老练、怀疑心重、不爱被人拿旧年资压住");
        duanLin.setPublicBackstory("他曾短暂接触过沉船事故的外围调查，却在关键时刻被迫退出。");
        duanLin.setPrivateBackstory("他并非彻底无辜，当年他接受过一次不该收的封口礼，因此这些年一直不愿再提起港口旧案。");
        duanLin.setKnownFacts(List.of("沉船案最初调查方向被人为改过"));
        duanLin.setHiddenSecrets(List.of("当年收过封口礼", "手里还留着旧案编号碎记"));
        duanLin.setForbiddenDisclosures(List.of("不会主动承认自己收过钱"));
        duanLin.setPublicObjective("把怀疑拉回证据，不让旧案情绪直接吃掉今晚判断。");
        duanLin.setPrivateObjective("别让任何人追到你当年的失守，否则你会和旧案一起沉下去。");
        duanLin.setOpeningTip("你可以像办案的人，但不能像已经知道全部的人。");
        duanLin.setResponseStrategy("擅长盯细节，但提到当年办案流程时会明显谨慎。");
        duanLin.setSelectableByPlayer(true);

        CharacterDefinition qiaoYue = new CharacterDefinition();
        qiaoYue.setCharacterId("qiao-yue");
        qiaoYue.setCharacterName("乔月");
        qiaoYue.setIdentity("码头调度员");
        qiaoYue.setRelationship("负责今晚封港期间的货船登记与去留");
        qiaoYue.setPublicPersona("利落、嘴硬、对港口规则极熟");
        qiaoYue.setPublicBackstory("她常年在码头调度进出船次，对谁该出港、谁不该靠岸有近乎本能的敏感。");
        qiaoYue.setPrivateBackstory("她并不直接参与旧案，但她发现今晚有人试图借封港掩护处理一份旧货单。");
        qiaoYue.setKnownFacts(List.of("封港前有一条异常登记被人改过"))
        ;
        qiaoYue.setHiddenSecrets(List.of("偷偷复写过一份异常货单编号"));
        qiaoYue.setForbiddenDisclosures(List.of("不会主动交出复写编号"));
        qiaoYue.setPublicObjective("守住自己在今晚流程上的专业权威。");
        qiaoYue.setPrivateObjective("确认是谁试图在封港夜里动旧货单，再决定要不要把编号拿出来。");
        qiaoYue.setOpeningTip("你不是旧案当事人，但你手里可能有今晚最硬的流程线索。");
        qiaoYue.setResponseStrategy("对流程问题很强势，对旧案情感纠葛则显得格外不耐烦。");
        qiaoYue.setSelectableByPlayer(true);

        return List.of(innkeeper, shenChi, yuLan, heMu, duanLin, qiaoYue);
    }

    private List<StageDefinition> buildFogHarborStages() {
        StageDefinition stageOne = new StageDefinition();
        stageOne.setStageId("fog-stage-1");
        stageOne.setStageName("封港夜的第一封信");
        stageOne.setStageOrder(1);
        stageOne.setObjective("厘清来信到场、馆主倒下和众人第一反应之间的顺序。");
        stageOne.setOpeningNarration("旅馆门窗都关着，海雾却像从缝里挤了进来。所有人都盯着那封信，但又都比看信更在意别人的表情。");
        stageOne.setAvailableClueIds(List.of("fog-clue-register", "fog-clue-bell"));
        stageOne.setFocusCharacterIds(List.of("shen-chi", "yu-lan", "he-mu", "duan-lin", "qiao-yue"));
        stageOne.setAdvanceKeywords(List.of("来信", "谁先碰过信", "封港", "第一反应", "钟声"));
        stageOne.setMinimumTurnsBeforeAdvance(2);
        stageOne.setNextStageCondition("当玩家开始围绕来信触碰顺序和封港流程逼问时进入下一阶段。");

        StageDefinition stageTwo = new StageDefinition();
        stageTwo.setStageId("fog-stage-2");
        stageTwo.setStageName("旧案浮出水面");
        stageTwo.setStageOrder(2);
        stageTwo.setObjective("把来信和二十年前沉船旧案连起来，锁定谁最怕旧档被翻出。");
        stageTwo.setOpeningNarration("真正让人沉不住气的不是馆主的死，而是那封信证明旧案从来没有真正沉下去。");
        stageTwo.setAvailableClueIds(List.of("fog-clue-register", "fog-clue-bell", "fog-clue-file", "fog-clue-photo"));
        stageTwo.setFocusCharacterIds(List.of("shen-chi", "duan-lin", "he-mu"));
        stageTwo.setAdvanceKeywords(List.of("旧案", "沉船", "档案", "编号", "照片", "沈迟"));
        stageTwo.setMinimumTurnsBeforeAdvance(2);
        stageTwo.setNextStageCondition("当玩家把档案缺页、照片和沈迟的异常反应串起来时进入下一阶段。");

        StageDefinition stageThree = new StageDefinition();
        stageThree.setStageId("fog-stage-3");
        stageThree.setStageName("真正的收件人");
        stageThree.setStageOrder(3);
        stageThree.setObjective("凭决定性证据完成最终指认。");
        stageThree.setOpeningNarration("那封信不是寄给旅馆的，而是寄给过去。现在只剩最后一步，要看谁会被名字重新拖回海雾里。");
        stageThree.setAvailableClueIds(List.of("fog-clue-register", "fog-clue-bell", "fog-clue-file", "fog-clue-photo", "fog-clue-letter"));
        stageThree.setFocusCharacterIds(List.of("shen-chi"));
        stageThree.setAdvanceKeywords(List.of("最终指认", "收件人", "真凶"));
        stageThree.setMinimumTurnsBeforeAdvance(99);
        stageThree.setNextStageCondition("玩家完成最终指认后结束。");

        return List.of(stageOne, stageTwo, stageThree);
    }

    private List<ClueDefinition> buildFogHarborClues() {
        ClueDefinition register = clue(
                "fog-clue-register", "异常登记", ClueType.DOCUMENT,
                "封港前最后一份进出港登记有被人二次改写的痕迹，改写时间就在来信送达前后。",
                "说明今晚有人在流程层面先动过手。", "fog-stage-1", List.of("qiao-yue"), false
        );

        ClueDefinition bell = clue(
                "fog-clue-bell", "钟声证词", ClueType.TESTIMONY,
                "旅馆楼梯口的旧钟在馆主倒下前刚敲过一次半点，至少有一人的口供比钟声提前了。",
                "撬开第一轮时间线矛盾。", "fog-stage-1", List.of("shen-chi", "duan-lin"), false
        );

        ClueDefinition file = clue(
                "fog-clue-file", "缺页档案", ClueType.DOCUMENT,
                "港务旧档中沉船案编号对应页缺失，而缺页边角残留了档案署专用封签纤维。",
                "把旧案缺页和档案系统内部人联系起来。", "fog-stage-2", List.of("shen-chi"), true
        );

        ClueDefinition photo = clue(
                "fog-clue-photo", "湿痕旧照", ClueType.DOCUMENT,
                "一张多年前的码头合影背面有未干透的指痕，馆主、沈迟父亲和沉船夜的货主都在照片里。",
                "证明旧案关联远比众人承认的更直接。", "fog-stage-2", List.of("shen-chi", "he-mu"), true
        );

        ClueDefinition letter = clue(
                "fog-clue-letter", "迟到来信", ClueType.DOCUMENT,
                "拆开的信里写着完整货单编号和一句话：‘真正该收到这封信的人，一直住在档案里。’",
                "把来信矛头直接引向沈迟。", "fog-stage-3", List.of("shen-chi"), true
        );

        return List.of(register, bell, file, photo, letter);
    }

    private ClueDefinition clue(
            String clueId,
            String clueName,
            ClueType clueType,
            String content,
            String effect,
            String unlockStageId,
            List<String> relatedCharacterIds,
            boolean keyClue
    ) {
        ClueDefinition clueDefinition = new ClueDefinition();
        clueDefinition.setClueId(clueId);
        clueDefinition.setClueName(clueName);
        clueDefinition.setClueType(clueType);
        clueDefinition.setContent(content);
        clueDefinition.setEffect(effect);
        clueDefinition.setUnlockStageId(unlockStageId);
        clueDefinition.setRelatedCharacterIds(relatedCharacterIds);
        clueDefinition.setKeyClue(keyClue);
        return clueDefinition;
    }
}
