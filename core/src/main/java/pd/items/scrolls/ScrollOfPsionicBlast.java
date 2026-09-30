/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.scrolls;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.SuperArcane;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.NPC;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.audio.Sample;

/** The ordinary SPS psionic-draw scroll, distinct from Shattered's exotic scroll. */
public class ScrollOfPsionicBlast extends Scroll {

	{
		icon = ItemSpriteSheet.Icons.SCROLL_PSIBLAST;
		consumedValue = 10;
		initials = 7;
	}

	@Override
	public void doRead() {
		detach(curUser.belongings.backpack);
		GameScene.flash(0xFFFFFF);
		Sample.INSTANCE.play(Assets.Sounds.BLAST);
		Invisibility.dispel();

		int people = 0;
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			mob.beckon(curUser.pos);
			if (!(mob instanceof NPC)) people++;
		}
		Buff.prolong(curUser, SuperArcane.class, SuperArcane.DURATION).level(people);

		Dungeon.observe();
		setKnown();
		readAnimation();
	}

	@Override
	public void empoweredRead() {
		GameScene.flash(0xFFFFFF);
		Sample.INSTANCE.play(Assets.Sounds.BLAST);
		Invisibility.dispel();

		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (Dungeon.level.heroFOV[mob.pos]) mob.damage(mob.HT, this);
		}

		setKnown();
		curUser.spendAndNext(TIME_TO_READ);
	}

	@Override
	public int value() {
		return isKnown() ? 80 * quantity : super.value();
	}

	@Override
	public int energyVal() {
		return consumedValue * quantity;
	}
}
