import java.util.*;

class Position {
    private int x, y;
    private Direction direction;

    public Position(int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Direction getDirection() { return direction; }

    public void moveForward() { direction.moveForward(this); }
    public void turnLeft() { direction = direction.turnLeft(); }
    public void turnRight() { direction = direction.turnRight(); }

    public void updatePosition(int x, int y) { this.x = x; this.y = y; }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ") facing " + direction;
    }
}

interface Direction {
    void moveForward(Position position);
    Direction turnLeft();
    Direction turnRight();
}

class North implements Direction {
    public void moveForward(Position position) { position.updatePosition(position.getX(), position.getY() + 1); }
    public Direction turnLeft() { return new West(); }
    public Direction turnRight() { return new East(); }
    @Override
    public String toString() { return "North"; }
}

class South implements Direction {
    public void moveForward(Position position) { position.updatePosition(position.getX(), position.getY() - 1); }
    public Direction turnLeft() { return new East(); }
    public Direction turnRight() { return new West(); }
    @Override
    public String toString() { return "South"; }
}

class East implements Direction {
    public void moveForward(Position position) { position.updatePosition(position.getX() + 1, position.getY()); }
    public Direction turnLeft() { return new North(); }
    public Direction turnRight() { return new South(); }
    @Override
    public String toString() { return "East"; }
}

class West implements Direction {
    public void moveForward(Position position) { position.updatePosition(position.getX() - 1, position.getY()); }
    public Direction turnLeft() { return new South(); }
    public Direction turnRight() { return new North(); }
    @Override
    public String toString() { return "West"; }
}

class Rover {
    private Position position;
    private Set<Obstacle> obstacles;
    private Grid grid;

    public Rover(int x, int y, Direction direction, Grid grid) {
        this.position = new Position(x, y, direction);
        this.obstacles = new HashSet<>();
        this.grid = grid;
    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }

    public void executeCommand(Command command) {
        command.execute();
    }

    public String getStatusReport() {
        return "Rover is at " + position + ". No obstacles detected.";
    }

    public Position getPosition() {
        return position;
    }

    public boolean detectObstacle() {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.isAtPosition(position.getX(), position.getY())) {
                System.out.println("Obstacle detected at (" + position.getX() + ", " + position.getY() + ")");
                return true;
            }
        }
        return false;
    }

    // Composite pattern for grid boundaries
    public boolean isWithinGrid() {
        return grid.isWithinBounds(position.getX(), position.getY());
    }
}

// Command Pattern interface and concrete command implementations
interface Command {
    void execute();
}

class MoveForwardCommand implements Command {
    private Rover rover;

    public MoveForwardCommand(Rover rover) {
        this.rover = rover;
    }

    @Override
    public void execute() {
        rover.getPosition().moveForward();
        if (!rover.isWithinGrid() || rover.detectObstacle()) {
            System.out.println("Cannot move forward. Staying in the current position.");
        }
    }
}

class TurnLeftCommand implements Command {
    private Rover rover;

    public TurnLeftCommand(Rover rover) {
        this.rover = rover;
    }

    @Override
    public void execute() {
        rover.getPosition().turnLeft();
    }
}

class TurnRightCommand implements Command {
    private Rover rover;

    public TurnRightCommand(Rover rover) {
        this.rover = rover;
    }

    @Override
    public void execute() {
        rover.getPosition().turnRight();
    }
}

// Composite Pattern for Grid and Obstacles
class Grid {
    private int width, height;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}

class Obstacle {
    private int x, y;

    public Obstacle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isAtPosition(int x, int y) {
        return this.x == x && this.y == y;
    }
}

public class MarsRover {
    public static void main(String[] args) {
        // Set up the grid and rover
        Grid grid = new Grid(10, 10);
        Rover rover = new Rover(0, 0, new North(), grid);

        // Add obstacles
        rover.addObstacle(new Obstacle(2, 2));
        rover.addObstacle(new Obstacle(3, 5));

        // Create command objects
        Command moveForward = new MoveForwardCommand(rover);
        Command turnLeft = new TurnLeftCommand(rover);
        Command turnRight = new TurnRightCommand(rover);

        // Command sequence
        rover.executeCommand(moveForward); // Move to (0, 1)
        rover.executeCommand(moveForward); // Move to (0, 2)
        rover.executeCommand(turnRight);   // Turn to East
        rover.executeCommand(moveForward); // Move to (1, 2)
        rover.executeCommand(turnLeft);    // Turn to North
        rover.executeCommand(moveForward); // Move to (1, 3)

        // Output status
        System.out.println(rover.getStatusReport()); // Final Position: (1, 3, E)
    }
}
