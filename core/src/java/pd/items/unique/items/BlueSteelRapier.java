/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Exposed;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;

/**
 * SPSEXPD: 蓝钢刺剑 —— 决斗一档（面具 A 路线）的武器。
 *
 * <p>从敌人的_八个方位_（相邻格或其延伸方向）各击中它一次，就会在敌人身上积满
 * 「破绽百出」；攒满的那一击_当场斩杀_（对 BOSS 只造成其最大生命 15%，但可以反复触发）。
 * 此外，在敌人周围八格内攻击时，你会_冲刺到它的对向格_。</p>
 */
public class BlueSteelRapier extends MeleeWeapon implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.DUELIST_SHIELD_T1_1_RAPIER;

	/** 需要击破的方位数。 */
	public static final int DIRECTIONS = 8;
	/** 对 BOSS 的斩杀伤害（最大生命比例）。 */
	public static final float BOSS_FINISHER = 0.15f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(BlueSteelRapier.class)
			.t("name", "蓝钢刺剑")
			.t("finished", "你一剑刺进%s的破绽，它当场毙命。")
			.t("boss_hit", "你找到了%s的破绽，这一剑造成了 %d 点伤害。")
			.t("desc", "一柄窄长的刺剑，剑身泛着淬火后的蓝。\n\n从敌人的_八个方位_（相邻格或其延伸方向）各击中它一次，它的破绽就会被全部挑开——攒满的那一击_当场斩杀_。\n\n对 _BOSS_ 而言，斩杀只会造成它_最大生命的 15%_，但破绽会重新出现，可以反复触发。\n\n此外，在敌人周围_八格范围内_攻击它时，你会_冲刺到它的对向格_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 2;
		unique = true;
	}

	@Override public int min(int lvl) { return 3 + lvl; }
	@Override public int max(int lvl) { return 8 + 2 * lvl; }
	@Override public int STRReq(int lvl) { return 12; }

	/** 需要击破的方位数（零素刺剑只要 6 个）。 */
	protected int requiredDirections() {
		return DIRECTIONS;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (attacker != null && defender != null && attacker != defender && defender.isAlive()) {
			int dir = direction(attacker.pos, defender.pos);
			if (dir >= 0) {
				Exposed exposed = Buff.affect(defender, Exposed.class).setup(requiredDirections());
				exposed.mark(dir);
				if (exposed.complete()) {
					finisher(attacker, defender);
					sprintPast(attacker, defender);
					return 0;
				}
			}
			sprintPast(attacker, defender);
		}
		return super.proc(attacker, defender, damage);
	}

	/**
	 * SPSEXPD: 攻击者相对防御者的方位（0..8，4 为同一格）。
	 * 只看方向不看距离，因此「相邻格及其延伸」落在同一方位上。
	 */
	public static int direction(int from, int to) {
		if (Dungeon.level == null) return -1;
		int w = Dungeon.level.width();
		int dx = Integer.signum((from % w) - (to % w));
		int dy = Integer.signum((from / w) - (to / w));
		if (dx == 0 && dy == 0) return -1;
		return (dy + 1) * 3 + (dx + 1);
	}

	/** SPSEXPD: 斩杀——普通单位直接击杀，BOSS 只吃 15% 最大生命，随后破绽重开。 */
	protected void finisher(Char attacker, Char defender) {
		if (defender.properties().contains(Char.Property.BOSS)) {
			int dmg = Math.max(1, Math.round(defender.HT * BOSS_FINISHER));
			defender.damage(dmg, this);
			GLog.p(Messages.get(this, "boss_hit", defender.name(), dmg));
		} else {
			defender.damage(defender.HP + defender.HT, this);
			GLog.p(Messages.get(this, "finished", defender.name()));
		}
		Exposed exposed = defender.buff(Exposed.class);
		if (exposed != null) exposed.clearMarks();
	}

	/** SPSEXPD: 贴身攻击越过敌人，冲刺到其对向格。 */
	protected void sprintPast(Char attacker, Char defender) {
		if (Dungeon.level == null || attacker == null || defender == null) return;
		if (Dungeon.level.distance(attacker.pos, defender.pos) != 1) return;
		int w = Dungeon.level.width();
		int dx = Integer.signum((attacker.pos % w) - (defender.pos % w));
		int dy = Integer.signum((attacker.pos / w) - (defender.pos / w));
		int tx = (defender.pos % w) - dx;
		int ty = (defender.pos / w) - dy;
		if (tx < 0 || ty < 0 || tx >= w) return;
		int cell = ty * w + tx;
		if (!Dungeon.level.insideMap(cell)) return;
		if (!Dungeon.level.passable[cell] || Dungeon.level.solid[cell]) return;
		if (Actor.findChar(cell) != null) return;

		attacker.pos = cell;
		Dungeon.level.occupyCell(attacker);
		if (attacker.sprite != null) attacker.sprite.place(cell);
		if (attacker instanceof Hero) GameScene.updateFog();
		Dungeon.observe();
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
