/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.effects.Beam;
import pd.effects.CellEmitter;
import pd.effects.particles.PurpleParticle;
import pd.items.Heap;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.tiles.DungeonTilemap;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentWandBasicWandDict;

/** Ice13's Blood Moon wand from SPS-PD 0.9.8. */
public class WandOf13 extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOf13.class)
			.t("name", "血色月华")
			.t("desc", "这根暗属性法杖是佣兵十三根据血月设计的，它能释放毁灭性的月蚀。")
			.t("stats_desc", "这根法杖射出的光束能穿透任何障碍物，造成_%1$d~%2$d点伤害_，并使目标流血和破甲。");
	}




	{
		image = EquipmentWandBasicWandDict.WAND_SPS_FIREBOLT;
		collisionProperties = Ballistica.WONT_STOP;
	}

	@Override public int min(int level) { return level; }
	@Override public int max(int level) { return 1 + 2 * level; }

	public static int maxDistance(int level) {
		return Math.min(10, level + 1);
	}

	public static int damageLevel(int level, int targets) {
		return Math.max(level - targets, 1);
	}

	@Override
	public void onZap(Ballistica beam) {
		int maximum = Math.min(maxDistance(level()), beam.dist);
		ArrayList<Char> targets = new ArrayList<>();
		for (int cell : beam.subPath(1, maximum)) {
			Char target = Actor.findChar(cell);
			if (target != null) targets.add(target);
			Heap heap = Dungeon.level.heaps.get(cell);
			if (heap != null) heap.darkhit();
			if (Game.instance != null
					&& Game.scene() instanceof pd.scenes.GameScene) {
				CellEmitter.center(cell).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
			}
		}

		int effectiveLevel = damageLevel(level(), targets.size());
		for (Char target : targets) {
			wandProc(target, chargesPerCast());
			Buff.affect(target, Bleeding.class).set(damageRoll());
			Buff.affect(target, ArmorBreak.class, 5f).level(20);
			target.damage((int)(damageRoll(effectiveLevel)
					* (1f + 0.1f * Dungeon.hero.magicSkill())), this);
			if (target.sprite != null) {
				target.sprite.centerEmitter().burst(PurpleParticle.BURST, Random.IntRange(1, 2));
				target.sprite.flash();
			}
		}
	}

	@Override
	public void fx(Ballistica beam, Callback callback) {
		int cell = beam.path.get(Math.min(beam.dist, maxDistance(level())));
		curUser.sprite.parent.add(new Beam.DeathRay(curUser.sprite.center(),
				DungeonTilemap.tileCenterToWorld(cell)));
		Sample.INSTANCE.play(Assets.Sounds.RAY);
		callback.call();
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}
}
