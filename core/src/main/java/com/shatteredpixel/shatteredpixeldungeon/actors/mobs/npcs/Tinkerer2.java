package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Mushroom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LynnSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTinkerer2;
import com.watabou.noosa.Game;

public class Tinkerer2 extends NPC {

	{
		spriteClass = LynnSprite.class;
		properties.add(Property.IMMOVABLE);
		properties.add(Property.ELF);
	}

	@Override
	public int defenseSkill(Char enemy) {
		return 1000;
	}

	@Override
	public void damage(int damage, Object source) {
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
	public boolean interact(Char ch) {
		sprite.turnTo(pos, Dungeon.hero.pos);
		if (ch != Dungeon.hero) return true;
		Mushroom mushroom = Dungeon.hero.belongings.getItem(Mushroom.class);
		Game.runOnRenderThread(() -> {
			if (mushroom != null) GameScene.show(new WndTinkerer2(Tinkerer2.this));
			else GameScene.show(new WndQuest(Tinkerer2.this, Messages.get(Tinkerer2.this, "tell1")));
		});
		return true;
	}
}
