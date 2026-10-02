package pd.levels.traps.damagetrap;
import pd.actors.blobs.damageblobs.DarkEffectDamage;
import pd.messages.InlineText;
public class DarkDamageTrap extends ElementalDamageTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(DarkDamageTrap.class)
			.t("name", "暗伤陷阱")
			.t("desc", "会释放暗属性伤害的陷阱。");
	}


 public DarkDamageTrap(){ super(VIOLET, LARGE_DOT, DarkEffectDamage.class, 1, 10); } }
