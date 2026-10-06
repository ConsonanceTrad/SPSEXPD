/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.mobs.pets.BlueDragon;
import pd.actors.mobs.pets.BlueGirl;
import pd.actors.mobs.pets.BugDragon;
import pd.actors.mobs.pets.GoldDragon;
import pd.actors.mobs.pets.GreenDragon;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.LeryFire;
import pd.actors.mobs.pets.LightDragon;
import pd.actors.mobs.pets.RedDragon;
import pd.actors.mobs.pets.Scorpion;
import pd.actors.mobs.pets.ShadowDragon;
import pd.actors.mobs.pets.VioletDragon;
import pd.effects.Pushing;
import pd.items.Item;
import pd.items.equipment.artifacts.Artifact;
import pd.items.consum.eggs.randomone.RandomEgg;
import pd.items.specific.sellitem.VIPcard;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import pd.messages.InlineText;
import pd.atlas.items.ConsumSummorDict;

/** The original SPS mob soul, whose absorbed energies determine its hatchling. */
public class Egg extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Egg.class)
			.t("name", "魔物之魂")
			.t("desc", "一个怪物的灵魂。根据吸收能量的不同，产生的结果也会不同。")
			.t("ac_break", "召唤")
			.t("ac_summon", "召唤")
			.t("ac_feed", "喂食")
			.t("ac_dismiss", "遣散")
			.t("ac_cast", "技能")
			.t("ac_command", "指挥")
			.t("ac_break_ring", "炸环")
			.t("ac_sacrifice", "献祭")
			.t("sacrificed", "你献祭了魂石，永久获得特质：%s")
			.t("no_ability", "这颗魂石没有对应的能力特质。")
			.t("one_stone", "你只能装备一颗魂石。")
			.t("broken", "魂石炸裂，你的下一次攻击将额外造成 %d 点伤害。")
			.t("broken_no", "这颗魂石还没有成形，无法炸环。")
			.t("prompt_command", "选择指挥目标：点敌人攻击、点自己跟随、点空地驻守。")
			.t("prompt_feed", "选择它喜欢的食物。")
			.t("nofood_left", "你没有可以喂给魂石的食物。")
			.t("nofood_pref", "它对你身上的食物没有兴趣。")
			.t("prompt_feed_any", "这个灵魂还没有偏好，选择要喂的食物来塑形。")
			.t("ability_cd", "宠物的技能还在冷却中。")
			.t("noability", "这个宠物没有可主动释放的技能。")
			.t("prevent", "这里不是尝试召唤它的最佳地点。")
			.t("notready", "你的宠物还没有准备好和其他宠物相处。")
			.t("yolk", "一些能量四下流散。")
			.t("hatch", "新的宠物诞生！")
			.t("warmhome", "这个灵魂在你温暖的背包里吸收能量。")
			.t("onlyone", "只有一个灵魂能在背包里面吸收能量。")
			.t("moves", "无属性：%d")
			.t("burns", "火属性：%d")
			.t("freezes", "冰属性：%d")
			.t("poisons", "地属性：%d")
			.t("lits", "雷属性：%d")
			.t("darks", "暗属性：%d")
			.t("lights", "光属性：%d")
			.t("summoned", "魂石亮起，你的宠物现身了。")
			.t("nodestiny", "这个灵魂还没有成形，无法召唤。")
			.t("hp", "生命：%d")
			.t("lvl", "培养等级：%d")
			.t("fed_count", "喂食次数：%d")
			.t("energy_low", "这个灵魂还没有攒够能量，无法召唤。")
			.t("gate_ok", "能量已足够，可以召唤了。")
			.t("gate_no", "能量还不足以召唤，继续喂食来积攒。");
	}




	public static final String AC_BREAK = "BREAK";
	//SPSXPD: 魂石动作（原「召唤」拆为 召唤 / 喂食 / 遣散）
	public static final String AC_SUMMON = "SUMMON";
	public static final String AC_FEED = "FEED";
	public static final String AC_DISMISS = "DISMISS";
	public static final String AC_CAST = "CAST";
	//SPSXPD: 指挥 —— 选一个目标格，控制宠物 移动/跟随/攻击
	public static final String AC_COMMAND = "COMMAND";
	//SPSXPD: 炸环（驯兽大师）—— 消耗魂石换取一次性攻击增幅
	public static final String AC_BREAK_RING = "BREAK_RING";
	//SPSXPD: 献祭（驯兽大师 2 级）—— 消耗魂石换取该生物的能力特质
	public static final String AC_SACRIFICE = "SACRIFICE";
	public static final int VIP_DROP_DENOMINATOR = 10;
	//SPSXPD: 主动遣散后的复活冷却（回合数）
	public static final float DISMISS_COOLDOWN = 5f;
	private static final float TIME_TO_USE = 1f;

	//SPSXPD: 魂石携带的宠物数据（投影本身不入档，这些数据入档）
	public int petHp = 0;
	public int petLevel = 0;
	public int feedCount = 0;
	public float reviveAtTurn = 0f;
	/** SPSXPD: 是否已首次召唤成型（固化后魂石固定给出同一种生物） */
	public boolean formed = false;
	/** SPSXPD: 固化后的宠物类名 */
	public String formedPet = null;

	public int moves;
	public int burns;
	public int freezes;
	public int poisons;
	public int lits;
	public int darks;
	public int lights;

	{
		image = ConsumSummorDict.RANDOM_SOUL;
		stackable = false;
		//SPSXPD: 不设 defaultAction —— 魂石是多行为工具，每个行为都有价值，
		//快捷栏点击应打开详情页而不是直接执行某个动作（见 QuickSlotButton）
	}

	@Override
	public boolean doEquip(final Hero hero) {
		//SPSXPD: 魂石只能装备一颗 —— 饰品槽（artifact/misc/ring）里已有别的魂石时直接阻止
		pd.items.KindofMisc[] miscs = {hero.belongings.artifact, hero.belongings.misc, hero.belongings.ring};
		for (pd.items.KindofMisc m : miscs) {
			if (m instanceof Egg && m != this) {
				GLog.w(Messages.get(this, "one_stone"));
				return false;
			}
		}
		return super.doEquip(hero);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		//SPSXPD: 魂石脱离后不在这里收回投影 —— 投影会因"魂石不在神器位"
		//而在每回合受 7% 纯粹伤害逐渐消散（见 LegacyPet.act）。
		//只有炸环 / 献祭会直接让投影消失。
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero)) {
			LegacyPet pet = LegacyPet.active();
			if (pet == null) {
				//SPSXPD: 未孵化 —— 可以召唤（能量够时），也可以喂食来积攒能量
				if (canSummon(hero)) actions.add(AC_SUMMON);
			} else {
				//SPSXPD: 已孵化 —— 指挥（选目标格控制 移动/跟随/攻击）与技能（CD 没好也显示）
				actions.add(AC_COMMAND);
				actions.add(AC_CAST);
			}
			actions.add(AC_FEED);
			if (pet != null) actions.add(AC_DISMISS);
			//SPSXPD: 驯兽大师 2 级解锁「献祭」、3 级解锁「炸环」（不要求在场的投影，只看魂石本身）
			pd.actors.hero.perks.BeastMaster bm = hero.heroPerk.get(pd.actors.hero.perks.BeastMaster.class);
			if (bm != null && hatchling() != null) {
				if (bm.level() >= 2) actions.add(AC_SACRIFICE);
				if (bm.level() >= 3) actions.add(AC_BREAK_RING);
			}
		}
		return actions;
	}

	/** SPSXPD: 复活冷却是否结束（按回合数计时） */
	public boolean canSummon(Hero hero) {
		return LegacyPet.active() == null && render.noosa.Game.timeTotal >= reviveAtTurn;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_FEED.equals(action)) { feed(hero); return; }
		if (AC_DISMISS.equals(action)) { dismiss(hero); return; }
		if (AC_SACRIFICE.equals(action)) {
			sacrifice(hero);
			return;
		}
		if (AC_BREAK_RING.equals(action)) {
			breakRing(hero);
			return;
		}
		if (AC_COMMAND.equals(action)) {
			//SPSXPD: 选一个目标格来指挥宠物（点敌人=攻击、点自己=跟随、点空地=驻守）
			final LegacyPet commanded = LegacyPet.active();
			if (commanded == null) return;
			pd.scenes.GameScene.selectCell(new pd.scenes.CellSelector.Listener() {
				@Override
				public void onSelect(Integer cell) {
					if (cell != null) commanded.directTocell(cell);
				}
				@Override
				public String prompt() {
					return Messages.get(Egg.class, "prompt_command");
				}
			});
			return;
		}
		if (AC_CAST.equals(action)) {
			LegacyPet pet = LegacyPet.active();
			if (pet == null) return;
			if (!pet.hasAbility()) {
				GLog.w(Messages.get(this, "noability"));
				return;
			}
			if (!pet.castAbilityAvailable()) {
				GLog.w(Messages.get(this, "ability_cd"));
				return;
			}
			if (pet.castAbility()) hero.spendAndNext(TIME_TO_USE);
			return;
		}
		if (AC_SUMMON.equals(action)) action = AC_BREAK; // 沿用原召唤实现
		if (!AC_BREAK.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (LegacyPet.active() != null) {
			GLog.w(Messages.get(this, "notready"));
			return;
		}
		LegacyPet pet = hatchling();
		if (pet == null) {
			//SPSXPD: 魂石不会被消耗，灵魂尚未成形时只是召唤不出东西
			GLog.w(Messages.get(this, "nodestiny"));
			return;
		}
		if (!formed && !energyReady()) {
			//SPSXPD: 首次召唤需要攒够门槛能量
			GLog.w(Messages.get(this, "energy_low"));
			return;
		}
		int spawn = spawnCell(hero.pos);
		if (spawn < 0) {
			GLog.w(Messages.get(this, "prevent"));
			return;
		}
		pet.updateStats(true);
		if (petHp > 0) pet.HP = Math.min(petHp, pet.HT); //SPSXPD: 继承魂石里保存的血量
		pet.markProjection(this); //SPSXPD: 标记为投影（不入档；死亡回写魂石）
		pet.pos = spawn;
		pet.state = pet.HUNTING;
		GameScene.add(pet);
		Actor.add(new Pushing(pet, hero.pos, spawn));
		//SPSXPD: 魂石是装备，不会被消耗；首次成功后成型固化
		formed = true;
		formedPet = pet.getClass().getName();
		GLog.p(Messages.get(this, "summoned"));
		hero.spendAndNext(TIME_TO_USE);
	}

	protected Item failedHatchReward() {
		return moves >= 100 ? new RandomEgg() : null;
	}

	private void dropBreakBonus(Hero hero) {
		if (Random.Int(VIP_DROP_DENOMINATOR) == 0) Dungeon.level.drop(new VIPcard(), hero.pos).sprite.drop();
	}

	//SPSXPD: 所有魂石都有首次召唤门槛（能量高低不一），达标后成型固化
	protected LegacyPet hatchling() {
		//已固化的魂石直接给出对应宠物
		if (formedPet != null) {
			try {
				return (LegacyPet) Class.forName(formedPet).getDeclaredConstructor().newInstance();
			} catch (Exception ex) {
				return null;
			}
		}
		if (allEnergies(20) && moves >= 200) {
			return Calendar.getInstance().get(Calendar.MONTH) == Calendar.SEPTEMBER || Random.Int(50) == 0
					? new BugDragon() : new GoldDragon();
		}
		if (poisons >= 30 && lights >= 66 && lights <= 122) return new BlueGirl();
		if (allEnergies(5) && moves >= 50) return new LeryFire();
		if (lights >= 20) return new ShadowDragon();
		if (freezes >= 20) return new BlueDragon();
		if (darks >= 20) return new LightDragon();
		if (poisons >= 20) return new VioletDragon();
		if (lits >= 20) return new GreenDragon();
		if (burns >= 20) return new RedDragon();
		if (moves >= 200) return new Scorpion();
		return null;
	}

	private boolean allEnergies(int v) {
		return burns >= v && freezes >= v && poisons >= v && lits >= v && darks >= v && lights >= v;
	}

	/** SPSXPD: 首次召唤的能量门槛是否达标（按这颗魂石会给出的宠物种类判定） */
	public boolean energyReady() {
		LegacyPet pet = hatchling();
		if (pet == null) return false;
		//按 legacyType 判定（kind() 是 protected，跨包不可见）
		switch (pet.legacyType()) {
			case 509: // GOLD_DRAGON
			case 510: return allEnergies(20) && moves >= 200;       // BUG_DRAGON
			case 508: return allEnergies(5) && moves >= 50;         // LERY_FIRE
			case 601: return poisons >= 30 && lights >= 66 && lights <= 122; // BLUE_GIRL
			case 504: return burns >= 20;                           // RED_DRAGON
			case 501: return freezes >= 20;                         // BLUE_DRAGON
			case 502: return lits >= 20;                            // GREEN_DRAGON
			case 506: return poisons >= 20;                         // VIOLET_DRAGON
			case 503: return darks >= 20;                           // LIGHT_DRAGON
			case 505: return lights >= 20;                          // SHADOW_DRAGON
			case 507: return moves >= 200;                          // SCORPION
			default: return moves >= 10;
		}
	}

	/** SPSXPD: 食材 -> 属性能量（按元素主题）。返回 {无,火,冰,地,雷,暗,光} */
	public static int[] energyOf(pd.items.Item food) {
		Class<?> c = food.getClass();
		if (c == pd.items.consum.food.meatfood.FireMeat.class
				|| c == pd.items.consum.food.vegetable.Chili.class) return new int[]{0, 5, 0, 0, 0, 0, 0};
		if (c == pd.items.consum.food.meatfood.IceMeat.class
				|| c == pd.items.consum.food.vegetable.IceMint.class) return new int[]{0, 0, 5, 0, 0, 0, 0};
		if (c == pd.items.consum.food.meatfood.EarthMeat.class
				|| c == pd.items.consum.food.vegetable.ToxicEggplant.class) return new int[]{0, 0, 0, 5, 0, 0, 0};
		if (c == pd.items.consum.food.meatfood.ShockMeat.class) return new int[]{0, 0, 0, 0, 5, 0, 0};
		if (c == pd.items.consum.food.meatfood.LightMeat.class
				|| c == pd.items.consum.food.vegetable.Sunflower.class
				|| c == pd.items.consum.food.vegetable.StarEaterFlower.class) return new int[]{0, 0, 0, 0, 0, 0, 5};
		if (c == pd.items.consum.food.meatfood.DarkMeat.class
				|| c == pd.items.consum.food.vegetable.DreamLeaf.class) return new int[]{0, 0, 0, 0, 0, 5, 0};
		if (food instanceof pd.items.consum.food.completefood.CompleteFood) return new int[]{1, 1, 1, 1, 1, 1, 1};
		return new int[]{3, 0, 0, 0, 0, 0, 0};
	}

	/** SPSXPD: 装备在神器位的那颗魂石（能量吸魂只针对装备中的魂石） */
	public static Egg equipped() {
		if (Dungeon.hero == null) return null;
		//魂石可能落在 artifact/misc/ring 任一空槽
		pd.items.KindofMisc[] miscs = {Dungeon.hero.belongings.artifact,
				Dungeon.hero.belongings.misc, Dungeon.hero.belongings.ring};
		for (pd.items.KindofMisc m : miscs) {
			if (m instanceof Egg) return (Egg) m;
		}
		return null;
	}

	protected int spawnCell(int center) {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = center + offset;
			if (cell >= 0 && cell < Dungeon.level.length()
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])
					&& Actor.findChar(cell) == null) cells.add(cell);
		}
		return cells.isEmpty() ? -1 : Random.element(cells);
	}

	//SPSXPD: 喂食 —— 消耗宠物喜爱的食物提升培养度
	private void feed(final Hero hero) {
		LegacyPet active = LegacyPet.active();
		//在场的投影按它自己的偏好；未召唤时按这颗魂石将要给出的宠物偏好
		LegacyPet eater = (active != null && active.stone == this) ? active : hatchling();
		//SPSXPD: 未成形的魔物之魂还没有确定偏好，允许喂任何食物来塑形
		final boolean anyFood = (eater == null);
		final LegacyPet eaterFinal = eater;

		boolean hasFood = false;
		for (pd.items.Item it : hero.belongings) {
			if (it instanceof pd.items.consum.food.Food && (anyFood || eater.lovefood(it))) { hasFood = true; break; }
		}
		if (!hasFood) { GLog.w(Messages.get(this, anyFood ? "nofood_left" : "nofood_pref")); return; }

		//SPSXPD: 由玩家挑选要喂的食物（只列出它喜欢的；不同食材按元素主题给不同属性能量）
		pd.scenes.GameScene.selectItem(new pd.windows.WndBag.ItemSelector() {
			@Override
			public String textPrompt() {
				return Messages.get(Egg.this, anyFood ? "prompt_feed_any" : "prompt_feed");
			}
			@Override
			public boolean itemSelectable(pd.items.Item item) {
				if (!(item instanceof pd.items.consum.food.Food)) return false;
				return anyFood || eaterFinal.lovefood(item);
			}
			@Override
			public void onSelect(pd.items.Item item) {
				if (item instanceof pd.items.consum.food.Food && (anyFood || eaterFinal.lovefood(item))) {
					feedWith(hero, item);
				}
			}
		});
	}

	/** SPSXPD: 用玩家选定的食物喂食 */
	private void feedWith(Hero hero, pd.items.Item food) {
		food.detach(hero.belongings.backpack);
		//按食材的元素主题积攒属性能量
		int[] gain = energyOf(food);
		moves += gain[0]; burns += gain[1]; freezes += gain[2];
		poisons += gain[3]; lits += gain[4]; darks += gain[5]; lights += gain[6];
		//培养度：宠物口粮额外提升等级
		feedCount++;
		if (food instanceof pd.items.consum.food.completefood.PetFood) petLevel++;
		else petLevel = Math.max(petLevel, feedCount / 3);
		GLog.p(Messages.get(this, "fed", petLevel));
		hero.spendAndNext(TIME_TO_USE);
	}

	private boolean lovefoodOfActive(pd.items.Item it) {
		LegacyPet pet = LegacyPet.active();
		return pet != null && pet.lovefood(it);
	}

	/** SPSXPD: 把在场投影收回魂石（记录血量），替代旧的 PocketBallFull.removePet */
	public static boolean recallProjection(Hero hero) {
		LegacyPet pet = LegacyPet.active();
		if (pet == null) return false;
		//SPSXPD: 优先回写到召唤它的那颗魂石，其次找英雄身上的魂石
		Egg stone = pet.stone;
		if (stone == null && hero != null) stone = hero.belongings.getItem(Egg.class);
		if (stone != null) {
			stone.petHp = Math.max(1, pet.HP);
			stone.reviveAtTurn = 0f;
		}
		pet.dismiss();
		return true;
	}

	//SPSXPD: 献祭 —— 消耗魂石，永久获得该生物的能力特质（驯兽大师 2 级）
	private void sacrifice(Hero hero) {
		LegacyPet source = hatchling();
		if (source == null) {
			GLog.w(Messages.get(this, "broken_no"));
			return;
		}
		pd.actors.hero.perks.Perk ability = petAbilityOf(source);
		if (ability == null) {
			GLog.w(Messages.get(this, "no_ability"));
			return;
		}
		hero.heroPerk.grantIfMissing(ability, hero);
		//献祭会消耗掉这颗魂石，投影一同消失
		LegacyPet owned = LegacyPet.active();
		detach(hero.belongings.backpack);
		if (owned != null && owned.stone == this) owned.dismiss();
		GLog.p(Messages.get(this, "sacrificed", ability.title()));
		hero.spendAndNext(TIME_TO_USE);
	}

	/** SPSXPD: 生物 -> 该生物的能力特质 */
	public static pd.actors.hero.perks.Perk petAbilityOf(LegacyPet pet) {
		if (pet == null) return null;
		switch (pet.legacyType()) {
			case 101: return new pd.actors.hero.perks.pets.KodoraAbility();
			case 104: return new pd.actors.hero.perks.pets.SnakeAbility();
			case 103: return new pd.actors.hero.perks.pets.RibbonRatAbility();
			case 102: return new pd.actors.hero.perks.pets.GentleCrabAbility();
			case 201: return new pd.actors.hero.perks.pets.DogAbility();
			case 202: return new pd.actors.hero.perks.pets.ChocoboAbility();
			case 203: return new pd.actors.hero.perks.pets.FlyAbility();
			case 204: return new pd.actors.hero.perks.pets.SpiderAbility();
			case 205: return new pd.actors.hero.perks.pets.StoneAbility();
			case 206: return new pd.actors.hero.perks.pets.DwarfBoyAbility();
			case 304: return new pd.actors.hero.perks.pets.ButterflyAbility();
			case 302: return new pd.actors.hero.perks.pets.MonkeyAbility();
			case 303: return new pd.actors.hero.perks.pets.PigAbility();
			case 301: return new pd.actors.hero.perks.pets.DaturaAbility();
			case 305: return new pd.actors.hero.perks.pets.FoxHelperAbility();
			case 306: return new pd.actors.hero.perks.pets.FrogAbility();
			case 105: return new pd.actors.hero.perks.pets.LitDemonAbility();
			case 106: return new pd.actors.hero.perks.pets.StarKidAbility();
			case 405: return new pd.actors.hero.perks.pets.AbiAbility();
			case 403: return new pd.actors.hero.perks.pets.HaroAbility();
			case 402: return new pd.actors.hero.perks.pets.CocoCatAbility();
			case 404: return new pd.actors.hero.perks.pets.VelociroosterAbility();
			case 401: return new pd.actors.hero.perks.pets.BunnyAbility();
			case 666: return new pd.actors.hero.perks.pets.YearAbility();
			case 504: return new pd.actors.hero.perks.pets.RedDragonAbility();
			case 501: return new pd.actors.hero.perks.pets.BlueDragonAbility();
			case 502: return new pd.actors.hero.perks.pets.GreenDragonAbility();
			case 506: return new pd.actors.hero.perks.pets.VioletDragonAbility();
			case 503: return new pd.actors.hero.perks.pets.DarkDragonAbility();
			case 505: return new pd.actors.hero.perks.pets.LightDragonAbility();
			case 509: return new pd.actors.hero.perks.pets.GoldDragonAbility();
			case 510: return new pd.actors.hero.perks.pets.BugDragonAbility();
			case 601: return new pd.actors.hero.perks.pets.BlueGirlAbility();
			case 508: return new pd.actors.hero.perks.pets.LeryFireAbility();
			case 507: return new pd.actors.hero.perks.pets.ScorpionAbility();
			default: return null;
		}
	}

	private void breakRing(Hero hero) {
		LegacyPet source = hatchling();
		if (source == null) {
			GLog.w(Messages.get(this, "broken_no"));
			return;
		}
		int boost = strikeBoost(source, petLevel);
		pd.actors.buffs.PhysicalEmpower emp =
				pd.actors.buffs.Buff.affect(hero, pd.actors.buffs.PhysicalEmpower.class);
		emp.set(boost, 1);
		//炸环会消耗掉这颗魂石，投影一同消失
		LegacyPet owned = LegacyPet.active();
		detach(hero.belongings.backpack);
		if (owned != null && owned.stone == this) owned.dismiss();
		GLog.p(Messages.get(this, "broken", boost));
		hero.spendAndNext(TIME_TO_USE);
	}

	/** SPSXPD: 炸环的增幅量 —— 按生物种类分档，并随培养程度提升 */
	public static int strikeBoost(LegacyPet pet, int petLevel) {
		int base;
		switch (pet.legacyType()) {
			case 509: case 510: base = 24; break;                   // 金龙 / 虫龙
			case 501: case 502: case 503:
			case 504: case 505: case 506: base = 18; break;         // 六系龙
			case 507: case 508: case 601: base = 20; break;         // 蝎子 / 火莲妖 / 蓝女
			default: base = 10;                                     // 普通宠物
		}
		return base + base * petLevel / 4;
	}

	private void dismiss(Hero hero) {
		if (recallProjection(hero)) {
			//SPSXPD: 主动遣散也要等冷却，不能立刻再召
			reviveAtTurn = render.noosa.Game.timeTotal + DISMISS_COOLDOWN;
			GLog.p(Messages.get(this, "dismissed", (int)DISMISS_COOLDOWN));
			hero.spendAndNext(TIME_TO_USE);
		}
	}

	static {
		InlineText.of(Egg.class)
			.t("nofood", "没有适合喂食它的食物。")
			.t("fed", "魂石中的宠物培养度提升了（等级 %d）。")
			.t("dismissed", "你遣散了宠物，它的灵魂回到了魂石之中（%d 回合后可再召唤）。")
			.t("reviving", "魂石里的灵魂尚未复原，还需约 %d 回合。");
	}

	public static boolean petHomeDepth() {
		return Dungeon.legacyDepth() == 50;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		GLog.w(Messages.get(this, "warmhome"));
		if (hero.belongings.getItem(Egg.class) != null) GLog.w(Messages.get(this, "onlyone"));
		return super.doPickUp(hero, pos);
	}

	public static Egg carried() {
		return Dungeon.hero == null ? null : Dungeon.hero.belongings.getItem(Egg.class);
	}

	@Override
	public String info() {
		StringBuilder text = new StringBuilder(desc());
		//SPSXPD: 魂石数据（生命 / 培养等级 / 喂食次数 / 复活冷却）
		if (petHp > 0) text.append("\n\n").append(Messages.get(this, "hp", petHp));
		if (petLevel > 0) text.append("\n").append(Messages.get(this, "lvl", petLevel));
		if (feedCount > 0) text.append("\n").append(Messages.get(this, "fed_count", feedCount));
		if (render.noosa.Game.timeTotal < reviveAtTurn) {
			text.append("\n").append(Messages.get(this, "reviving",
					(int)Math.ceil(reviveAtTurn - render.noosa.Game.timeTotal)));
		}
		//门槛进度
		if (!formed) text.append("\n").append(Messages.get(this, energyReady() ? "gate_ok" : "gate_no"));
		//能量（供喂养攒能量时参考）
		return text.append("\n\n").append(Messages.get(this, "moves", moves))
				.append("\n").append(Messages.get(this, "burns", burns))
				.append("\n").append(Messages.get(this, "freezes", freezes))
				.append("\n").append(Messages.get(this, "poisons", poisons))
				.append("\n").append(Messages.get(this, "lits", lits))
				.append("\n").append(Messages.get(this, "darks", darks))
				.append("\n").append(Messages.get(this, "lights", lights))
				.toString();
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }

	/** SPSXPD: 把宠物等级显示在物品图标的右上角 */
	@Override
	public String slotLevelText() {
		return petLevel > 0 ? "Lv" + petLevel : null;
	}
	@Override public int value() { return 50 * quantity; }

	private static final String MOVES = "moves";
	private static final String BURNS = "burns";
	private static final String FREEZES = "freezes";
	private static final String POISONS = "poisons";
	private static final String LITS = "lits";
	private static final String DARKS = "darks";
	private static final String LIGHTS = "lights";
	private static final String PET_HP = "pet_hp";
	private static final String PET_LEVEL = "pet_level";
	private static final String FOOD_COUNT = "feed_count";
	private static final String REVIVE_AT = "revive_at";
	private static final String FORMED = "formed";
	private static final String FORMED_PET = "formed_pet";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(MOVES, moves); bundle.put(BURNS, burns); bundle.put(FREEZES, freezes);
		bundle.put(POISONS, poisons); bundle.put(LITS, lits); bundle.put(DARKS, darks); bundle.put(LIGHTS, lights);
		bundle.put(PET_HP, petHp); bundle.put(PET_LEVEL, petLevel);
		bundle.put(FORMED, formed); if (formedPet != null) bundle.put(FORMED_PET, formedPet);
		bundle.put(FOOD_COUNT, feedCount); bundle.put(REVIVE_AT, reviveAtTurn);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		moves = bundle.getInt(MOVES); burns = bundle.getInt(BURNS); freezes = bundle.getInt(FREEZES);
		poisons = bundle.getInt(POISONS); lits = bundle.getInt(LITS); darks = bundle.getInt(DARKS); lights = bundle.getInt(LIGHTS);
		petHp = bundle.getInt(PET_HP); petLevel = bundle.getInt(PET_LEVEL);
		feedCount = bundle.getInt(FOOD_COUNT); reviveAtTurn = bundle.getFloat(REVIVE_AT);
		formed = bundle.getBoolean(FORMED); formedPet = bundle.getString(FORMED_PET);
	}
}
