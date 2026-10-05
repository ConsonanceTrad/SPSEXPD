/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 特质图标（perks.png 的 16x16 帧）。
 */

package pd.ui;

import pd.Assets;
import pd.actors.hero.perks.Perk;
import render.noosa.Image;
import render.noosa.TextureFilm;

public class PerkIcon extends Image {

	private static TextureFilm film;
	private static final int SIZE = 16;

	public PerkIcon(Perk perk) {
		this(perk.image());
	}

	public PerkIcon(int icon) {
		super(Assets.Interfaces.PERKS);

		if (film == null) film = new TextureFilm(texture, SIZE, SIZE);

		frame(film.get(icon));
	}
}
