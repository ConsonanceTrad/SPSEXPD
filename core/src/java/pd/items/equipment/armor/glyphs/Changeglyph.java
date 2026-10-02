/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.glyphs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.NormalCell;
import pd.actors.mobs.npcs.MirrorImage;
import pd.items.equipment.armor.Armor;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Changeglyph extends SpsGlyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Changeglyph.class)
			.t("name", "变幻%s")
			.t("desc", "变幻刻印有几率创造一个诱饵，并使使用者远离危险。");
	}



	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0x8844CC);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		clearElementalMarker(defender);
		if (Dungeon.level == null) return damage;
		int level = level(armor);
		if (roll(level / 2 + 6, 5, defender, 3)) createDecoy(defender);
		if (!Dungeon.bossLevel()) teleportDefender(armor, defender);
		return damage;
	}

	private static void createDecoy(Char defender) {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = defender.pos + offset;
			if (cell >= 0 && cell < Dungeon.level.length() && Actor.findChar(cell) == null
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) cells.add(cell);
		}
		if (cells.isEmpty()) return;
		Mob decoy;
		if (defender instanceof Hero) {
			MirrorImage mirror = new MirrorImage();
			mirror.duplicate((Hero)defender);
			decoy = mirror;
		} else {
			decoy = new NormalCell();
		}
		GameScene.add(decoy);
		ScrollOfTeleportation.appear(decoy, Random.element(cells));
	}

	private static void teleportDefender(Armor armor, Char defender) {
		int attempts = Math.max(1, (armor.level() < 0 ? 1 : armor.level() + 1) * 5);
		for (int i = 0; i < attempts; i++) {
			int cell = Random.Int(Dungeon.level.length());
			if (Dungeon.level.heroFOV[cell] && Dungeon.level.passable[cell] && Actor.findChar(cell) == null) {
				ScrollOfTeleportation.appear(defender, cell);
				Buff.affect(defender, Invisibility.class, 5f);
				Dungeon.observe();
				return;
			}
		}
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}
