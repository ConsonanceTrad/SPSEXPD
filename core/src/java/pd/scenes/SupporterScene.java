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

package pd.scenes;

import com.badlogic.gdx.Gdx;
import pd.Chrome;
import pd.ShatteredPixelDungeon;
import pd.messages.Languages;
import pd.messages.Messages;
import pd.ui.ExitButton;
import pd.ui.Icons;
import pd.ui.RenderedTextBlock;
import pd.ui.ScrollPane;
import pd.ui.StyledButton;
import pd.ui.TitleBackground;
import pd.ui.Window;
import pd.windows.IconTitle;
import render.noosa.Camera;
import render.noosa.NinePatch;
import render.noosa.ui.Component;
import render.utils.data.Callback;
import render.utils.geom.RectF;
import pd.messages.InlineText;

public class SupporterScene extends PixelScene {
	//SPSEXPD: inline Chinese text (generated from messages/scenes/zh)
	static {
		InlineText.of(SupporterScene.class)
			.t("title", "支持游戏开发")
			.t("intro", "我不希望破碎的像素地牢沾染任何广告或侵入性微交易要素。那些东西毁了不知多少免费游戏。相应地，我希望玩家们能对本游戏进行直接的支持！")
			.t("patreon_msg", "如果您喜欢玩破碎的像素地牢并且想要帮助我持续地开发它，请考虑考虑在_Patreon_上支持我吧！Patreon可为您提供独占福利！\n_-抢先体验：_游玩包含未来更新的版本。\n_-每周开发小日志：_分享我正着力进行的工作。\n_-月度民意调查：_调查的结果会直接影响到我正开发的内容。\n_-实体胸针奖品：_进行1、2、3年的赞助之后。\n_-Discord社区：_包含开发日志以及聊天频道。\n_以及更多！_\n\n你可以通过访问在Patreon上的页面获得更多信息。感谢您百忙之中抽空拜访，祝地牢探险愉快！")
			.t("patreon_english", "(请注意：Patreon回报仅提供英文内容)")
			.t("supporter_link", "前往在Patreon上的主页。")
			.t("tab_pd", "SPD")
			.t("tab_sps", "SPS")
			.t("tab_spsex", "SPSEX")
			.t("sps_msg", "特别惊喜像素地牢 hmdzl 制作，是一款在经典像素地牢基础上大幅扩展的同人作品，是本移植项目的内容源头。\n\n交流群：QQ「特别坚果交流群」836879661")
			.t("spsex_msg", "特别惊喜移植版（SPSEXPD）由协音Trad 维护：将 SPS 0.9.8 的内容移植到破碎像素地牢 4.0.0 引擎，并选取 SPS 的低压体验特性融入破碎基底。\n\n项目以 GPLv3 协议开源、持续开发中。若你喜欢这个项目，欢迎参与测试、反馈问题，或在 GitHub 上点个 Star——这就是最好的支持！")
			.t("spsex_link", "项目仓库（GitHub）")
			.t("spsex_url", "https://github.com/ConsonanceTrad/STSEXPD")
			.t("pd_repo", "GitHub 仓库：00-Evan/shattered-pixel-dungeon")
			.t("qq_group", "836879661")
			.t("copy_qq", "复制群号")
			.t("copy_qq_done", "已复制群号");
	}


	private static final int BTN_HEIGHT = 22;
	private static final int GAP = 2;

	//SPS: 三来源标签页（0=PD 破碎 1=SPS 特别惊喜 2=SPSEX 移植版）
	//SPS: 默认为 SPS——主菜单进入此页的入口是「加入交流群」
	public static int sourceSelected = 1;

