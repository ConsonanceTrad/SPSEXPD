package pd.actors;

import pd.Dungeon;
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.HealLight;
import pd.actors.blobs.SlowGas;
import pd.actors.blobs.TarGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.actors.buffs.AcidOoze;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.BoxStar;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.buffs.ShadowCurse;
import pd.actors.buffs.StoneIce;
import pd.actors.buffs.Tar;
import pd.actors.buffs.Wet;
import pd.actors.buffs.armorbuff.GlyphEarth;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Hero;
import pd.actors.mobs.*;
import pd.actors.mobs.npcs.Blacksmith;
import pd.actors.mobs.npcs.Ghost;
import pd.actors.mobs.npcs.ImpShopkeeper;
import pd.actors.mobs.npcs.MirrorImage;
import pd.actors.mobs.npcs.RatKing;
import pd.actors.mobs.npcs.Sheep;
import pd.actors.mobs.npcs.Shopkeeper;
import pd.actors.mobs.npcs.Tinkerer1;
import pd.actors.mobs.npcs.Tinkerer2;
import pd.actors.mobs.npcs.Wandmaker;
import pd.actors.mobs.pets.Abi;
import pd.actors.mobs.pets.BlueGirl;
import pd.actors.mobs.pets.Bunny;
import pd.actors.mobs.pets.LeryFire;
import pd.actors.mobs.pets.Scorpion;
import pd.actors.mobs.pets.YearPet;
import pd.items.bombs.DungeonBomb;
import pd.items.scrolls.ScrollOfPsionicBlast;
import pd.items.wands.WandOfAcid;
import pd.items.wands.WandOfFreeze;
import pd.items.wands.WandOfLight;
import pd.items.wands.fusion.WandOfFlow;
import pd.items.weapon.enchantments.EnchantmentDark;
import pd.items.weapon.enchantments.EnchantmentEarth2;
import pd.items.weapon.enchantments.EnchantmentEarth;

/** Verifies the resistance, immunity, and weakness table from SPS-PD 0.9.8 Char.Property. */
public final class SpsLegacyPropertiesTest {

	public static void main(String[] args) {
		checkPropertyTables();
		checkLegacyMobTags();
		checkBossRushDefenses();
		checkEarthGlyph();
		checkWeaknessDuration();
		System.out.println("SPS旧版阵营属性测试通过：十二类抗性、免疫、弱点及1.5倍状态时长均正常。");
	}

