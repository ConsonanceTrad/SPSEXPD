package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Mushroom;
import com.shatteredpixel.shatteredpixeldungeon.items.sellitem.SellMushroom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TinkererSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTinkerer;
import com.watabou.noosa.Game;

public class Tinkerer1 extends NPC {

	{
		spriteClass = TinkererSprite.class;
		properties.add(Property.IMMOVABLE);
		properties.add(Property.HUMAN);
	}

	@Override
	public int defenseSkill(Char enemy) {
		return 1000;
	}

	@Override
	public void damage(int dmg, Object src) {
	}

	@Override
	public boolean add(Buff buff) {
		return false;
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public SellMushroom SupercreateLoot() {
		return new SellMushroom();
	}

	@Override
	public boolean interact(Char ch) {
		sprite.turnTo(pos, Dungeon.hero.pos);
		if (ch != Dungeon.hero) return true;

		Mushroom mushroom = Dungeon.hero.belongings.getItem(Mushroom.class);
		Waterskin waterskin = Dungeon.hero.belongings.getItem(Waterskin.class);
		Game.runOnRenderThread(() -> {
			if (mushroom != null && waterskin != null) {
				GameScene.show(new WndTinkerer(Tinkerer1.this));
			} else {
				GameScene.show(new WndQuest(Tinkerer1.this,
						Messages.get(Tinkerer1.this, waterskin == null ? "tell2" : "tell1")));
			}
		});
		return true;
	}
}
