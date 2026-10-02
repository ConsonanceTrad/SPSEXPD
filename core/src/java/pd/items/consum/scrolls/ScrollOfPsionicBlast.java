/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.scrolls;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.SuperArcane;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.NPC;
import pd.scenes.GameScene;
import pd.sprites.ItemIconSheet;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

/** The ordinary SPS psionic-draw scroll, distinct from Shattered's exotic scroll. */
public class ScrollOfPsionicBlast extends Scroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfPsionicBlast.class)
			.t("name", "灵能汲取卷轴")
			.t("ondeath", "灵能震爆撕碎了你的意识……")
			.t("desc", "这张卷轴蕴含神秘的能量，一旦引导出来将吸取视野内所有生物的心灵，并提升使用者的灵能。");
	}




	{
		icon = ItemIconSheet.SCROLL_PSIBLAST;
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
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
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

		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
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
