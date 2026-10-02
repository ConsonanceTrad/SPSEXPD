/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.EquipmentWandBasicWandDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.effects.MagicMissile;
import pd.items.Heap;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The direct-damage acid wand from SPS-PD 0.9.8. */
public class WandOfAcid extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfAcid.class)
			.t("name", "酸蚀法杖")
			.t("desc", "这根地属性法杖的紫色主干半包住顶端一颗亮绿色宝石。")
			.t("stats_desc", "被使用时，这根法杖会释放出翠绿的强酸，造成_%1$d~%2$d点伤害_并有概率使被命中的敌人持续受到酸蚀伤害。");
	}


	{
		image = EquipmentWandBasicWandDict.WAND_ACID;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public int min(int lvl) {
		return 2 + lvl;
	}

	@Override
	public int max(int lvl) {
		return 6 + 4 * lvl;
	}

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	static Class<Ooze> oozeEffectClass() {
		return Ooze.class;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.earthhit();

		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			wandProc(target, chargesPerCast());
			if (target.isAlive() && Random.Int(2) == 0) {
				Buff.affect(target, oozeEffectClass()).set(level());
			}
			target.damage((int) (damageRoll() * magicSkillMultiplier(Dungeon.hero.magicSkill())), this);
		}
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.FOLIAGE,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
