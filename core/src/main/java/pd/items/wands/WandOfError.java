/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.wands;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.ParalyticGas;
import pd.actors.blobs.Regrowth;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Frost;
import pd.actors.buffs.MoonFury;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Slow;
import pd.actors.mobs.npcs.NPC;
import pd.effects.MagicMissile;
import pd.effects.SpellSprite;
import pd.items.bombs.Bomb;
import pd.items.scrolls.ScrollOfRecharging;
import pd.items.scrolls.ScrollOfTeleportation;
import pd.items.weapon.melee.MagesStaff;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.LightningTrap;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

/** SPS-PD's deliberately unpredictable, zero-generation-weight wand. */
public class WandOfError extends Wand {

	public static final int EFFECT_COUNT = 10;
	private static final float SPS_FROST_DURATION = 5f;

	{
		image = ItemSpriteSheet.WAND_ERROR;
	}

	@Override
	public void onZap(Ballistica bolt) {
		applyEffect(Random.Int(EFFECT_COUNT), bolt);
	}

	void applyEffect(int effect, Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		switch (effect) {
			case 0:
				if (target == curUser) {
					ScrollOfTeleportation.teleportChar(curUser);
				} else if (target != null && !(target instanceof NPC)) {
					int destination = -1;
					for (int tries = 0; tries < 10 && destination < 0; tries++) {
						destination = Dungeon.level.randomRespawnCell(target);
					}
					if (destination < 0) {
						GLog.w(Messages.get(this, "no_teleport"));
					} else {
						ScrollOfTeleportation.appear(target, destination);
						Dungeon.level.occupyCell(target);
						GLog.i(Messages.get(this, "teleported", curUser.name(), target.name()));
					}
				}
				break;
			case 1:
				if (target != null) {
					boolean moonFury = Dungeon.hero.buff(MoonFury.class) != null;
					target.damage(quarterLifeDamage(target.HT, moonFury), this);
					if (moonFury) Buff.detach(Dungeon.hero, MoonFury.class);
				}
				break;
			case 2:
				if (target != null) Buff.affect(target, Slow.class, 10f);
				break;
			case 3:
				int destination = bolt.sourcePos;
				if (bolt.dist > 9) destination = bolt.path.get(8);
				else if (Actor.findChar(bolt.sourcePos) != null && bolt.dist > 1) {
					destination = bolt.path.get(bolt.dist - 2);
				}
				ScrollOfTeleportation.appear(curUser, destination);
				Dungeon.level.occupyCell(curUser);
				Dungeon.observe();
				break;
			case 4:
				for (int i = 1; i < bolt.dist - 1; i++) {
					int cell = bolt.path.get(i);
					int terrain = Dungeon.level.map[cell];
					if (terrain == Terrain.EMPTY || terrain == Terrain.EMBERS || terrain == Terrain.EMPTY_DECO) {
						Level.set(cell, Terrain.GRASS);
						GameScene.updateMap(cell);
					}
				}
				int terrain = Dungeon.level.map[bolt.collisionPos];
				if (terrain == Terrain.EMPTY || terrain == Terrain.EMBERS || terrain == Terrain.EMPTY_DECO
						|| terrain == Terrain.GRASS || terrain == Terrain.HIGH_GRASS) {
					GameScene.add(Blob.seed(bolt.collisionPos, (level() + 2) * 20, Regrowth.class));
				}
				break;
			case 5:
				switch (Random.Int(3)) {
					case 0: GameScene.add(Blob.seed(bolt.collisionPos, 800, ConfusionGas.class)); break;
					case 1: GameScene.add(Blob.seed(bolt.collisionPos, 500, ToxicGas.class)); break;
					default: GameScene.add(Blob.seed(bolt.collisionPos, 200, ParalyticGas.class)); break;
				}
				break;
			case 6:
				new Bomb().explode(bolt.collisionPos);
				break;
			case 7:
				LightningTrap trap = new LightningTrap();
				trap.set(curUser.pos);
				trap.activate(curUser);
				Buff.prolong(curUser, Recharging.class, 20f);
				ScrollOfRecharging.charge(curUser);
				SpellSprite.show(curUser, SpellSprite.CHARGE);
				break;
			case 8:
				if (target != null && Random.Int(2) == 0) {
					Buff.affect(target, Burning.class).reignite(target, 5f);
				} else if (target != null) {
					Buff.affect(target, Frost.class,
							SPS_FROST_DURATION * Random.Float(3f, 5f));
				}
				break;
			default:
				GLog.i(Messages.get(this, "nothing"));
				break;
		}
	}

	public static int quarterLifeDamage(int totalHealth, boolean moonFury) {
		return totalHealth / 4 * (moonFury ? 4 : 1);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.RAINBOW,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