	private static void checkBossRushDefenses() {
		UYog common = new UYog();
		resists(common, ToxicGas.class, "Boss Rush毒气抗性");
		resists(common, Poison.class, "Boss Rush中毒抗性");
		immune(common, ToxicGas.class, "Boss Rush毒气免疫");
		immune(common, ScrollOfPsionicBlast.class, "Boss Rush灵能震爆免疫");
		immune(common, Bleeding.class, "Boss Rush流血免疫");

		UAmulet amulet = new UAmulet();
		resists(amulet, EnchantmentDark.class, "护符暗影暗附魔抗性");
		immune(amulet, EnchantmentDark.class, "护符暗影暗附魔免疫");

		UDM300 machine = new UDM300();
		immune(machine, EnchantmentDark.class, "终极DM300暗附魔免疫");
		immune(machine, SlowGas.class, "终极DM300迟缓气体免疫");
		immune(machine, TarGas.class, "终极DM300焦油气体免疫");
		immune(machine, Tar.class, "终极DM300焦油免疫");
		TestUDM300 testMachine = new TestUDM300();
		testMachine.setPhase(1);
		check(testMachine.phaseBlob() == SlowGas.class && testMachine.phaseVolume() == 30,
				"终极DM300第一阶段气体错误");
		testMachine.setPhase(2);
		check(testMachine.phaseBlob() == TarGas.class && testMachine.phaseVolume() == 60,
				"终极DM300第二阶段气体错误");
		testMachine.setPhase(3);
		check(testMachine.phaseBlob() == DarkGas.class && testMachine.phaseVolume() == 100,
				"终极DM300第三阶段气体错误");
		check(new TestSeekBomb().bomb() instanceof DungeonBomb, "终极追踪炸弹没有使用旧版地牢炸弹");

		UGoo goo = new UGoo();
		immune(goo, EnchantmentDark.class, "终极粘咕暗附魔免疫");
		TestUGoo testGoo = new TestUGoo();
		testGoo.triggerPhase();
		check(testGoo.spawnCount == 4, "终极粘咕首阶段没有召唤四种分身");
		testGoo.livingMinions = true;
		testGoo.triggerPhase();
		check(testGoo.spawnCount == 4, "终极粘咕在分身存活时重复召唤");
		testGoo.livingMinions = false;
		testGoo.triggerPhase();
		check(testGoo.spawnCount == 8, "终极粘咕没有在下一阶段重新召唤已清除的分身");
		testGoo.setFinalPhase();
		check(Math.abs(testGoo.speed() - 1.5f) < 0.001f, "终极粘咕最终阶段不是六倍基础速度");
		testGoo.HP = 100;
		testGoo.livingMinions = true;
		testGoo.damage(20, new TaggedMob());
		check(testGoo.HP == 100, "终极粘咕在分身存活时没有免疫伤害");
		UGoo.EarthGoo earth = new UGoo.EarthGoo();
		check(earth.state == earth.WANDERING, "大地粘咕没有以游荡状态生成");
		immune(earth, Poison.class, "大地粘咕中毒免疫");
		immune(earth, ToxicGas.class, "大地粘咕毒气免疫");
		UGoo.FireGoo fire = new UGoo.FireGoo();
		immune(fire, Burning.class, "火焰粘咕燃烧免疫");
		immune(fire, ScrollOfPsionicBlast.class, "火焰粘咕灵能震爆免疫");
		UGoo.IceGoo ice = new UGoo.IceGoo();
		check(ice.state == ice.FLEEING, "寒冰粘咕没有以逃跑状态生成");
		immune(ice, SlowGas.class, "寒冰粘咕迟缓气体免疫");
		UGoo.ShockGoo shock = new UGoo.ShockGoo();
		immune(shock, ElectriShock.class, "雷电粘咕电击免疫");

		UIcecorps icecorps = new UIcecorps();
		immune(icecorps, Chill.class, "终极冰兔寒冷免疫");
		immune(icecorps, Frost.class, "终极冰兔霜冻免疫");
		immune(new UIcecorps2(), Frost.class, "终极冰兔二形态霜冻免疫");
		check(new TestIcecorps().summonedFireRabbit().getClass() == FireRabbit.class,
				"终极冰兔没有召唤旧版顶层烈焰兔人");
		Hero previousHero = Dungeon.hero;
		Dungeon.hero = new Hero();
		try {
			icecorps.HP = 100;
			Buff.affect(icecorps, BoxStar.class, 3f);
			icecorps.damage(20, new Object());
			check(icecorps.HP == 100, "终极冰兔没有执行星盒无敌");
			icecorps.damage(20, new StoneIce());
			check(icecorps.HP == 90, "终极冰兔没有执行寒冰石固定10点伤害");
		} finally {
			Dungeon.hero = previousHero;
		}
	}

