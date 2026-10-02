/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.start;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Shocked2;
import pd.actors.buffs.Shocked;
import pd.actors.buffs.Silent;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class EleKatana extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EleKatana.class)
			.t("name", "武士雷刀")
			.t("ac_zap", "一闪")
			.t("silent", "沉默状态下无法释放雷刀。")
			.t("prompt", "选择目标地点")
			.t("no", "雷刀需要10点充能。")
			.t("blocked", "这条直线上没有安全的落脚点。")
			.t("charge", "充能：%1$d / %2$d。")
			.t("desc", "附有雷电的武士刀。每次命中都会积蓄充能，并在目标身上留下不稳定电流；“一闪”消耗10点充能，斩过直线上的目标并瞬移到末端。");
	}


	public static final String AC_ZAP = "ZAP";
	public static final int ZAP_COST = 10;
	private static final String CHARGE = "charge";
	private int charge;

	public EleKatana() {
		super(2, 2f, 1f, 1, 5, 20, SpecificPlaceHolderDict.SOMETHING_0);
		unique = true;
		reinforced = true;
		cursed = true;
		defaultAction = AC_ZAP;
		usesTargeting = true;
	}

	@Override protected void applyLegacyUpgrade(Stats stats) { stats.min++; stats.max++; }
	@Override public Item uncurse() { return this; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ZAP);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_ZAP.equals(action)) {
			if (hero.buff(Silent.class) != null) GLog.w(Messages.get(this, "silent"));
			else { curUser = hero; GameScene.selectCell(zapper); }
		} else super.execute(hero, action);
	}

	public boolean zap(Hero hero, int target) {
		if (hero == null || Dungeon.level == null || charge < ZAP_COST || target == hero.pos
				|| !Dungeon.level.insideMap(target) || hero.buff(Silent.class) != null) return false;
		Ballistica beam = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
		int destination = beam.collisionPos;
		if (!Dungeon.level.passable[destination] || (Actor.findChar(destination) != null && destination != hero.pos)) {
			int index = Math.min(beam.dist - 1, beam.path.size() - 1);
			if (index < 1) return false;
			destination = beam.path.get(index);
		}
		if (!Dungeon.level.passable[destination] || Actor.findChar(destination) != null) return false;
		for (int i = 1; i <= beam.dist && i < beam.path.size(); i++) {
			Char ch = Actor.findChar(beam.path.get(i));
			if (ch != null && ch != hero) {
				Buff.affect(ch, Shocked.class).level(5);
				Buff.affect(ch, Shocked2.class).level(5);
			}
		}
		if (hero.sprite == null) {
			hero.move(destination, false);
			Dungeon.level.occupyCell(hero);
		} else if (!ScrollOfTeleportation.teleportToLocation(hero, destination)) return false;
		charge -= ZAP_COST;
		Invisibility.dispel(hero);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int low = Math.max(0, damage / 4);
		int high = Math.max(low, damage / 2);
		if (high > 0) defender.damage(high <= low ? low : Random.Int(low, high), this);
		charge++;
		if (defender.buff(Shocked2.class) != null) damage = Math.round(damage * 1.5f);
		else Buff.affect(defender, Shocked2.class).level(5);
		updateQuickslot();
		return super.proc(attacker, defender, damage);
	}

	public int charge() { return charge; }
	public void charge(int value) { charge = Math.max(0, value); updateQuickslot(); }
	@Override public String info() { return super.info() + "\n\n" + Messages.get(this, "charge", charge, ZAP_COST); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge(bundle.getInt(CHARGE)); }

	private final CellSelector.Listener zapper = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target != null && !zap(curUser, target)) GLog.w(Messages.get(EleKatana.this, charge < ZAP_COST ? "no" : "blocked"));
		}
		@Override public String prompt() { return Messages.get(EleKatana.this, "prompt"); }
	};
}
