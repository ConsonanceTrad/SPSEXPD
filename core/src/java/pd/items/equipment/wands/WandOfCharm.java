/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.SpsCharm;
import pd.actors.buffs.Vertigo;
import pd.effects.MagicMissile;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The half-charge charm wand from SPS-PD 0.9.8. */
public class WandOfCharm extends Wand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfCharm.class)
			.t("name", "魅惑法杖")
			.t("desc", "这根光属性法杖形状很普通，是暗红的色泽和镶在顶端的漆黑宝石让它显眼起来。")
			.t("stats_desc", "该法杖会消耗当前一半充能，使敌人陷入狂乱，并暂时无法直接攻击施法者。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int charges = chargesPerCast();
			wandProc(target, charges);
			if (target == Dungeon.hero) {
				Buff.affect(target, Vertigo.class, 5f);
			} else {
				Buff.affect(target, Amok.class, charges + level());
				SpsCharm charm = Buff.affect(target, SpsCharm.class,
						Random.IntRange(charges, charmDurationMax(level(), charges)));
				charm.object = curUser.id();
				if (target.sprite != null) {
					target.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);
				}
				Sample.INSTANCE.play(Assets.Sounds.CHARMS);
			}
		}

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.lighthit();
	}

	@Override
	public int initialCharges() {
		return 2;
	}

	@Override
	protected int chargesPerCast() {
		return chargesToSpend(curCharges);
	}

	public static int chargesToSpend(int currentCharges) {
		return Math.max(1, (int) Math.ceil(currentCharges * 0.5f));
	}

	public static int charmDurationMax(int lvl, int charges) {
		return Math.max(charges, 3 * lvl);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.PURPLE_LIGHT,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