	@Override
	public void create() {
		super.create();

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;
		RectF insets = getCommonInsets();

		int elementWidth = PixelScene.landscape() ? 202 : 120;

		TitleBackground BG = new TitleBackground(w, h);
		add(BG);

		w -= insets.right + insets.left;
		h -= insets.top + insets.bottom;

		ExitButton btnExit = new ExitButton();
		btnExit.setPos(insets.left + w - btnExit.width(), insets.top);
		add(btnExit);

		IconTitle title = new IconTitle(Icons.GOLD.get(), Messages.get(this, "title"));
		title.setSize(200, 0);
		title.setPos(
				insets.left + (w - title.reqWidth()) / 2f,
				insets.top + (20 - title.height()) / 2f
		);
		align(title);
		add(title);

		//SPS: 三来源标签页按钮
		float tabW = (elementWidth - 4) / 3f;
		float tabLeft = insets.left + (w - elementWidth) / 2f;
		float tabTop = insets.top + 21;

		StyledButton tabPD = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "tab_pd")){
			@Override
			protected void onClick() { sourceSelected = 0; ShatteredPixelDungeon.switchNoFade(SupporterScene.class); }
		};
		tabPD.setRect(tabLeft, tabTop, tabW, 14);
		align(tabPD);
		add(tabPD);

		StyledButton tabSPS = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "tab_sps")){
			@Override
			protected void onClick() { sourceSelected = 1; ShatteredPixelDungeon.switchNoFade(SupporterScene.class); }
		};
		tabSPS.setRect(tabPD.right() + 2, tabTop, tabW, 14);
		align(tabSPS);
		add(tabSPS);

		StyledButton tabSPSEX = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "tab_spsex")){
			@Override
			protected void onClick() { sourceSelected = 2; ShatteredPixelDungeon.switchNoFade(SupporterScene.class); }
		};
		tabSPSEX.setRect(tabSPS.right() + 2, tabTop, tabW, 14);
		align(tabSPSEX);
		add(tabSPSEX);

		//SPS: 底部按钮按标签页切换（正文文本见 messageText()）
		String linkLabel = null;
		String linkUrl = null;
		String copyText = null;
		switch (sourceSelected) {
			case 1:
				//SPS: SPS 标签页底部是「复制群号」按钮
				linkLabel = Messages.get(this, "copy_qq");
				copyText = Messages.get(this, "qq_group");
				break;
			case 2:
				linkLabel = Messages.get(this, "spsex_link");
				linkUrl = Messages.get(this, "spsex_url");
				break;
			case 0: default:
				linkLabel = Messages.get(this, "supporter_link");
				linkUrl = "https://www.patreon.com/ShatteredPixel?utm_source=shatteredpd&utm_medium=supporter_page&utm_campaign=ingame_link";
		}

		//SPS: 正文区可用高度（标签栏与底部按钮之外）
		float msgMaxHeight = h - 40 - (linkLabel != null ? BTN_HEIGHT + GAP : 0);

		SupporterMessage msg = new SupporterMessage();
		//SPS: 先按完整宽度排版；只有内容确实超出可视高度、需要滚动条时，
		//才让出滚动条的 4px 并重排 —— 否则短内容会白占宽度、把正文框挤偏
		msg.setSize(elementWidth, 0);
		if (msg.height() > msgMaxHeight) {
			msg.setSize(elementWidth - 4, 0);
		}

		ScrollPane msgPane = new ScrollPane(msg);
		add(msgPane);

		StyledButton link = null;
		if (linkLabel != null) {
			final String url = linkUrl;
			final String clipboard = copyText;
			link = new StyledButton(Chrome.Type.GREY_BUTTON_TR, linkLabel){
				@Override
				protected void onClick() {
					super.onClick();
					if (clipboard != null) {
						//SPS: 复制交流群号，并把按钮文案改为已复制
						Gdx.app.getClipboard().setContents(clipboard);
						text(Messages.get(SupporterScene.class, "copy_qq_done"));
					} else {
						ShatteredPixelDungeon.platform.openURI(url);
					}
				}
			};
			link.icon(Icons.get(clipboard != null ? Icons.COPY : Icons.GOLD));
			link.textColor(Window.TITLE_COLOR);
			link.setSize(elementWidth, BTN_HEIGHT);
			add(link);
		}

		//SPS: 正文区高度封顶，内容过长时在区域内部滚动（矮屏/横屏不再压住标签栏与底部按钮）
		float msgHeight = Math.min(msg.height(), msgMaxHeight);
		float elementHeight = msgHeight + (link != null ? BTN_HEIGHT + GAP : 0);

		float top = insets.top + 40 + (h - 40 - elementHeight)/2f;
		float left = insets.left + (w-elementWidth)/2f;

		msgPane.setRect(left, top, elementWidth, msgHeight);
		msgPane.scrollTo(0, 0);
		align(msgPane);

		if (link != null) {
			link.setPos(left, top + msgHeight + GAP);
			align(link);
		}

	}

	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchNoFade( TitleScene.class );
	}

	//SPS: 支持窗口正文文本按标签页切换（0=PD 破碎 1=SPS 特别惊喜 2=SPSEX 移植版）
	private static String messageText(){
		switch (sourceSelected) {
			case 1:
				return Messages.get(SupporterScene.class, "sps_msg");
			case 2:
				return Messages.get(SupporterScene.class, "spsex_msg");
			default:
				String message = Messages.get(SupporterScene.class, "intro");
				message += "\n\n" + Messages.get(SupporterScene.class, "patreon_msg");
				if (Messages.lang() != Languages.ENGLISH) {
					message += "\n" + Messages.get(SupporterScene.class, "patreon_english");
				}
				message += "\n\n- Evan";
				//SPS: 破碎官方支持渠道（捐款 + 仓库）
				message += "\n\n" + Messages.get(SupporterScene.class, "pd_repo");
				return message;
		}
	}

	private static class SupporterMessage extends Component {

		NinePatch bg;
		RenderedTextBlock text;

		@Override
		protected void createChildren() {
			bg = Chrome.get(Chrome.Type.GREY_BUTTON_TR);
			add(bg);

			//SPS: 文本必须在 createChildren 内取，构造参数此时尚未赋值
			text = PixelScene.renderTextBlock(messageText(), 6);
			add(text);

		}

		@Override
		protected void layout() {
			bg.x = x;
			bg.y = y;

			text.maxWidth((int)width - bg.marginHor());
			text.setPos(x + bg.marginLeft(), y + bg.marginTop() + 1);

			height = (text.bottom() + 3) - y;

			height += bg.marginBottom();

			bg.size(width, height);

		}

	}

}