	private static void checkLegacyMobTags() {
		has(new Rat(), Char.Property.BEAST, "老鼠");
		has(new Crab(), Char.Property.FISHER, "螃蟹");
		has(new Bat(), Char.Property.BEAST, "吸血蝙蝠");
		has(new Gnoll(), Char.Property.ORC, "豺狼");
		has(new Brute(), Char.Property.ORC, "豺狼暴徒");
		has(new Thief(), Char.Property.GOBLIN, "疯狂小偷");
		has(new Warlock(), Char.Property.DWARF, "矮人术士");
		has(new Warlock(), Char.Property.MAGICER, "矮人术士");
		has(new Monk(), Char.Property.DWARF, "矮人武僧");
		has(new Eye(), Char.Property.MAGICER, "邪恶魔眼");
		has(new Guard(), Char.Property.HUMAN, "监狱守卫");
		has(new GnollKing(), Char.Property.ORC, "豺狼王");
		has(new ThiefKing(), Char.Property.ELF, "盗贼之王");
		has(new ShadowYog(), Char.Property.UNKNOW, "暗影古神");
		has(new Zot(), Char.Property.UNKNOW, "Zot");
		has(new ZotPhase(), Char.Property.UNKNOW, "Zot虚像");

		has(new Albino(), Char.Property.BEAST, "白化老鼠");
		has(new Albino(), Char.Property.DEMONIC, "白化老鼠");
		has(new AlbinoPiranha(), Char.Property.FISHER, "白化食人鱼");
		has(new Bandit(), Char.Property.ELF, "疯狂强盗");
		has(new DwarfLich(), Char.Property.DWARF, "矮人巫妖");
		has(new DwarfLich(), Char.Property.MAGICER, "矮人巫妖");
		has(new Fiend(), Char.Property.ELEMENT, "恶魔精灵");
		has(new FishProtector(), Char.Property.ELEMENT, "水域守护者");
		has(new FlyingProtector(), Char.Property.ELEMENT, "飞行守护者");
		has(new GnollArcher(), Char.Property.ORC, "豺狼射手");
		has(new GoldOrc(), Char.Property.ORC, "黄金兽人");
		has(new GoldThief(), Char.Property.ELF, "黄金盗贼");
		has(new Golem(), Char.Property.MECH, "魔像");
		has(new Goo(), Char.Property.UNKNOW, "旧粘咕兼容类");
		has(new GraveProtector(), Char.Property.TROLL, "墓地守护者");
		has(new GreatCrab(), Char.Property.BEAST, "巨钳螃蟹");
		has(new HermitCrab(), Char.Property.BEAST, "寄居蟹兼容类");
		has(new HermitCrab(), Char.Property.BOSS, "寄居蟹兼容类");
		has(new IceBall(), Char.Property.ELEMENT, "冰球");
		has(new IceBall(), Char.Property.MECH, "冰球");
		has(new LitTower(), Char.Property.MECH, "巫术塔");
		has(new LitTower(), Char.Property.MAGICER, "巫术塔");
		has(new MagicEye(), Char.Property.MAGICER, "魔法眼");
		has(new MagicEye(), Char.Property.ELEMENT, "魔法眼");
		has(new Mimic(), Char.Property.UNKNOW, "宝箱怪");
		has(new MineSentinel(), Char.Property.MECH, "矿区哨兵");
		has(new MonsterBox(), Char.Property.UNKNOW, "怪物箱");
		has(new Orc(), Char.Property.ORC, "兽人");
		has(new Otiluke(), Char.Property.ELEMENT, "奥蒂卢克");
		has(new Otiluke(), Char.Property.MAGICER, "奥蒂卢克");
		has(new Piranha(), Char.Property.FISHER, "食人鱼");
		has(new Scorpio(), Char.Property.BEAST, "巨蝎");
		has(new Acidic(), Char.Property.BEAST, "酸液巨蝎");
		has(new Sentinel(), Char.Property.MECH, "哨兵");
		has(new Shell(), Char.Property.MECH, "高压电壳");
		has(new SkeletonHand1(), Char.Property.BOSS, "骷髅左手");
		has(new SkeletonHand2(), Char.Property.BOSS, "骷髅右手");
		has(new SokobanSentinel(), Char.Property.MECH, "推箱哨兵");
		has(new Spinner(), Char.Property.BEAST, "洞穴蜘蛛");
		has(new Statue(), Char.Property.ELEMENT, "雕像");
		has(new Swarm(), Char.Property.BEAST, "蝇群");
		has(new Tengu(), Char.Property.HUMAN, "天狗兼容类");
		has(new TenguDen(), Char.Property.HUMAN, "天狗巢主");
		has(new TestMob(), Char.Property.PLANT, "训练稻草人");
		has(new TestMob2(), Char.Property.MECH, "强化训练稻草人");
		has(new VaultProtector(), Char.Property.HUMAN, "宝库守护者");

		has(new SpsPrisonMobs.Assassin(), Char.Property.HUMAN, "暗杀者");
		has(new SpsPrisonMobs.BambooMob(), Char.Property.PLANT, "竹子怪");
		has(new SpsPrisonMobs.BanditKing(), Char.Property.MINIBOSS, "强盗王");
		has(new BlueWraith(), Char.Property.UNDEAD, "蓝色怨灵");
		has(new BombBug(), Char.Property.BEAST, "炸弹虫");
		has(new DemonFlower(), Char.Property.DEMONIC, "恶魔花");
		has(new DemonGoo(), Char.Property.DEMONIC, "恶魔粘咕");
		has(new DemonRabbit(), Char.Property.DEMONIC, "恶魔兔");
		has(new DragonRider(), Char.Property.DRAGON, "龙骑士");
		has(new ExBambooMob(), Char.Property.PLANT, "竹子精");
		has(new FetidRat(), Char.Property.BEAST, "腐臭老鼠");
		has(new FireSuccubus(), Char.Property.DEMONIC, "火焰魅魔");
		has(new GnollTrickster(), Char.Property.ORC, "豺狼骗术师");
		has(new Greatmoss(), Char.Property.PLANT, "巨型苔藓");
		has(new LevelChecker(), Char.Property.MECH, "等级检测器");
		has(new ManySkeleton(), Char.Property.UNDEAD, "多重骷髅");
		has(new Musketeer(), Char.Property.DWARF, "火枪手");
		has(new RatBoss(), Char.Property.BEAST, "鼠王怪物");
		has(new RatBoss(), Char.Property.BOSS, "鼠王怪物");
		has(new RedWraith(), Char.Property.UNDEAD, "红色怨灵");
		has(new Senior(), Char.Property.DWARF, "资深武僧");
		has(new Shielded(), Char.Property.ORC, "持盾暴徒");
		has(new SpiderBot(), Char.Property.BEAST, "机械蜘蛛");
		has(new Sufferer(), Char.Property.DEMONIC, "受难者");
		has(new Sufferer(), Char.Property.MAGICER, "受难者");
		has(new Sufferer(), Char.Property.HUMAN, "受难者");
		has(new ThiefImp(), Char.Property.DEMONIC, "小偷魔鬼");
		has(new Zombie(), Char.Property.UNDEAD, "僵尸");

		has(new UAmulet(), Char.Property.UNKNOW, "护符暗影");
		has(new UDM300(), Char.Property.MECH, "终极DM300");
		has(new UDM300.SeekBomb(), Char.Property.MECH, "终极追踪炸弹");
		has(new UDM300.SeekBomb(), Char.Property.MINIBOSS, "终极追踪炸弹");
		has(new UGoo(), Char.Property.ELEMENT, "终极粘咕");
		has(new UGoo(), Char.Property.UNKNOW, "终极粘咕");
		has(new UGoo.EarthGoo(), Char.Property.ELEMENT, "终极大地粘咕");
		has(new UGoo.EarthGoo(), Char.Property.UNKNOW, "终极大地粘咕");
		has(new UGoo.EarthGoo(), Char.Property.MINIBOSS, "终极大地粘咕");
		has(new UIcecorps(), Char.Property.ORC, "终极冰兔");
		has(new UIcecorps2(), Char.Property.ORC, "终极冰兔二形态");
		has(new UKing(), Char.Property.PLANT, "终极植物王");
		UKing plantKing = new UKing();
		plantKing.HP = 100;
		plantKing.damage(100, new TaggedMob());
		check(plantKing.HP == 80, "终极植物王没有执行40%减伤和20点首领上限");
		check(plantKing.buff(AttackUp.class) != null && plantKing.buff(AttackUp.class).level() == 7,
				"终极植物王没有按旧版刷新3回合受伤增攻");
		has(new UTengu(), Char.Property.HUMAN, "终极天狗");
		has(new UYog(), Char.Property.UNKNOW, "终极古神");

		has(new Blacksmith(), Char.Property.TROLL, "铁匠");
		has(new Ghost(), Char.Property.UNDEAD, "悲伤幽灵");
		has(new ImpShopkeeper(), Char.Property.DEMONIC, "小恶魔商人");
		has(new ImpShopkeeper(), Char.Property.IMMOVABLE, "小恶魔商人");
		has(new MirrorImage(), Char.Property.UNKNOW, "镜像");
		has(new RatKing(), Char.Property.BEAST, "鼠王");
		has(new RatKing(), Char.Property.BOSS, "鼠王");
		has(new Sheep(), Char.Property.BEAST, "绵羊");
		has(new Shopkeeper(), Char.Property.HUMAN, "商店老板");
		has(new Tinkerer1(), Char.Property.HUMAN, "一层工匠");
		has(new Tinkerer2(), Char.Property.ELF, "十二层工匠");
		has(new Wandmaker(), Char.Property.HUMAN, "老杖匠");

		has(new Abi(), Char.Property.BEAST, "阿比");
		has(new Abi(), Char.Property.MINIBOSS, "阿比");
		has(new BlueGirl(), Char.Property.ELF, "蓝衣女孩");
		has(new Bunny(), Char.Property.BEAST, "宠物兔");
		has(new LeryFire(), Char.Property.ELEMENT, "火焰伙伴");
		has(new Scorpion(), Char.Property.BEAST, "宠物蝎");
		has(new YearPet(), Char.Property.BEAST, "年兽伙伴");
		has(new YearPet(), Char.Property.UNKNOW, "年兽伙伴");
	}

