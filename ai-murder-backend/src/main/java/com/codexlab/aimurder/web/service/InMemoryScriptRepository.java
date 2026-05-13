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
    public static final String THIRD_SCRIPT_ID = "summer-evening-cicadas";

    private final Map<String, ScriptDefinition> scriptStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        ScriptDefinition rainyNight = buildRainyNightBlackout();
        ScriptDefinition fogHarbor = buildFogHarborLetter();
        ScriptDefinition summerEvening = buildSummerEveningCicadas();
        scriptStore.put(rainyNight.getScriptId(), rainyNight);
        scriptStore.put(fogHarbor.getScriptId(), fogHarbor);
        scriptStore.put(summerEvening.getScriptId(), summerEvening);
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
        scriptDefinition.setPlayerModeName("固定主视角 / 山庄暴雨推理");
        scriptDefinition.setPlayerModeDescription("玩家以固定主视角进入山庄停电案，从在场嫌疑人之间的问答、时间线和线索冲突里逐步逼近真相。");
        scriptDefinition.setUnlockOrder(1);
        scriptDefinition.setRandomRoleOnStart(false);
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

        CharacterDefinition detective = new CharacterDefinition();
        detective.setCharacterId("detective");
        detective.setCharacterName("顾徵");
        detective.setIdentity("侦探");
        detective.setRelationship("受邀来到山庄的独立调查者，负责在暴雨封庄的夜里主持这场临时审问。");
        detective.setPublicPersona("冷静、克制、擅长从矛盾里逼出真相");
        detective.setPublicBackstory("你并不属于山庄旧关系网，因此每个人都想借你的判断替自己洗清嫌疑，也都害怕你真的看穿他们。");
        detective.setKnownFacts(List.of("停电后的书房是第一现场", "所有嫌疑人都各自藏着不愿明说的动机与漏洞"));
        detective.setPublicObjective("稳定局面，厘清停电前后的时间线和每个人的关键动向。");
        detective.setPrivateObjective("尽快把停电、账本和人际冲突拼成一条可验证的证据链。");
        detective.setOpeningTip("你不是嫌疑人，但你说出的每一句判断，都会改变现场每个人的防御姿态。");
        detective.setResponseStrategy("以提问、归纳和压迫式追索推动剧情，不承担嫌疑席位。");
        detective.setSelectableByPlayer(true);

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

        return List.of(butler, detective, linQiao, guShen, zhouYan, luChen);
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

    private ScriptDefinition buildSummerEveningCicadas() {
        ScriptDefinition scriptDefinition = new ScriptDefinition();
        scriptDefinition.setScriptId(THIRD_SCRIPT_ID);
        scriptDefinition.setScriptName("蝉鸣晚自习");
        scriptDefinition.setSummary("高二晚自习前，一张匿名纸条和一段被剪辑过的语音，把六个关系缠绕的少年少女同时推到灯下。你将固定扮演许知夏，从暗恋与误会里一路摸到每个人真正想藏住的秘密。");
        scriptDefinition.setOpeningNarration("傍晚的热气还压在教学楼里，风扇转得很慢，窗外操场尽头的蝉声却已经吵得像一整面墙。你刚把书包塞进桌洞，就看见习题册下面有一点不该出现的银色反光。");
        scriptDefinition.setPlayerModeName("固定主视角 / 校园关系推理");
        scriptDefinition.setPlayerModeDescription("玩家固定扮演高二女生许知夏，在四段晚自习时序里通过追问、观察和线索拼接，逐步识别六人关系网与真正被故意引爆的矛盾。");
        scriptDefinition.setUnlockOrder(3);
        scriptDefinition.setRandomRoleOnStart(false);
        scriptDefinition.setHostCharacterId("homeroom-teacher");
        scriptDefinition.setOpeningInstruction("先交代高二晚自习前的教室氛围、玩家身份和最初异常，再让其余角色依次给出第一反应，保留真正秘密。");
        scriptDefinition.setNarrationInstruction("旁白负责教室、走廊、广播室和课间气氛的推进，要像青春文学里的镜头，不直接替角色交代内心答案。");
        scriptDefinition.setTruthSummary("真正故意推动这场矛盾的人是林澄。她放出匿名纸条和被剪过的语音，不是为了毁掉谁，而是为了逼周屿停止替人扛下一次违纪旧事，也逼所有人面对已经藏不下去的关系裂缝。");
        scriptDefinition.setEndingTitle("晚自习后的名字");
        scriptDefinition.setEndingStory("匿名纸条只是刀尖，真正被割开的，是每个人小心维持的体面。林澄故意把矛盾推到所有人都无法再装作没看见的位置，想逼周屿和叶真把那次违纪、替扛与沉默全部说出来。许知夏最终看清的，不只是自己喜欢的人，更是每个人在那段夏夜里各自背着的重量。");
        scriptDefinition.setMinimumKeyCluesForAccusation(3);
        scriptDefinition.setCharacters(buildSummerEveningCharacters());
        scriptDefinition.setStages(buildSummerEveningStages());
        scriptDefinition.setClues(buildSummerEveningClues());
        return scriptDefinition;
    }

    private List<CharacterDefinition> buildSummerEveningCharacters() {
        CharacterDefinition teacher = new CharacterDefinition();
        teacher.setCharacterId("homeroom-teacher");
        teacher.setCharacterName("陈老师");
        teacher.setIdentity("高二二班班主任");
        teacher.setRelationship("维持晚自习秩序的人，也是这场少年人风暴外围唯一的成人");
        teacher.setPublicPersona("稳、克制、尽量不给任何人贴死标签");
        teacher.setPublicBackstory("陈老师今晚不会一直站在教室里，但她的存在让所有人都还努力维持着最后一点体面。");
        teacher.setResponseStrategy("负责控场、转场和提醒时间，不替任何学生作答。");

        CharacterDefinition xuZhixia = new CharacterDefinition();
        xuZhixia.setCharacterId("xu-zhixia");
        xuZhixia.setCharacterName("许知夏");
        xuZhixia.setIdentity("高二二班语文课代表");
        xuZhixia.setRelationship("站在所有关系边缘的人，却恰好最容易看见细节");
        xuZhixia.setPublicPersona("安静、细腻、不擅长争抢存在感");
        xuZhixia.setPublicBackstory("你习惯把话咽回去，把喜欢藏起来，也把很多不确定的目光留给自己消化。");
        xuZhixia.setPrivateBackstory("你暗恋周屿很久了，也隐约知道宋晚已经看出来。更重要的是，你曾在值日那天捡到一张写着“你不该替她瞒着”的便利贴，却一直没敢问出口。");
        xuZhixia.setKnownFacts(List.of("宋晚大概知道自己的暗恋", "周屿最近状态不对", "晚自习前自己的桌洞里出现了不该有的东西"));
        xuZhixia.setPublicObjective("先弄明白今天到底发生了什么，不要让局面完全失控。");
        xuZhixia.setPrivateObjective("看清每个人之间真正的关系，也看清周屿、宋晚和林澄到底各自隐瞒了什么。");
        xuZhixia.setOpeningTip("你不是最会说话的人，但你最擅长从别人漏掉的停顿里听出不对劲。");
        xuZhixia.setResponseStrategy("玩家固定扮演角色，由玩家自由提问、判断和推进。");
        xuZhixia.setSelectableByPlayer(true);

        CharacterDefinition zhouYu = new CharacterDefinition();
        zhouYu.setCharacterId("zhou-yu");
        zhouYu.setCharacterName("周屿");
        zhouYu.setIdentity("篮球队主力");
        zhouYu.setRelationship("许知夏暗恋对象，也是所有人最容易误会成故事中心的人");
        zhouYu.setPublicPersona("开朗、松弛、看起来总能把气氛接住");
        zhouYu.setPublicBackstory("他在班里一直很显眼，成绩不差，又总像没什么真正过不去的事。");
        zhouYu.setPrivateBackstory("他最近压力很大，家里关系恶化，又一直替叶真扛着一次足以影响评优的违纪记录。林澄知道这件事，并帮他一起瞒着。");
        zhouYu.setKnownFacts(List.of("林澄知道自己的一部分难处", "叶真那次违纪不是小事", "宋晚可能留着会还原时间线的东西"));
        zhouYu.setHiddenSecrets(List.of("替叶真顶过违纪", "知道语音被剪过，却没第一时间拆穿"));
        zhouYu.setForbiddenDisclosures(List.of("不能主动说出自己替谁扛了违纪", "不能主动说出林澄是如何帮忙压下记录的"));
        zhouYu.setPublicObjective("把今晚所有人关于感情的误会压下去，别让局面越闹越大。");
        zhouYu.setPrivateObjective("守住叶真那次违纪的真相，也别让林澄因为帮忙收尾被一起拖进风暴里。");
        zhouYu.setOpeningTip("你越想稳住局面，越容易在关键细节上露出你知道得太多。");
        zhouYu.setResponseStrategy("前期会用轻松语气打圆场，被追到时间线和违纪细节时会明显收紧。");

        CharacterDefinition linCheng = new CharacterDefinition();
        linCheng.setCharacterId("lin-cheng");
        linCheng.setCharacterName("林澄");
        linCheng.setIdentity("班长");
        linCheng.setRelationship("最擅长维持秩序的人，也是今晚真正故意推动失衡的人");
        linCheng.setPublicPersona("理性、稳、总能先把场面接住");
        linCheng.setPublicBackstory("她习惯记住每个人的作业、座位、值日和情绪，一直像把教室秩序缝起来的人。");
        linCheng.setPrivateBackstory("她已经厌倦了替所有人收拾残局。匿名纸条和剪辑语音都是她放出来的，她想逼周屿停止继续替叶真扛事，也逼宋晚和程野别再靠沉默回避真相。");
        linCheng.setKnownFacts(List.of("知道违纪记录是怎么被压住的", "知道宋晚手里还有旧纸条和照片", "知道许知夏今晚迟早会被卷进来"));
        linCheng.setHiddenSecrets(List.of("放出匿名纸条", "故意推动了语音传播", "知道走廊争执会成为导火索"));
        linCheng.setForbiddenDisclosures(List.of("不能主动承认自己安排了矛盾", "不能直接替周屿说出违纪真相"));
        linCheng.setPublicObjective("把所有人从无效争吵里拉回事实，至少表面看起来如此。");
        linCheng.setPrivateObjective("逼真正该开口的人自己说出那次违纪、那次顶替和后来每个人的沉默。");
        linCheng.setOpeningTip("你看起来最像秩序本身，所以不到最后，没人该轻易觉得你在推波助澜。");
        linCheng.setResponseStrategy("前期控场，中期转为反问和逼问，证据逼近时会从冷静变得尖锐。");
        linCheng.setKiller(true);

        CharacterDefinition songWan = new CharacterDefinition();
        songWan.setCharacterId("song-wan");
        songWan.setCharacterName("宋晚");
        songWan.setIdentity("文艺委员");
        songWan.setRelationship("许知夏最亲近的朋友，也是许多沉默证据的保管者");
        songWan.setPublicPersona("温柔、安静、像总会替别人留余地");
        songWan.setPublicBackstory("她似乎很少站到风暴中心，却总在别人说完以后，补出最让人没法忽略的一句。");
        songWan.setPrivateBackstory("她一直知道许知夏喜欢周屿，也一直留着那些别人随手丢掉的纸条、照片和一封没送出的信。她不是故意瞒着谁，只是不知道什么时候说出来才不会伤人。");
        songWan.setKnownFacts(List.of("知道许知夏暗恋周屿", "知道林澄和周屿之间有不止班务的秘密", "知道照片为什么会被撕掉一半"));
        songWan.setHiddenSecrets(List.of("留着没送出的信", "留着能还原时间线的旧纸条和照片"));
        songWan.setForbiddenDisclosures(List.of("不会主动交出那封信", "不会在前期说出许知夏的暗恋"));
        songWan.setPublicObjective("别让今晚把所有人的关系彻底撕裂。");
        songWan.setPrivateObjective("尽量保护许知夏，也尽量别让自己手里的东西变成伤人的证据。");
        songWan.setOpeningTip("你知道得越多，越会在关键时刻显得犹豫。");
        songWan.setResponseStrategy("前期温和回避，被线索点穿后会说出很关键的补充细节。");

        CharacterDefinition chengYe = new CharacterDefinition();
        chengYe.setCharacterId("cheng-ye");
        chengYe.setCharacterName("程野");
        chengYe.setIdentity("转学生");
        chengYe.setRelationship("最像旁观者的人，却和其中某段旧事有真正牵连");
        chengYe.setPublicPersona("冷淡、疏离、像对谁都没有兴趣");
        chengYe.setPublicBackstory("他来这个班不久，和所有人都保持距离，所以任何情绪起伏都会显得格外明显。");
        chengYe.setPrivateBackstory("他初中时认识叶真，也知道她不是那种会无缘无故失控的人。他转来之后很快发现周屿和林澄一直在共同掩盖什么，只是还没拼完整。");
        chengYe.setKnownFacts(List.of("叶真不是无端发火", "广播室那段语音不是原始版本", "林澄今晚像在等某个时刻出现"));
        chengYe.setHiddenSecrets(List.of("知道语音被剪过", "和叶真有初中旧识"));
        chengYe.setForbiddenDisclosures(List.of("不会主动说出自己和叶真以前认识", "不会一开始就交代自己发现语音有问题"));
        chengYe.setPublicObjective("先确认今晚是谁在故意引爆局面。");
        chengYe.setPrivateObjective("别让叶真一个人扛下所有情绪反应，也别让自己和她的旧事成为新的误会。");
        chengYe.setOpeningTip("你可以冷眼旁观，但不能像已经知道答案。");
        chengYe.setResponseStrategy("前期像观察者，后期会精准指出别人回避的细节。");

        CharacterDefinition yeZhen = new CharacterDefinition();
        yeZhen.setCharacterId("ye-zhen");
        yeZhen.setCharacterName("叶真");
        yeZhen.setIdentity("体育委员");
        yeZhen.setRelationship("看起来最像矛盾制造者，实际是被保护最久的人");
        yeZhen.setPublicPersona("直、急、容易顶撞人");
        yeZhen.setPublicBackstory("她不擅长把委屈藏得很深，所以很多人都以为她的问题最好懂。");
        yeZhen.setPrivateBackstory("那次真正违纪的人是她。周屿替她顶下记录，林澄帮忙压住，宋晚无意中保留了某张能还原那晚动线的照片。她今晚发火，是因为她知道有人在拿感情线掩盖真正的问题。");
        yeZhen.setKnownFacts(List.of("周屿替自己扛过违纪", "林澄故意在逼谁开口", "程野看得出自己不对劲"));
        yeZhen.setHiddenSecrets(List.of("真正违纪者是自己", "撞见过林澄处理匿名纸条"));
        yeZhen.setForbiddenDisclosures(List.of("不能主动承认违纪是自己", "不能主动暴露林澄放纸条"));
        yeZhen.setPublicObjective("别让所有人都把今晚说成一场无聊的感情戏。");
        yeZhen.setPrivateObjective("如果真相一定要出来，至少别再让周屿替自己站在前面。");
        yeZhen.setOpeningTip("你的情绪是真的，所以一旦退缩，别人就会立刻知道你碰到了最痛的地方。");
        yeZhen.setResponseStrategy("前期易爆，后期在真正被追到核心时反而会短暂沉默。");

        return List.of(teacher, xuZhixia, zhouYu, linCheng, songWan, chengYe, yeZhen);
    }

    private List<StageDefinition> buildSummerEveningStages() {
        StageDefinition stageOne = new StageDefinition();
        stageOne.setStageId("summer-stage-1");
        stageOne.setStageName("晚自习前的反光");
        stageOne.setStageOrder(1);
        stageOne.setObjective("确认匿名纸条、桌洞反光和第一轮走廊争执分别牵住了谁。");
        stageOne.setOpeningNarration("教室里还没完全安静下来，椅脚声、翻书声和窗外蝉鸣混在一起。所有人看起来都还坐在自己的位置上，但真正先乱掉的，是那些没有被说出口的视线。");
        stageOne.setAvailableClueIds(List.of("summer-clue-recorder-shell", "summer-clue-anonymous-note", "summer-clue-corridor-argument"));
        stageOne.setFocusCharacterIds(List.of("zhou-yu", "lin-cheng", "song-wan", "ye-zhen"));
        stageOne.setAdvanceKeywords(List.of("桌洞", "纸条", "反光", "语音", "谁吵架", "走廊"));
        stageOne.setMinimumTurnsBeforeAdvance(2);
        stageOne.setNextStageCondition("当玩家开始把匿名纸条和具体人物反应联系起来时进入下一阶段。");

        StageDefinition stageTwo = new StageDefinition();
        stageTwo.setStageId("summer-stage-2");
        stageTwo.setStageName("被剪过的声音");
        stageTwo.setStageOrder(2);
        stageTwo.setObjective("把矛盾从表面的喜欢和误会，推进到谁在故意传递不完整信息。");
        stageTwo.setOpeningNarration("真正开始发烫的不是空气，是每个人说出口之前的那半秒停顿。有人希望这只是一次感情误会，也有人拼命不让话题往更深的地方走。");
        stageTwo.setAvailableClueIds(List.of("summer-clue-recorder-shell", "summer-clue-anonymous-note", "summer-clue-corridor-argument", "summer-clue-ripped-photo", "summer-clue-audio-fragment", "summer-clue-duty-log"));
        stageTwo.setFocusCharacterIds(List.of("lin-cheng", "song-wan", "cheng-ye", "ye-zhen"));
        stageTwo.setAdvanceKeywords(List.of("广播室", "剪辑", "照片", "值日", "谁知道", "原始语音"));
        stageTwo.setMinimumTurnsBeforeAdvance(2);
        stageTwo.setNextStageCondition("当玩家开始追问语音来源、照片残片和异常值日记录时进入下一阶段。");

        StageDefinition stageThree = new StageDefinition();
        stageThree.setStageId("summer-stage-3");
        stageThree.setStageName("课间没有散掉");
        stageThree.setStageOrder(3);
        stageThree.setObjective("还原违纪旧事、顶替链条和林澄故意引爆矛盾的真正目的。");
        stageThree.setOpeningNarration("课间铃已经响过，但没有一个人真的从这场局里走出去。窗外的风进来了，教室里的热却一点没散。");
        stageThree.setAvailableClueIds(List.of("summer-clue-recorder-shell", "summer-clue-anonymous-note", "summer-clue-corridor-argument", "summer-clue-ripped-photo", "summer-clue-audio-fragment", "summer-clue-duty-log", "summer-clue-unsent-letter", "summer-clue-discipline-copy", "summer-clue-broadcast-trash"));
        stageThree.setFocusCharacterIds(List.of("zhou-yu", "lin-cheng", "song-wan", "ye-zhen"));
        stageThree.setAdvanceKeywords(List.of("违纪", "谁替谁", "保送", "没送出的信", "林澄", "为什么故意"));
        stageThree.setMinimumTurnsBeforeAdvance(2);
        stageThree.setNextStageCondition("当玩家把违纪、顶替和故意引爆矛盾的动机串起来时进入最终阶段。");

        StageDefinition stageFour = new StageDefinition();
        stageFour.setStageId("summer-stage-4");
        stageFour.setStageName("晚自习后的名字");
        stageFour.setStageOrder(4);
        stageFour.setObjective("完成最终判断，指出谁故意推动了这场矛盾，并解释她真正想逼出的真相。");
        stageFour.setOpeningNarration("现在已经没有人能退回晚自习开始以前。真正要落下来的，不是谁喜欢谁的答案，而是谁把今晚推到了这里。");
        stageFour.setAvailableClueIds(List.of("summer-clue-recorder-shell", "summer-clue-anonymous-note", "summer-clue-corridor-argument", "summer-clue-ripped-photo", "summer-clue-audio-fragment", "summer-clue-duty-log", "summer-clue-unsent-letter", "summer-clue-discipline-copy", "summer-clue-broadcast-trash", "summer-clue-teacher-note", "summer-clue-timeline-sketch", "summer-clue-full-audio"));
        stageFour.setFocusCharacterIds(List.of("lin-cheng", "zhou-yu", "ye-zhen"));
        stageFour.setAdvanceKeywords(List.of("最终指认", "故意引爆", "幕后的人", "就是林澄"));
        stageFour.setMinimumTurnsBeforeAdvance(99);
        stageFour.setNextStageCondition("玩家完成最终指认后结束。");

        return List.of(stageOne, stageTwo, stageThree, stageFour);
    }

    private List<ClueDefinition> buildSummerEveningClues() {
        ClueDefinition recorderShell = clue(
                "summer-clue-recorder-shell", "银色录音笔外壳", ClueType.PHYSICAL,
                "许知夏桌洞深处藏着一个银色录音笔外壳，边缘有被仓促拆开的划痕，像有人只拿走了里面真正重要的部分。",
                "提示今晚的信息传播并不自然，有人提前处理过音频相关物件。",
                "summer-stage-1", List.of("lin-cheng", "song-wan", "cheng-ye"), false
        );

        ClueDefinition anonymousNote = clue(
                "summer-clue-anonymous-note", "匿名纸条", ClueType.DOCUMENT,
                "纸条上只有一句话：'你以为他真的喜欢你吗？' 字迹故意压得很平，像怕别人一眼认出来。",
                "把所有人注意力先引向感情误会，为真正的矛盾打掩护。",
                "summer-stage-1", List.of("xu-zhixia", "zhou-yu", "lin-cheng"), false
        );

        ClueDefinition corridorArgument = clue(
                "summer-clue-corridor-argument", "走廊争执", ClueType.TESTIMONY,
                "晚自习前有人听见叶真和周屿在走廊压低声音争执，叶真说过一句：'你别再替我装没事。'",
                "说明两人争执的核心并不是暧昧，而是某件已经持续了一段时间的隐瞒。",
                "summer-stage-1", List.of("zhou-yu", "ye-zhen"), true
        );

        ClueDefinition rippedPhoto = clue(
                "summer-clue-ripped-photo", "被撕开的合照", ClueType.DOCUMENT,
                "后排储物柜里藏着半张旧合照，残留部分能看出周屿、叶真和宋晚都在，另一半像是被人专门撕走了。",
                "说明有人在主动切断能还原旧时间线的物证。",
                "summer-stage-2", List.of("song-wan", "zhou-yu", "ye-zhen"), false
        );

        ClueDefinition audioFragment = clue(
                "summer-clue-audio-fragment", "被剪过的语音片段", ClueType.DOCUMENT,
                "流出来的语音里只剩一句：'你就是仗着她不会说。' 结尾处的环境底噪断得很生硬，明显不是完整原件。",
                "把矛盾从单纯感情戏引向“谁被利用、谁被迫沉默”。",
                "summer-stage-2", List.of("lin-cheng", "cheng-ye", "ye-zhen"), true
        );

        ClueDefinition dutyLog = clue(
                "summer-clue-duty-log", "值日表涂改痕迹", ClueType.DOCUMENT,
                "值日表右下角有一次被擦掉又重写的记录，那天本该留下来锁广播室的人并不是后来登记的那个名字。",
                "说明广播室和语音传播链路里有人替换过行动顺序。",
                "summer-stage-2", List.of("lin-cheng", "song-wan"), false
        );

        ClueDefinition unsentLetter = clue(
                "summer-clue-unsent-letter", "没送出的信", ClueType.DOCUMENT,
                "天台门口夹层里藏着一封没送出的信，开头写着：'如果你再替我扛一次，我以后连看你都不敢。'",
                "直接把矛头指向“替谁扛”的旧事，而不是谁喜欢谁。",
                "summer-stage-3", List.of("zhou-yu", "ye-zhen"), true
        );

        ClueDefinition disciplineCopy = clue(
                "summer-clue-discipline-copy", "旧违纪单复印件", ClueType.DOCUMENT,
                "违纪单复印件上的名字是周屿，但时间、地点和旁边潦草添上的备注都与叶真那晚的行动轨迹更吻合。",
                "说明违纪责任存在顶替，周屿不是表面上那次事件的真正当事人。",
                "summer-stage-3", List.of("zhou-yu", "ye-zhen", "lin-cheng"), true
        );

        ClueDefinition broadcastTrash = clue(
                "summer-clue-broadcast-trash", "广播室回收站记录", ClueType.DOCUMENT,
                "广播室电脑回收站里有一段被删除的音频缓存，创建时间恰好在晚自习前十分钟，文件名带着班级缩写。",
                "坐实语音是被人为处理后投放，不是自然流出。",
                "summer-stage-3", List.of("lin-cheng", "cheng-ye"), false
        );

        ClueDefinition teacherNote = clue(
                "summer-clue-teacher-note", "班长记录本备注页", ClueType.DOCUMENT,
                "林澄的记录本边页写着一行很轻的字：'再拖下去，所有人都会一起坏掉。'",
                "揭示林澄的动机更接近逼真相浮出，而不是单纯伤害谁。",
                "summer-stage-4", List.of("lin-cheng"), false
        );

        ClueDefinition timelineSketch = clue(
                "summer-clue-timeline-sketch", "课间时间线草图", ClueType.PHYSICAL,
                "宋晚留下的一张草图把晚自习前后几个人的动线全画了出来，广播室、走廊和储物柜在同一时间段被反复圈出。",
                "让玩家能把零散细节拼成一条完整的行动链。",
                "summer-stage-4", List.of("song-wan", "lin-cheng", "zhou-yu", "ye-zhen"), false
        );

        ClueDefinition fullAudio = clue(
                "summer-clue-full-audio", "原始完整语音", ClueType.DOCUMENT,
                "完整语音里真正的话是：'你就是仗着她不会说，才一直替她把那件事压下去。' 说话的人是林澄，她的语气更像逼问，不像挑拨。",
                "最终指向林澄才是主动引爆矛盾的人，但她在逼出的是真相，不是八卦。",
                "summer-stage-4", List.of("lin-cheng", "zhou-yu", "ye-zhen"), true
        );

        return List.of(
                recorderShell,
                anonymousNote,
                corridorArgument,
                rippedPhoto,
                audioFragment,
                dutyLog,
                unsentLetter,
                disciplineCopy,
                broadcastTrash,
                teacherNote,
                timelineSketch,
                fullAudio
        );
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
