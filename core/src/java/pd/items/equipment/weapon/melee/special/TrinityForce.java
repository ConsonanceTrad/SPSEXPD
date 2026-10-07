/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Slow;
import pd.actors.buffs.TrinityStance;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.mechanics.Ballistica;
import pd.mechanics.ConeAOE;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: SPS 0.9.8 之外原创武器——三相之力。
 *
 * <p>由七把飞刃组成的武器组：一次挥击的伤害总额被拆成七次独立攻击（命中与效果各自结算），
 * 但只消耗一个回合。为维持操控飞刃的战舞，装备期间移速固定降低到原先的四分之三。</p>
 */
public class TrinityForce extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TrinityForce.class)
			.t("name", "三相之力")
			.t("desc", "七把飞刃组成的一套武器组，极难操控：只有以战舞驾驭它，七刃才会同时起舞。为维持操控飞刃的战舞，你的移速会降低到原先的四分之三。\n\n能够使用冲锋或防御姿态进行迎敌。\n\n先锋之刃：向指定方向挥出 60° 扇形（5 格），使掠过的敌人减速 5 回合，施放后退出防御姿态。")
			.t("ac_defend", "防御姿态")
			.t("ac_vanguard", "先锋之刃")
			.t("enter_defend", "你沉入防御姿态，七刃环绕如盾。")
			.t("defend_unavailable", "你暂时无法进入防御姿态。")
			.t("vanguard_prompt", "选择先锋之刃的挥击方向")
			.t("vanguard_used", "先锋之刃掠过 %1$d 个敌人。")
			.t("vanguard_none", "先锋之刃扫过空气。");
	}

	public static final String AC_DEFEND = "DEFEND";
	public static final String AC_VANGUARD = "VANGUARD";

	/** 一次挥击由七把飞刃组成。 */
	public static final int BLADES = 7;
	public static final int VANGUARD_RANGE = 5;
	public static final int VANGUARD_DEGREES = 60;
	public static final float VANGUARD_SLOW = 5f;

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
		actions.add(AC_VANGUARD);
		return actions;
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (AC_DEFEND.equals(action)) return Messages.get(this, "ac_defend");
		if (AC_VANGUARD.equals(action)) return Messages.get(this, "ac_vanguard");
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
		} else if (AC_VANGUARD.equals(action)) {
			curUser = hero;
			GameScene.selectCell(vanguardSelector);
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

		//SPSEXPD: 伤害总额拆成七份，七片飞刃各自独立结算；第 1 片沿用引擎已完成的那次命中判定，
		//其伤害由外层 Char.attack 结算，其余六片在这里独立判定并立即结算。
		int per = total / BLADES;
		int remainder = total % BLADES;

		int first = super.proc(attacker, defender, per + (remainder > 0 ? 1 : 0));

		//SPSEXPD: 主片已足以击杀时，剩余飞刃不必再挥出
		if (defender.HP > first) {
			for (int blade = 1; blade < BLADES && defender.isAlive(); blade++) {
				int amount = per + (blade < remainder ? 1 : 0);
				if (amount <= 0) continue;
				if (!Char.hit(attacker, defender, false)) continue;
				amount = super.proc(attacker, defender, amount);
				if (amount <= 0) continue;
				defender.damage(amount, this);
				//SPSEXPD: 七片飞刃各自独立命中，每一片都要播一次命中音效
				//（第 1 片由外层 Char.attack 播放，这里补上追加的六片）
				hitSound(Random.Float(0.87f, 1.15f));
			}
		}

		dashTo(attacker, defender);
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

	/** 命中两格或更远的敌人时，像刺击一样向对方接近一格。 */
	private void dashTo(Char attacker, Char defender) {
		if (!(attacker instanceof Hero) || Dungeon.level == null) return;
		Hero hero = (Hero) attacker;
		//SPSEXPD: 冲刺只在冲锋姿态生效；防御姿态原地格挡，不移动
		pd.actors.buffs.TrinityStance stance = pd.actors.buffs.TrinityStance.of(hero);
		if (stance != null && stance.defending()) return;
		if (!Dungeon.level.insideMap(hero.pos) || !Dungeon.level.insideMap(defender.pos)) return;
		if (Dungeon.level.distance(hero.pos, defender.pos) < 2) return;

		int landing = approachStep(hero.pos, defender.pos, Dungeon.level.width());
		if (!Dungeon.level.insideMap(landing)) return;
		if (Actor.findChar(landing) != null || !Dungeon.level.passable[landing]) return;

		hero.move(landing, false);
		//SPSEXPD: 给一次明确反馈，便于确认接近真的发生了（位移是瞬移，没有动画）
		pd.utils.GLog.i(Messages.get(this, "dash_near", Messages.get(defender, "name")));
		Dungeon.level.pressCell(landing);
		Dungeon.observe();
	}

	private final CellSelector.Listener vanguardSelector = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null || curUser == null || Dungeon.level == null) return;
			Hero hero = curUser;

			Ballistica aim = new Ballistica(hero.pos, target, Ballistica.WONT_STOP);
			ConeAOE cone = new ConeAOE(aim, VANGUARD_RANGE, VANGUARD_DEGREES,
					Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

			int slowed = 0;
			for (int cell : cone.cells) {
				if (!Dungeon.level.insideMap(cell)) continue;
				Char ch = Actor.findChar(cell);
				if (ch == null || ch == hero || !ch.isAlive() || ch.alignment == hero.alignment) continue;
				Buff.prolong(ch, Slow.class, VANGUARD_SLOW);
				slowed++;
			}

			TrinityStance stance = TrinityStance.of(hero);
			if (stance != null) stance.leaveDefend();

			if (hero.sprite != null) {
				hero.sprite.zap(target);
				Sample.INSTANCE.play(Assets.Sounds.BLAST, 1f, 0.5f);
			}
			if (slowed > 0) GLog.i(Messages.get(TrinityForce.this, "vanguard_used", slowed));
			else GLog.i(Messages.get(TrinityForce.this, "vanguard_none"));
			hero.spendAndNext(1f);
		}

		@Override
		public String prompt() {
			return Messages.get(TrinityForce.this, "vanguard_prompt");
		}
	};
}