	private static void checkEarthGlyph() {
		GlyphEarth glyph = new GlyphEarth();
		check(glyph.immunities().contains(AcidOoze.class), "地系护甲没有免疫永久腐酸");
		check(glyph.immunities().contains(EnchantmentEarth.class), "地系护甲没有免疫地附魔");
		check(glyph.immunities().contains(EnchantmentEarth2.class), "地系护甲没有免疫强地附魔");
		check(glyph.immunities().contains(DamageType.Earth.class), "地系护甲没有免疫地元素伤害");
	}

	private static void checkPropertyTables() {
		TaggedMob orc = mob(Char.Property.ORC);
		resists(orc, Bleeding.class, "兽人流血抗性");
		immune(orc, Roots.class, "兽人扎根免疫");
		weak(orc, BeOld.class, "兽人衰老弱点");

		TaggedMob fisher = mob(Char.Property.FISHER);
		resists(fisher, WandOfFreeze.class, "水生冻结法杖抗性");
		weak(fisher, Burning.class, "水生燃烧弱点");

		TaggedMob elf = mob(Char.Property.ELF);
		resists(elf, BeOld.class, "精灵衰老抗性");
		weak(elf, Blindness.class, "精灵致盲弱点");

		TaggedMob dwarf = mob(Char.Property.DWARF);
		resists(dwarf, AcidOoze.class, "矮人腐酸抗性");
		immune(dwarf, Cripple.class, "矮人残废免疫");
		weak(dwarf, ShadowCurse.class, "矮人暗影诅咒弱点");

		weak(mob(Char.Property.TROLL), Blindness.class, "巨魔全状态弱点");

		TaggedMob demon = mob(Char.Property.DEMONIC);
		resists(demon, DamageType.Dark.class, "恶魔暗元素抗性");
		weak(demon, DamageType.Light.class, "恶魔光元素弱点");

		TaggedMob goblin = mob(Char.Property.GOBLIN);
		resists(goblin, DamageType.Shock.class, "哥布林雷元素抗性");
		weak(goblin, DamageType.Earth.class, "哥布林地元素弱点");

		TaggedMob beast = mob(Char.Property.BEAST);
		resists(beast, Wet.class, "野兽潮湿抗性");
		weak(beast, Blindness.class, "野兽致盲弱点");

		TaggedMob dragon = mob(Char.Property.DRAGON);
		resists(dragon, Burning.class, "龙类燃烧抗性");
		immune(dragon, AttackDown.class, "龙类降攻免疫");
		weak(dragon, WandOfFlow.class, "龙类流水法杖弱点");

		TaggedMob plant = mob(Char.Property.PLANT);
		resists(plant, DamageType.Light.class, "植物光元素抗性");
		immune(plant, Poison.class, "植物中毒免疫");
		weak(plant, Burning.class, "植物燃烧弱点");

		TaggedMob mech = mob(Char.Property.MECH);
		resists(mech, SlowGas.class, "机械迟缓气体抗性");
		immune(mech, Charm.class, "机械魅惑免疫");
		weak(mech, WandOfAcid.class, "机械酸液法杖弱点");

		TaggedMob undead = mob(Char.Property.UNDEAD);
		resists(undead, Frost.class, "亡灵冰冻抗性");
		immune(undead, HealLight.class, "亡灵治疗光免疫");
		weak(undead, WandOfLight.class, "亡灵光明法杖弱点");

		resists(mob(Char.Property.ALIEN), Blindness.class, "异星全状态抗性");
	}

