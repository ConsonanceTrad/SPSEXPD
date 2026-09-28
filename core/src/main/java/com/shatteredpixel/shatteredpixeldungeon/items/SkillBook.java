/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.skills.ClassSkill;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

/** Converts into the reader's SPS-PD class-skill item. */
public class SkillBook extends Item {

	private static final float TIME_TO_APPLY = 2f;
	private static final String AC_APPLY = "APPLY";

	{
		image = ItemSpriteSheet.MASTERY;
		defaultAction = AC_APPLY;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_APPLY);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_APPLY.equals(action)) {
			super.execute(hero, action);
			return;
		}
		ClassSkill classSkill = ClassSkill.createFor(hero.heroClass);
		if (classSkill == null) {
			GLog.w(Messages.get(this, "not_ready"));
			return;
		}
		if (!classSkill.collect(hero.belongings.backpack)) {
			GLog.w(Messages.get(this, "no_space"));
			return;
		}
		detach(hero.belongings.backpack);
		ClassSkill.resetCooldown();
		if (hero.sprite != null) {
			hero.sprite.centerEmitter().start(Speck.factory(Speck.EVOKE), 0.05f, 10);
			hero.spend(TIME_TO_APPLY);
			hero.busy();
			hero.sprite.operate(hero.pos);
		} else {
			hero.spendAndNext(TIME_TO_APPLY);
		}
		Sample.INSTANCE.play(Assets.Sounds.EVOKE);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 200 * quantity; }
}
