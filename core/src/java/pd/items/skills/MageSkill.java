/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.actors.damagetype.SpsMagicDamage;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.Item;
import pd.items.scrolls.ScrollOfTeleportation;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

/** The four mage class skills from SPS-PD 0.9.8. */
public class MageSkill extends ClassSkill {
	{ image = ItemSpriteSheet.ARMOR_MAGE; }

	@Override public void doSpecial() {
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (!visibleMob(mob, 10)) continue;
			int raw = mob.HT / 10 + Math.round(curUser.lvl * (1f + 0.1f * curUser.magicSkill()));
			int damage = Math.max(0, Math.min(mob.HP - 10, raw));
			int element = Random.Int(7);
			if (damage > 0) mob.damage(damage, SpsMagicDamage.values()[element]);
			if (!mob.isAlive()) continue;
			switch (element) {
				case 0: Buff.prolong(mob, MagicWeak.class, 10f); break;
				case 1: Buff.affect(mob, Burning.class).reignite(mob, 10f); break;
				case 2: Buff.affect(mob, FrostIce.class).level(10); break;
				case 3: Buff.affect(mob, Ooze.class).set(10f); break;
				case 4: Buff.affect(mob, Shocked.class).set(10f); break;
				case 5: Buff.affect(mob, LightShootAttack.class).level(10); break;
				default: Buff.affect(mob, ShadowCurse.class); break;
			}
		}
		if (curUser.lvl > 55) burnAdjacentWalls();
		addCooldown(20);
		finishSkillCast();
	}

	private void burnAdjacentWalls() {
		int width = Dungeon.level.width();
		int[] diagonals = {width + 1, width - 1, -width + 1, -width - 1};
		for (int offset : diagonals) {
			int cell = curUser.pos + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			int terrain = Dungeon.level.map[cell];
			if (terrain == Terrain.WALL || terrain == Terrain.WALL_DECO || terrain == Terrain.GLASS_WALL) {
				Level.set(cell, Terrain.EMBERS, Dungeon.level);
				GameScene.updateMap(cell);
			}
		}
		Dungeon.observe();
	}

	@Override public void doSpecial2() {
		Buff.prolong(curUser, Feed.class, 50f);
		Buff.prolong(curUser, HTimprove.class, 100f);
		addCooldown(curUser.lvl > 55 ? 10 : 20);
		finishSkillCast();
	}

	@Override public void doSpecial3() {
		if (Dungeon.level != null && curUser.sprite != null) ScrollOfTeleportation.teleportChar(curUser);
		if (curUser.lvl > 1) {
			curUser.lvl--;
			curUser.HTBoost += 5;
		}
		if (curUser.lvl < 56) curUser.HTBoost -= 10;
		curUser.updateHT(false);
		if (curUser.permanentHT() <= 0 || curUser.HP <= 0) curUser.die(this);
		addCooldown(15);
		finishSkillCast();
	}

	@Override public void doSpecial4() {
		Item wand = Generator.random(Generator.Category.WAND);
		if (wand != null) {
			wand.upgrade(5).identify().uncurse();
			if (curUser.lvl > 55) wand.reinforce();
			dropAtHero(wand);
		}
		Buff.prolong(curUser, Recharging.class, 30f);
		addCooldown(20);
		finishSkillCast();
	}
}
