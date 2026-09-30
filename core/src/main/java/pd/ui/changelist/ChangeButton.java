/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.ui.changelist;

import pd.items.Item;
import pd.messages.Messages;
import pd.scenes.ChangesScene;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import watabou.noosa.Image;
import watabou.noosa.ui.Component;

//not actually a button, but functions as one.
public class ChangeButton extends Component {
	
	protected Image icon;
	protected String title;
	protected String[] messages;

	public ChangeButton(ChangeIcons icon, String title, String... message){
		this( icon.get(), title, message );
	}

	public ChangeButton( Image icon, String title, String... messages){
		super();
		
		this.icon = icon;
		add(this.icon);
		
		this.title = Messages.titleCase(title);
		this.messages = messages;
		
		layout();
	}
	
	protected void onClick() {
		ChangesScene.showChangeInfo(new Image(icon), title, messages);
	}
	
	@Override
	protected void layout() {
		super.layout();
		
		icon.x = x + (width - icon.width()) / 2f;
		icon.y = y + (height - icon.height()) / 2f;
		PixelScene.align(icon);
	}
}
