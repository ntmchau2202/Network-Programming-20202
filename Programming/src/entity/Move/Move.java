package entity.Move;

public class Move {
	private int x, y;
	private String state, result;
	private String movePlayer;
	private boolean seen;

	public Move(int x, int y, String movePlayer, String state, String result) {
		this.x = x;
		this.y = y;
		this.state = state;
		this.movePlayer = movePlayer;
		this.result = result;
		this.seen = false;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public String getState() {
		return state;
	}
	public String getResult() {
		return result;
	}
	public String getMovePlayer() {
		return movePlayer;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public boolean isSeen() {
		return this.seen;
	}
	
}
