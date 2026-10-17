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

package pd.messages;

import java.util.Locale;

public enum Languages {
	ENGLISH("english",      "en", Status.O_COMPLETE, null, null),
	CHI_SMPL("简体中文",     "zh", Status.O_COMPLETE, new String[]{"Chronie_Lynn_Iwa", "Jinkeloid(zdx00793)", "endlesssolitude"}, new String[]{"931451545", "Budding", "DM_216", "Fatir", "Fishbone", "Hcat", "HoofBumpBlurryface", "Horr_lski", "Lery", "Lyn_0401", "Lyx0527", "Ooooscar", "RainSlide", "ShatteredFlameBlast", "SpaceAnchor", "SunsetGlowTheDOGE", "Teller", "hmdzl001", "leo", "tempest102", "tequilasunset", "户方狸奴"}),
	CHI_TRAD("繁體中文","zh-hant", Status.__UNREVIEW, new String[]{"JZR", "Yichm", "p2635"}, new String[]{"DT227", "Fishbone", "Ken4Ro", "Lstron", "Relrin167", "Sotis425", "Zoe096423", "arnolam", "jackymaxj", "redbrow", "shiba", "唐延諭"});
	//FINNISH("suomi", 		"fi", Status.X_UNFIN, new String[]{"TenguKnight"}, new String[]{"Allugaattori10", "Dakkus", "Jaskas123", "MailBoxGod", "Oftox", "Sautari", "Tikkari"} ),
	//CATALAN("català",     "ca", Status.X_UNFIN, new String[]{"Illyatwo2"}, new String[]{"Elosy", "n1ngu"})
	//GALICIAN("galego",    "gl", Status.X_UNFIN, new String[]{"xecarballido"}, null),
	//BASQUE("euskara",     "eu", Status.X_UNFIN, new String[]{"Deathrevenge", "Osoitz"}, null),

	public enum Status{
		//below 80% translated languages are not added or removed
		X_UNFINISH, //unfinished, ~80-99% translated
		__UNREVIEW, //unreviewed, but 100% translated
		O_COMPLETE, //complete, 100% reviewed
	}

	private String name;
	private String code;
	private Status status;
	private String[] reviewers;
	private String[] translators;

	Languages(String name, String code, Status status, String[] reviewers, String[] translators){
		this.name = name;
		this.code = code;
		this.status = status;
		this.reviewers = reviewers;
		this.translators = translators;
	}

	public String nativeName(){
		return name;
	}

	public String code(){
		return code;
	}

	public Status status(){
		return status;
	}

	public String[] reviewers() {
		if (reviewers == null) return new String[]{};
		else return reviewers.clone();
	}

	public String[] translators() {
		if (translators == null) return new String[]{};
		else return translators.clone();
	}

	public static Languages matchLocale(Locale locale){
		//special case for chinese traditional, which matches more specifically than other languages
		if (locale.getLanguage().equals("zh") && locale.toString().contains("Hant")){
			return Languages.CHI_TRAD;
		}
		return matchCode(locale.getLanguage());
	}

	public static Languages matchCode(String code){
		for (Languages lang : Languages.values()){
			if (lang.code().equals(code))
				return lang;
		}
		return ENGLISH;
	}

}
