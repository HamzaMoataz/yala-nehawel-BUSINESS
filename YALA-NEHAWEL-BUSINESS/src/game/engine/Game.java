package game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import game.engine.cells.Cell;
import game.engine.dataloader.DataLoader;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.*;

public class Game {
	private Board board;
	private ArrayList<Monster> allMonsters; 
	private Monster player;
	private Monster opponent;
	private Monster current;
	
	public Game(Role playerRole) throws IOException {
		this.board = new Board(DataLoader.readCards());
		this.allMonsters = DataLoader.readMonsters();
		this.player = selectRandomMonsterByRole(playerRole);
		this.opponent = selectRandomMonsterByRole(playerRole == Role.SCARER ? Role.LAUGHER : Role.SCARER);
		this.current = player;
		this.allMonsters.remove(player);
		this.allMonsters.remove(opponent);
		ArrayList<Monster> stationedMonsters = new ArrayList<>(allMonsters);
		stationedMonsters.remove(player);
		stationedMonsters.remove(opponent);

		board.setStationedMonsters(stationedMonsters);
		ArrayList<Cell> specialCells = DataLoader.readCells();
		board.initializeBoard(specialCells);

	}
	
	public Board getBoard() {
		return board;
	}
	
	public ArrayList<Monster> getAllMonsters() {
		return allMonsters; 
	}
	
	public Monster getPlayer() {
		return player;
	}
	
	public Monster getOpponent() {
		return opponent;
	}
	
	public Monster getCurrent() {
		return current;
	}
	
	public void setCurrent(Monster current) {
		this.current = current;
	}
	
	private Monster selectRandomMonsterByRole(Role role) {
		Collections.shuffle(allMonsters);
	    return allMonsters.stream()
	    		.filter(m -> m.getRole() == role)
	    		.findFirst()
	    		.orElse(null);
	}
	private Monster getCurrentOpponent(){
		if (current == player){
			return opponent;
		}else {
			return player;
		}

	}
	private int rollDice(){
		return (int) (Math.random() *6)+ 1;
	}

	public void usePowerup() throws OutOfEnergyException {
		if(current.getEnergy()>=Constants.POWERUP_COST) {
			current.alterEnergy(-Constants.POWERUP_COST);
			current.executePowerupEffect(getCurrentOpponent());
		} else
			throw new OutOfEnergyException();
	}
	public void playTurn() throws InvalidMoveException {
		if (current.isFrozen()) {
			current.setFrozen(false);
		} else {
			int roll = rollDice();
			board.moveMonster(current, roll, getCurrentOpponent());
		}
		switchTurn();
	}
	private void switchTurn(){

		if (current==player){
			current=opponent;}
		else{
			current=player;
		}

	}

	private boolean checkWinCondition(Monster monster){
		if (monster!=null && monster.getPosition()==99 && monster.getEnergy()>=1000) {
			return true;
		}else{
			return false;
		}

	}
	public Monster getWinner(){
		if(player != null && checkWinCondition(player)) {
			return player;
		}
		if (opponent != null && checkWinCondition(opponent)){
			return opponent;
		}
		return null;
	}
}