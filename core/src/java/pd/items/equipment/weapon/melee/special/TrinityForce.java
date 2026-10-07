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
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: SPS 0.9.8 之外原创武器——三相之力。
 *
 * <p>由七把飞刃组成的武器组：一次挥击的伤害总额被拆成七次独立攻击（命中与效果各自结算），
 * 但只消耗一个回合。装备期间一直拖慢移动速度，随着以战舞击杀敌人逐步习得防御姿态与先锋之刃。</p>
 */
public class TrinityForce extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TrinityForce.class)
			.t("name", "三相之力")
			.t("desc", "七把飞刃组成的一套武器组，极难操控：只有以战舞驾驭它，七刃才会同时起舞。\n\n一次挥击由七把飞刃各自独立飞舞，命中与附带效果分别结算，但只消耗一个回合；代价是额外的重量会一直拖慢你的步伐。\n\n命中两格开外的敌人时，你会顺势向对方冲进一格。")
			.t("ac_defend", "防御姿态")
			.t("ac_vanguard", "先锋之刃")
			.t("enter_defend", "你沉入防御姿态，七刃环绕如盾。")
			.t("defend_unavailable", "你暂时无法进入防御姿态。")
			.t("vanguard_prompt", "选择先锋之刃的挥击方向")
			.t("vanguard_used", "先锋之刃掠过 %1$d 个敌人。")
			.t("vanguard_none", "先锋之刃扫过空气。")
			.t("unlock_defend", "战舞更进一步：你习得了_防御姿态_，冲锋姿态的移速惩罚也减轻了。")
			.t("unlock_vanguard", "战舞大成：你习得了_先锋之刃_，冲锋姿态不再拖慢你的步伐。")
			.t("progress_defend", "战舞进度：%1$d 次击杀（%2$d 次后习得_防御姿态_）")
			.t("progress_vanguard", "战舞进度：%1$d 次击杀（%2$d 次后习得_先锋之刃_）")
			.t("progress_done", "战舞大成：%1$d 次击杀。");
	}

	public static final String AC_DEFEND = "DEFEND";
	public static final String AC_VANGUARD = "VANGUARD";

	/** 一次挥击由七把飞刃组成。 */
	public static final int BLADES = 7;
	/** 习得防御姿态所需的累计击杀。 */
	public static final int TRAINED_KILLS = 50;
	/** 习得先锋之刃所需的累计击杀。 */
	public static final int VANGUARD_KILLS = 150;
	public static final int VANGUARD_RANGE = 5;
	public static final int VANGUARD_DEGREES = 60;
	public static final float VANGUARD_SLOW = 5f;

	private static final String KILLS = "kills";

	private int kills;

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

	public int kills() {
		return kills;
	}

	@Override
	public Item random() {
		return this;
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
		if (kills >= VANGUARD_KILLS) actions.add(AC_VANGUARD);
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
		int slain = 0;

		if (defender.HP <= first) {
			//主片已足以击杀，剩余飞刃不必再挥出
			slain = 1;
		} else {
			for (int blade = 1; blade < BLADES && defender.isAlive(); blade++) {
				int amount = per + (blade < remainder ? 1 : 0);
				if (amount <= 0) continue;
				if (!Char.hit(attacker, defender, false)) continue;
				amount = super.proc(attacker, defender, amount);
				if (amount <= 0) continue;
				defender.damage(amount, this);
				if (!defender.isAlive()) slain++;
			}
		}

		if (slain > 0) addKills(attacker, slain);
		dashTo(attacker, defender);
		return first;
	}

	private void addKills(Char attacker, int amount) {
		int before = kills;
		kills += amount;
		if (attacker instanceof Hero) {
			if (before < TRAINED_KILLS && kills >= TRAINED_KILLS) {
				GLog.p(Messages.get(this, "unlock_defend"));
			} else if (before < VANGUARD_KILLS && kills >= VANGUARD_KILLS) {
				GLog.p(Messages.get(this, "unlock_vanguard"));
			}
		}
		updateQuickslot();
	}

	/** 命中两格外的敌人时，像刺击一样向对方冲进一格。 */
	private void dashTo(Char attacker, Char defender) {
		if (!(attacker instanceof Hero) || Dungeon.level == null) return;
		Hero hero = (Hero) attacker;
		if (!Dungeon.level.insideMap(hero.pos) || !Dungeon.level.insideMap(defender.pos)) return;
		if (Dungeon.level.distance(hero.pos, defender.pos) < 2) return;

		int step = defender.pos - hero.pos;
		int landing = -1;
		for (int offset : PathFinder.NEIGHBOURS8) {
			if (offset == step) landing = hero.pos + offset;
		}
		if (landing < 0 || !Dungeon.level.insideMap(landing)) return;
		if (Actor.findChar(landing) != null || !Dungeon.level.passable[landing]) return;

		hero.move(landing, false);
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

	@Override
	public String info() {
		String info = super.info();
		if (kills < TRAINED_KILLS) {
			info += "\n\n" + Messages.get(this, "progress_defend", kills, TRAINED_KILLS - kills);
		} else if (kills < VANGUARD_KILLS) {
			info += "\n\n" + Messages.get(this, "progress_vanguard", kills, VANGUARD_KILLS - kills);
		} else {
			info += "\n\n" + Messages.get(this, "progress_done", kills);
		}
		return info;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(KILLS, kills);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		kills = Math.max(0, bundle.getInt(KILLS));
	}
}
