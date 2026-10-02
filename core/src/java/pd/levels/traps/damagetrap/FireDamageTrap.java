package pd.levels.traps.damagetrap;
import pd.actors.blobs.damageblobs.FireEffectDamage;
import pd.messages.InlineText;
public class FireDamageTrap extends ElementalDamageTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(FireDamageTrap.class)
			.t("name", "火伤陷阱")
			.t("desc", "会释放火属性伤害的陷阱。");
	}
 public FireDamageTrap(){ super(ORANGE, LARGE_DOT, FireEffectDamage.class, 1, 10); } }
