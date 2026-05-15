package game.engine;

import java.io.IOException;
import java.util.ArrayList;

import game.engine.cards.Card;
import game.engine.cells.*;
import game.engine.exceptions.InvalidMoveException;
import game.engine.monsters.Monster;
import static game.engine.Constants.*;
import game.engine.cells.*;

import static game.engine.Constants.BOARD_COLS;
import static game.engine.Constants.BOARD_SIZE;
import static game.engine.dataloader.DataLoader.readCards;
import static game.engine.dataloader.DataLoader.readMonsters;

public class Board {
	private Cell[][] boardCells;
	private static ArrayList<Monster> stationedMonsters;
	private static ArrayList<Card> originalCards;
	public static ArrayList<Card> cards;

	private static boolean upcomingIsScarer;

	public Board(ArrayList<Card> readCards) {
		this.boardCells = new Cell[BOARD_ROWS][BOARD_COLS];
		stationedMonsters = new ArrayList<Monster>();
		originalCards = readCards;
		cards = new ArrayList<Card>();
		setCardsByRarity();
		reloadCards();
	}

	public Cell[][] getBoardCells() {
		return boardCells;
	}

	public static ArrayList<Monster> getStationedMonsters() {
		return stationedMonsters;
	}

	public static void setStationedMonsters(ArrayList<Monster> stationedMonsters) {
		Board.stationedMonsters = stationedMonsters;
	}

	public static ArrayList<Card> getOriginalCards() {
		return originalCards;
	}

	public static ArrayList<Card> getCards() {
		return cards;
	}

	public static void setCards(ArrayList<Card> cards) {
		Board.cards = cards;
	}

	private int[] indexToRowCol(int index) {
		int row = index / BOARD_COLS;
		int col = index % BOARD_COLS;

		if (row % 2 != 0) {
			col = (BOARD_COLS - 1) - col;
		}

		return new int[]{row, col};
	}

	private Cell getCell(int index) {
		int[] rc = indexToRowCol(index);
		return boardCells[rc[0]][rc[1]];
	}

	private void setCell(int index, Cell cell) {
		int[] rc = indexToRowCol(index);
		boardCells[rc[0]][rc[1]] = cell;

	}

	private void setCardsByRarity() {
		ArrayList<Card> expandsCards = new ArrayList<>();
		for (int i = 0; i < originalCards.size(); i++) {
			Card card = originalCards.get(i);
			for (int j = 0; j < card.getRarity(); j++) {
				expandsCards.add(card);
			}
		}
		originalCards = expandsCards;
		cards = new ArrayList<>(expandsCards);
	}

	public static void reloadCards() {
		ArrayList<Card> shuffledCards = new ArrayList<>(originalCards);
		for (int i = shuffledCards.size() - 1; i > 0; i--) {
			int j = (int) (Math.random() * (i + 1));
			Card temp = shuffledCards.get(i);
			shuffledCards.set(i, shuffledCards.get(j));
			shuffledCards.set(j, temp);
		}
		cards = shuffledCards;
	}

	public static Card drawCard() {
		if (cards.isEmpty()) {
			reloadCards();
		}
		return cards.remove(0);
	}

	public void moveMonster(Monster currentMonster, int roll, Monster opponentMonster)
			throws InvalidMoveException {
		int oldPosition = currentMonster.getPosition();
		currentMonster.setPosition(oldPosition + roll);

		if (currentMonster.getPosition() == opponentMonster.getPosition()) {
			currentMonster.setPosition(oldPosition);
			throw new InvalidMoveException("Monsters cannot occupy the same cell!");
		}

		Cell landedCell = getCell(currentMonster.getPosition());
		landedCell.onLand(currentMonster, opponentMonster);

		if (currentMonster.isConfused()) {
			currentMonster.decrementConfusion();
			if (opponentMonster.isConfused()) {
				opponentMonster.decrementConfusion();
			}
		}

		updateMonsterPositions(currentMonster, opponentMonster);
	}

	private void updateMonsterPositions(Monster player, Monster opponent) {
		for (int i = 0; i < BOARD_SIZE; i++) {
			getCell(i).setMonster(null);
		}
		int playerPos = player.getPosition();
		getCell(playerPos).setMonster(player);

		int opponentPos = opponent.getPosition();
		getCell(opponentPos).setMonster(opponent);
	}

	public void initializeBoard(ArrayList<Cell> specialCells) throws IOException {
		ArrayList<DoorCell> doorCells = new ArrayList<>();
		ArrayList<ConveyorBelt> conveyorBelts = new ArrayList<>();
		ArrayList<ContaminationSock> contaminationSocks = new ArrayList<>();

		for (int j = 0; j < specialCells.size(); j++) {
			Cell cell = specialCells.get(j);
			if (cell instanceof DoorCell) {
				doorCells.add((DoorCell) cell);
			} else if (cell instanceof ConveyorBelt) {
				conveyorBelts.add((ConveyorBelt) cell);
			} else if (cell instanceof ContaminationSock) {
				contaminationSocks.add((ContaminationSock) cell);
			}
		}

		int tempMonsterCount = 0;
		int tempDoorCount = 0;
		int tempConveyorCount = 0;
		int tempContaminationCount = 0;
		int tempCardCount = 0;

		ArrayList<Monster> monsters = stationedMonsters;

		for (int i = 0; i < BOARD_SIZE; i++) {

			switch (i) {
				case 2, 18, 34, 54, 82, 88:
					if (monsters != null && tempMonsterCount < monsters.size()) {
						Monster monsterCell = monsters.get(tempMonsterCount++);
						monsterCell.setPosition(i);
						setCell(i, new MonsterCell(monsterCell.getName(), monsterCell));
					}
					break;

				case 6, 22, 44, 52, 66:
					ConveyorBelt conveyorCell = conveyorBelts.get(tempConveyorCount++);
					setCell(i, new ConveyorBelt(conveyorCell.getName(), conveyorCell.getEffect()));
					break;

				case 32, 42, 74, 84, 98:
					ContaminationSock contaminationCell = contaminationSocks.get(tempContaminationCount++);
					setCell(i, new ContaminationSock(contaminationCell.getName(), contaminationCell.getEffect()));
					break;

				case 4, 12, 28, 36, 48, 56, 60, 76, 86, 90:
					String cardName = (Board.cards != null && !Board.cards.isEmpty())
							? Board.cards.get(tempCardCount % Board.cards.size()).getName()
							: "Card Cell";
					tempCardCount++;
					setCell(i, new CardCell(cardName));
					break;

				default:
					if (i % 2 == 0) {
						setCell(i, new Cell("Normal Cell"));
					} else {
						DoorCell doorCell = doorCells.get(tempDoorCount++);
						setCell(i, doorCell);
					}
					break;
			}
		}
	}
}


