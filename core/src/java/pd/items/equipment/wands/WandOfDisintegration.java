/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.Beam;
import pd.effects.CellEmitter;
import pd.effects.particles.PurpleParticle;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.tiles.DungeonTilemap;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The short-range, obstacle-piercing disintegration wand from SPS-PD 0.9.8. */
public class WandOfDisintegration extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfDisintegration.class)
			.t("name", "解离法杖")
			.t("staff_name", "解离魔杖")
			.t("desc", "这根_无属性_法杖由一整块光滑的黑曜石制成，深紫色的光自其边缘流向法杖顶端。它闪烁着破坏性能量，准备着向前迸射。")
			.t("stats_desc", "这根法杖射出的光束能穿透任何障碍物，并将随着法杖等级的提升而射得更远。光束会造成_%1$d~%2$d点伤害_，并且会根据穿透的地形和敌人数量造成额外伤害。")
			.t("upgrade_stat_name_2", "射程上限")
			.t("bmage_desc", "当_战斗法师_以解离魔杖作为近战武器时，魔杖会像索敌附魔一样获得额外的攻击距离。")
			.t("eleblast_desc", "解离魔杖的元素风暴无视墙壁并对所有目标造成100%的伤害。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		collisionProperties = Ballistica.WONT_STOP;
	}

	@Override public int min(int level) { return 2 + level; }
	@Override public int max(int level) { return 8 + 4 * level; }

	public static int maxDistance(int level) {
		return Math.min(8, level + 2);
	}

	public static int damageLevel(int level, int targets, int terrainBonus) {
		return Math.max(1, level + Math.max(0, targets - 1) + terrainBonus);
	}

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	@Override
	public void onZap(Ballistica beam) {
		int maximum = Math.min(maxDistance(level()), beam.dist);
		ArrayList<Char> targets = new ArrayList<>();
		int terrainPassed = 2;
		int terrainBonus = 0;

		for (int cell : beam.subPath(1, maximum)) {
			Char target = Actor.findChar(cell);
			if (target != null) {
				terrainBonus += terrainPassed / 3;
				terrainPassed %= 3;
				targets.add(target);
			}
			if (Dungeon.level.solid[cell]) terrainPassed++;
			CellEmitter.center(cell).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
		}

		int damageLevel = damageLevel(level(), targets.size(), terrainBonus);
		for (Char target : targets) {
			wandProc(target, chargesPerCast());
			target.damage((int)(damageRoll(damageLevel)
					* magicSkillMultiplier(Dungeon.hero.magicSkill())), this);
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