	private static void checkWeaknessDuration() {
		TaggedMob troll = mob(Char.Property.TROLL);
		TestFlavour buff = Buff.affect(troll, TestFlavour.class, 10f);
		check(Math.abs(buff.cooldown() - 15f) < 0.001f,
				"弱点没有把10回合状态延长到15回合: " + buff.cooldown());
	}

	private static TaggedMob mob(Char.Property property) {
		return new TaggedMob().with(property);
	}

	private static void resists(Char target, Class<?> effect, String label) {
		check(target.resist(effect) == 0.5f, label + "错误: " + target.resist(effect));
	}

	private static void immune(Char target, Class<?> effect, String label) {
		check(target.isImmune(effect), label + "缺失");
	}

	private static void weak(Char target, Class<?> effect, String label) {
		check(target.weak(effect) == 1.5f, label + "错误: " + target.weak(effect));
	}

	private static void has(Char target, Char.Property property, String label) {
		check(target.properties().contains(property), label + "缺少旧版" + property + "属性");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	public static final class TestFlavour extends FlavourBuff { }

	private static final class TestIcecorps extends UIcecorps {
		Mob summonedFireRabbit() {
			return createFireRabbit();
		}
	}

	private static final class TestUGoo extends UGoo {
		int spawnCount;
		boolean livingMinions;

		void triggerPhase() {
			onPhaseChanged(1);
		}

		void setFinalPhase() {
			breaks = 3;
		}

		@Override
		protected void spawnMinion(Mob minion) {
			spawnCount++;
		}

		@Override
		protected boolean hasLivingMinions() {
			return livingMinions;
		}
	}

	private static final class TestUDM300 extends UDM300 {
		void setPhase(int phase) {
			breaks = phase;
		}

		Class<?> phaseBlob() {
			return activePhaseBlob();
		}

		int phaseVolume() {
			return activePhaseVolume();
		}
	}

	private static final class TestSeekBomb extends UDM300.SeekBomb {
		DungeonBomb bomb() {
			return createBomb();
		}
	}

	private static final class TaggedMob extends Mob {
		TaggedMob with(Char.Property property) {
			properties.add(property);
			return this;
		}
	}

	private SpsLegacyPropertiesTest() { }
}
