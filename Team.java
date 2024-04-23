public class Team {
    private final String name;
    private int win;
    private int loss;
    private int remaining;

    public Team(String n) {
        name = n;
    }

    public void setWin(int w) {
        win = w;
    }

    public void setLoss(int a) {
        loss = a;
    }

    public void setRemaining(int r) {
        remaining = r;
    }

    public int getWin() {
        return win;
    }

    public int getLoss() {
        return loss;
    }

    public int getRemaining() {
        return remaining;
    }

    public String getName() {
        return name;
    }
}
