/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.mobs.DecoySheep;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 移植自 Darkest PD 0.7.2 的神器「绵羊披风」（CloakOfSheep）。
 *
 * <p>可以「闪烁」到射程内任意已探明且空置的格子，并在原地留下一只绵羊替身吸引敌人。
 * 每次闪烁都有冷却，闪烁次数累积可以让披风升级（射程更远）。</p>
 */
public class CloakOfSheep extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CloakOfSheep.class)
			.t("name", "绵羊披风")
			.t("ac_blink", "闪烁")
			.t("prompt", "选择闪烁目的地")
			.t("out_of_range", "那里太远了。")
			.t("cannot_go_there", "你无法闪烁到那里。")
			.t("not_ready", "绵羊披风尚未准备好再次闪烁。")
			.t("cannot_move", "你被缠住了，无法闪烁。")
			.t("cursed", "被诅咒的绵羊披风不肯挪动半分。")
			.t("baa", "咩？")
			.t("levelup", "绵羊披风变得更加强大！")
			.t("desc", "咩？咩……\n\n披风的穿戴者变得格外灵巧，它允许你发动_闪烁_：瞬间移动，并在原地留下一只绵羊替你挨打。")
			.t("desc_hint", "闪烁距离：%1$d 格。每次闪烁都会留下一只替身绵羊，并累积成长。")
			.t("desc_max", "绵羊披风已经成长到极限，闪烁距离额外提升 2 格。")
			.t("desc_cursed", "绵羊披风被诅咒了咩……");
	}

	public static final String AC_BLINK = "BLINK";
	/** 每次闪烁后的冷却回合数。 */
	public static final int COOLDOWN = 20;
	/** 基础闪烁距离。 */
	public static final int RANGE = 5;
	/** 满级时的额外闪烁距离。 */
	public static final int MAX_RANGE_BONUS = 2;

	{
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
		levelCap = 10;
		charge = 0;
		partialCharge = 0;
		chargeCap = COOLDOWN;
		defaultAction = AC_BLINK;
	}

	/** 当前闪烁距离。 */
	public int range() {
		return level() + RANGE + (level() >= levelCap() ? MAX_RANGE_BONUS : 0);
	}

	/** 升到下一级所需的闪烁次数。 */
	public int requireExp() {
		return (level() + 2) * (level() + 1);
	}

	public int exp() {
		return exp;
	}

	/** 是否已冷却完毕。 */
	public boolean ready() {
		return charge <= 0;
	}

	private final CellSelector.Listener caster = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null || Dungeon.level == null) return;
			Hero hero = Dungeon.hero;
			if (hero == null) return;
			if (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell]) return;
			if (Dungeon.level.distance(hero.pos, cell) > range()) {
				GLog.w(Messages.get(CloakOfSheep.class, "out_of_range"));
				return;
			}
			if (Dungeon.level.solid[cell] || Actor.findChar(cell) != null) {
				GLog.w(Messages.get(CloakOfSheep.class, "cannot_go_there"));
				return;
			}
			blink(cell);
		}

		@Override
		public String prompt() {
			return Messages.get(CloakOfSheep.class, "prompt");
		}
	};

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && !cursed && ready()) actions.add(AC_BLINK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (!AC_BLINK.equals(action)) return;

		if (!isEquipped(hero)) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			return;
		}
		if (cursed) {
			GLog.w(Messages.get(this, "cursed"));
			return;
		}
		if (!ready()) {
			GLog.w(Messages.get(this, "not_ready"));
			return;
		}
		if (hero.rooted) {
			GLog.w(Messages.get(this, "cannot_move"));
			return;
		}
		GameScene.selectCell(caster);
	}

	/** 闪烁：留下替身绵羊，自己瞬移过去。 */
	public void blink(int cell) {
		Hero hero = Dungeon.hero;
		if (hero == null || Dungeon.level == null) return;

		charge = chargeCap;
		exp++;
		if (level() < levelCap() && exp >= requireExp()) {
			exp -= requireExp();
			upgrade();
			GLog.p(Messages.get(this, "levelup"));
		}

		//SPSEXPD: 原地留下一只替身绵羊替玩家吸引敌人
		DecoySheep sheep = new DecoySheep();
		sheep.initialize(Random.Int(Dungeon.depth + 10) + 2);
		sheep.pos = hero.pos;
		GameScene.add(sheep);

		ScrollOfTeleportation.appear(hero, cell);
		GLog.i(Messages.get(this, "baa"));
		hero.spendAndNext(0.5f);
		Dungeon.observe();
		updateQuickslot();
	}

	@Override
	public String desc() {
		String desc = super.desc();
		Hero hero = Dungeon.hero;
		if (hero == null || !isEquipped(hero)) return desc;

		if (cursed) return desc + "\n\n" + Messages.get(this, "desc_cursed");
		desc += "\n\n" + Messages.get(this, "desc_hint", range());
		if (level() >= levelCap()) return desc + "\n" + Messages.get(this, "desc_max");
		return desc;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Recharge();
	}

	/** 冷却递减。 */
	public class Recharge extends ArtifactBuff {
		@Override
		public boolean act() {
			if (charge > 0) {
				charge--;
				if (charge <= 0) updateQuickslot();
			}
			spend(TICK);
			return true;
		}
	}
}
