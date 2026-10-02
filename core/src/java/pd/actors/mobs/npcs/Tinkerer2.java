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
import render.noosa.Game;
import pd.messages.InlineText;

public class Tinkerer2 extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Tinkerer2.class)
			.t("name", "符文学者-莲恩")
			.t("desc", "居住在多利亚的研究者之一，对不同魔法之间的联系有着强烈兴趣。")
			.t("tell1", "我想不同种类的魔法之间肯定有什么联系……没准某种东西可以帮助我。");
	}




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
