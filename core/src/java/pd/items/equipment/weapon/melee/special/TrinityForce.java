/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.TrinityStance;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: SPS 0.9.8 之外原创武器——三相之力。
 *
 * <p>由六把飞刃组成的武器组：一次挥击的伤害总额被拆成三次独立攻击（命中与效果各自结算），
 * 但只消耗一个回合。为维持操控飞刃的战舞，装备期间移速固定降低到原先的四分之三。</p>
 */
public class TrinityForce extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TrinityForce.class)
			.t("name", "三相之力")
			.t("desc", "六把飞刃组成的一套武器组，极难操控：只有以战舞驾驭它，六刃才会同时起舞。\n\n一次挥击的伤害被拆成三次独立结算，命中与附带效果分别计算，但总共只消耗一个回合。\n\n为维持操控飞刃的战舞，你的移速会降低到原先的四分之三。\n\n冲锋姿态下命中敌人时会顺势贴到对方身边（无论多远，只要这一击打得到）。")
			.t("ac_defend", "防御姿态")
			.t("enter_defend", "你沉入防御姿态，六刃环绕如盾。")
			.t("defend_unavailable", "你暂时无法进入防御姿态。");
	}

	public static final String AC_DEFEND = "DEFEND";

	/** 这套武器组的飞刃总数：六把。 */
	public static final int BLADES = 6;
	/** 一次挥击把这六把飞刃分三次挥出，伤害也按三次独立结算。 */
	public static final int STRIKES = 3;

	{
		unique = true;
		reinforced = true;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;
		usesTargeting = true;
	}

	public TrinityForce() {
		super(5, 1f, 1f, 2, 8, 20, SpecificPlaceHolderDict.SPS_PH_WEAPON);
	}

	@Override
	public Item random() {
		return this;
	}

	//SPSEXPD: 不设默认动作——三相之力的每个动作都有各自的价值，
	//放进快捷栏后点击应打开动作列表，而不是直接执行某一个动作
	@Override
	public String defaultAction() {
		return null;
	}

	@Override
	public boolean canQuickSlot() {
		return true;
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (ch != null) Buff.affect(ch, TrinityStance.class);
	}

	@Override
	protected float speedMultiplier(Char owner) {
		float multi = super.speedMultiplier(owner);
		TrinityStance stance = TrinityStance.of(owner);
		return stance == null ? multi : stance.attackSpeedMultiplier(multi);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (!isEquipped(hero)) return actions;
		TrinityStance stance = TrinityStance.of(hero);
		if (stance != null && stance.canDefend()) actions.add(AC_DEFEND);
		return actions;
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (AC_DEFEND.equals(action)) return Messages.get(this, "ac_defend");
		return super.actionName(action, hero);
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_DEFEND.equals(action)) {
			TrinityStance stance = TrinityStance.of(hero);
			if (stance != null && stance.enterDefend()) {
				GLog.i(Messages.get(this, "enter_defend"));
				hero.spendAndNext(1f);
			} else {
				GLog.i(Messages.get(this, "defend_unavailable"));
			}
		} else {
			super.execute(hero, action);
		}
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		TrinityStance stance = TrinityStance.of(attacker);
		if (stance != null) stance.onAttack();
		if (defender == null) return super.proc(attacker, defender, damage);

		int total = Math.max(0, damage);
		if (total <= 0) return super.proc(attacker, defender, damage);

		//SPSEXPD: 伤害总额拆成三次独立结算（六把飞刃两两一组）；第 1 次沿用引擎已完成的那次命中判定，
		//其伤害由外层 Char.attack 结算，其余两次在这里独立判定并立即结算。
		int per = total / STRIKES;
		int remainder = total % STRIKES;

		//SPSEXPD: 先贴近、再结算这一击的伤害（视觉与逻辑都是「冲上去砍」）
		dashTo(attacker, defender);

		int first = super.proc(attacker, defender, per + (remainder > 0 ? 1 : 0));

		//SPSEXPD: 第 1 次已足以击杀时，剩余两次不必再挥出
		if (defender.HP > first) {
			for (int strike = 1; strike < STRIKES && defender.isAlive(); strike++) {
				int amount = per + (strike < remainder ? 1 : 0);
				if (amount <= 0) continue;
				if (!Char.hit(attacker, defender, false)) continue;
				amount = super.proc(attacker, defender, amount);
				if (amount <= 0) continue;
				defender.damage(amount, this);
				//SPSEXPD: 三次结算各自独立命中，每一次都要播一次命中音效
				//（第 1 次由外层 Char.attack 播放，这里补上追加的两次）
				hitSound(Random.Float(0.87f, 1.15f));
			}
		}

		return first;
	}

	/**
	 * SPSXPD: 从 from 朝 to 方向跨一格的落点（八向）。
	 * 不能用「位置差 == 八向偏移」来判断方向：那只在相邻格成立，距离两格以上时永远匹配不上
	 * （旧实现正是如此，导致接近效果几乎从不触发）。调用方负责判断落点是否在 map 内、是否可通行。
	 */
	public static int approachStep(int from, int to, int width) {
		int dx = Integer.compare(to % width, from % width);
		int dy = Integer.compare(to / width, from / width);
		return from + dy * width + dx;
	}

	/**
	 * 命中后朝对方贴过去，一路走到与它相邻为止（距离多远都一样，例如用精准戒指提高攻击距离时）；
	 * 路上被单位或地形挡住就停在能走到的最远处。
	 */
	private void dashTo(Char attacker, Char defender) {
		if (!(attacker instanceof Hero) || Dungeon.level == null) return;
		Hero hero = (Hero) attacker;
		//SPSEXPD: 接近只在冲锋姿态生效；防御姿态原地格挡，不移动
		TrinityStance stance = TrinityStance.of(hero);
		if (stance != null && stance.defending()) return;
		if (!Dungeon.level.insideMap(hero.pos) || !Dungeon.level.insideMap(defender.pos)) return;
		if (Dungeon.level.distance(hero.pos, defender.pos) <= 1) return;

		int width = Dungeon.level.width();
		int landing = hero.pos;
		while (Dungeon.level.distance(landing, defender.pos) > 1) {
			int next = approachStep(landing, defender.pos, width);
			if (next == landing || !Dungeon.level.insideMap(next)) break;
			if (Actor.findChar(next) != null || !Dungeon.level.passable[next]) break;
			landing = next;
		}
		if (landing == hero.pos) return;

		//SPSEXPD: 用与正常移动相同的方式位移——先起移动动画（参数是起点与终点，必须在 pos 更新前调），
		//再改逻辑位置；直接 place 会变成瞬移
		if (hero.sprite != null) hero.sprite.move(hero.pos, landing);
		hero.move(landing, false);
		Dungeon.level.pressCell(landing);
		Dungeon.observe();
	}

}
