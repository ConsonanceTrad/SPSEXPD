package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.items.quest.Mushroom;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.LynnSprite;
import pd.windows.WndQuest;
import pd.windows.WndTinkerer2;
import watabou.noosa.Game;

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
