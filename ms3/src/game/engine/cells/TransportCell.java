package game.engine.cells;

import game.engine.monsters.Monster;

public abstract class TransportCell extends Cell {
	private int effect;
	private int positiveEffect;
	private int negativeEffect;

	public TransportCell(String name, int effect) {
		super(name);

		this.effect = effect;
	}

	public int getEffect() {

		return effect;
	}
	public void transport(Monster monster){

		monster.setPosition(monster.getPosition() + effect);
	}

	@Override
	public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster);
		transport(landingMonster);
	}
	public int getPositiveEffect() {
		return positiveEffect;
	}
	public int getNegativeEffect() {
		return negativeEffect;
	}
	public void setPositiveEffect(int positiveEffect) {
		this.positiveEffect = positiveEffect;
	}
	public void setNegativeEffect(int negativeEffect) {
		this.negativeEffect = negativeEffect;
	}
}

