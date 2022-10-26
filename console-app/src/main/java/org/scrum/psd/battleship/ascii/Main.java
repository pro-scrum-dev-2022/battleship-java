package org.scrum.psd.battleship.ascii;

import org.scrum.psd.battleship.controller.GameController;
import org.scrum.psd.battleship.controller.dto.Letter;
import org.scrum.psd.battleship.controller.dto.Position;
import org.scrum.psd.battleship.controller.dto.Ship;

import java.util.*;

import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.*;

public class Main {
    private static List<Ship> myFleet;
    protected static List<Ship> enemyFleet;

    private static final Telemetry telemetry = new Telemetry();

    private static final String STEP_SEPARATOR = colorize("-----------------------------------------", MAGENTA_TEXT());

    public static void main(String[] args) {
        telemetry.trackEvent("ApplicationStarted", "Technology", "Java");
        System.out.println(colorize("                                     |__", MAGENTA_TEXT()));
        System.out.println(colorize("                                     |\\/", MAGENTA_TEXT()));
        System.out.println(colorize("                                     ---", MAGENTA_TEXT()));
        System.out.println(colorize("                                     / | [", MAGENTA_TEXT()));
        System.out.println(colorize("                              !      | |||", MAGENTA_TEXT()));
        System.out.println(colorize("                            _/|     _/|-++'", MAGENTA_TEXT()));
        System.out.println(colorize("                        +  +--|    |--|--|_ |-", MAGENTA_TEXT()));
        System.out.println(colorize("                     { /|__|  |/\\__|  |--- |||__/", MAGENTA_TEXT()));
        System.out.println(colorize("                    +---------------___[}-_===_.'____                 /\\", MAGENTA_TEXT()));
        System.out.println(colorize("                ____`-' ||___-{]_| _[}-  |     |_[___\\==--            \\/   _", MAGENTA_TEXT()));
        System.out.println(colorize(" __..._____--==/___]_|__|_____________________________[___\\==--____,------' .7", MAGENTA_TEXT()));
        System.out.println(colorize("|                        Welcome to Battleship                         BB-61/", MAGENTA_TEXT()));
        System.out.println(colorize(" \\_________________________________________________________________________|", MAGENTA_TEXT()));
        System.out.println("");

        InitializeGame();

        StartGame();
    }

    private static void StartGame() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\033[2J\033[;H");
        System.out.println("                  __");
        System.out.println("                 /  \\");
        System.out.println("           .-.  |    |");
        System.out.println("   *    _.-'  \\  \\__/");
        System.out.println("    \\.-'       \\");
        System.out.println("   /          _/");
        System.out.println("  |      _  /\" \"");
        System.out.println("  |     /_\'");
        System.out.println("   \\    \\_/");
        System.out.println("    \" \"\" \"\" \"\" \"");
        int hitCounter = 0;

        do {
            System.out.println(STEP_SEPARATOR);
            System.out.println("Player, it's your turn");
            System.out.println("Enter coordinates (i.e. A3) for your shot or S for show sunk or left over ships:");
            String input = scanner.next();
            if (input.equals("S")) {
                showSunkShips(enemyFleet);
            } else if (input.equals("L")) {
                showLeftOverShips(enemyFleet);
            } else {
                Position position = parsePosition(input);

                while (position == null || !position.toString().matches("[a-hA-H][1-8]")) {
                    if (position == null) {
                        System.out.println("Invalid position, please enter a valid position (i.e A3):");
                    } else {
                        System.out.println("Miss. Position is outside of field. Try again.");
                        System.out.println("Enter coordinates for your shot :");
                    }
                    position = parsePosition(scanner.next());
                }
                boolean isHit = GameController.checkIsHit(enemyFleet, position);
                if (isHit) {
                    beep();
                    hitCounter++;

                    System.out.println(colorize("                \\         .  ./", RED_TEXT()));
                    System.out.println(colorize("              \\      .:\" \";'.:..\" \"   /", RED_TEXT()));
                    System.out.println(colorize("                  (M^^.^~~:.'\" \").", RED_TEXT()));
                    System.out.println(colorize("            -   (/  .    . . \\ \\)  -", RED_TEXT()));
                    System.out.println(colorize("               ((| :. ~ ^  :. .|))", RED_TEXT()));
                    System.out.println(colorize("            -   (\\- |  \\ /  |  /)  -", RED_TEXT()));
                    System.out.println(colorize("                 -\\  \\     /  /-", RED_TEXT()));
                    System.out.println(colorize("                   \\  \\   /  /", RED_TEXT()));
                }

                System.out.println(isHit ? colorize("Yeah ! Nice hit !", RED_TEXT()) : colorize("~~~~~~~~~~~~ Miss ~~~~~~~~~~~~", BLUE_TEXT()));
                // check if all ships are sunk
                if (hitCounter == 16) {
                    System.out.println(colorize("You won !", RED_TEXT()));
                    break;
                }
                telemetry.trackEvent("Player_ShootPosition", "Position", position.toString(), "IsHit", Boolean.valueOf(isHit).toString());

                position = getRandomPosition();
                isHit = GameController.checkIsHit(myFleet, position);
                System.out.println("");
                System.out.println(String.format("Computer shoot in %s%s and %s", position.getColumn(), position.getRow(), isHit ? colorize("hit your ship !", RED_TEXT()) : colorize("\n~~~~~~~~~~~~ miss ~~~~~~~~~~~~", BLUE_TEXT())));
                telemetry.trackEvent("Computer_ShootPosition", "Position", position.toString(), "IsHit", Boolean.valueOf(isHit).toString());
                if (isHit) {
                    beep();

                    System.out.println(colorize("                \\         .  ./", RED_TEXT()));
                    System.out.println(colorize("              \\      .:\" \";'.:..\" \"   /", RED_TEXT()));
                    System.out.println(colorize("                  (M^^.^~~:.'\" \").", RED_TEXT()));
                    System.out.println(colorize("            -   (/  .    . . \\ \\)  -", RED_TEXT()));
                    System.out.println(colorize("               ((| :. ~ ^  :. .|))", RED_TEXT()));
                    System.out.println(colorize("            -   (\\- |  \\ /  |  /)  -", RED_TEXT()));
                    System.out.println(colorize("                 -\\  \\     /  /-", RED_TEXT()));
                    System.out.println(colorize("                   \\  \\   /  /", RED_TEXT()));

                }
                showBattleField(false, false);
            }
        } while (true);
    }

    private static void showLeftOverShips(Collection<Ship> ships) {
        for (Ship s : ships) {
            if (!s.isSunk()) {
                System.out.println("Left over ship: " + s.getName());
            }
        }
    }

    private static void showSunkShips(Collection<Ship> ships) {
        for (Ship s : ships) {
            if (s.isSunk()) {
                System.out.println("Sunk ship: " + s.getName());
            }
        }
    }

    private static void beep() {
        System.out.print("\007");
    }

    protected static Position parsePosition(String input) {
        try {
            Letter letter = Letter.valueOf(input.toUpperCase().substring(0, 1));
            int number = Integer.parseInt(input.substring(1));
            return new Position(letter, number);

        } catch (Exception e) {
            return null;
        }
    }

    private static Position getRandomPosition() {
        int rows = 8;
        int lines = 8;
        Random random = new Random();
        Letter letter = Letter.values()[random.nextInt(lines)];
        int number = random.nextInt(rows);
        Position position = new Position(letter, number);
        return position;
    }

    private static void InitializeGame() {
        InitializeMyFleet();

        InitializeEnemyFleet();
    }

    private static void InitializeMyFleet() {
        Scanner scanner = new Scanner(System.in);
        myFleet = GameController.initializeShips();

        System.out.println("Please position your fleet (Game board has size from A to H and 1 to 8) :");

        for (Ship ship : myFleet) {
            System.out.println("");
            System.out.println(String.format("Please enter the positions for the %s (size: %s)", ship.getName(), ship.getSize()));
            for (int i = 1; i <= ship.getSize(); i++) {
                System.out.println(String.format("Enter position %s of %s (i.e A3):", i, ship.getSize()));

                String positionInput = scanner.next();

                // check if input is valid
                while (!positionInput.matches("[a-hA-H][1-8]")) {
                    System.out.println("Invalid position, please enter a valid position (i.e A3):");
                    positionInput = scanner.next();
                }

                ship.addPosition(positionInput);
                telemetry.trackEvent("Player_PlaceShipPosition", "Position", positionInput, "Ship", ship.getName(), "PositionInShip", Integer.valueOf(i).toString());
            }
        }
    }

    protected static void InitializeEnemyFleet() {
        enemyFleet = GameController.initializeShips();
        List<Letter> rows = Arrays.asList(Letter.A, Letter.B, Letter.C, Letter.D, Letter.E, Letter.F, Letter.G, Letter.H);
        List<Integer> usedIndexRows = new ArrayList<>();

        for (Ship s : enemyFleet) {
            Letter row = getRandomRow(rows, usedIndexRows);
            Integer startPosition = getRandomColumn(8 - s.getSize());
            for (int i = 0; i < s.getSize(); i++) {
                s.getPositions().add(new Position(row, startPosition + i));
            }
        }

//        enemyFleet.get(0).getPositions().add(new Position(Letter.B, 4));
//        enemyFleet.get(0).getPositions().add(new Position(Letter.B, 5));
//        enemyFleet.get(0).getPositions().add(new Position(Letter.B, 6));
//        enemyFleet.get(0).getPositions().add(new Position(Letter.B, 7));
//        enemyFleet.get(0).getPositions().add(new Position(Letter.B, 8));
//
//        enemyFleet.get(1).getPositions().add(new Position(Letter.E, 6));
//        enemyFleet.get(1).getPositions().add(new Position(Letter.E, 7));
//        enemyFleet.get(1).getPositions().add(new Position(Letter.E, 8));
//        enemyFleet.get(1).getPositions().add(new Position(Letter.E, 9));
//
//        enemyFleet.get(2).getPositions().add(new Position(Letter.A, 3));
//        enemyFleet.get(2).getPositions().add(new Position(Letter.B, 3));
//        enemyFleet.get(2).getPositions().add(new Position(Letter.C, 3));
//
//        enemyFleet.get(3).getPositions().add(new Position(Letter.F, 8));
//        enemyFleet.get(3).getPositions().add(new Position(Letter.G, 8));
//        enemyFleet.get(3).getPositions().add(new Position(Letter.H, 8));
//
//        enemyFleet.get(4).getPositions().add(new Position(Letter.C, 5));
//        enemyFleet.get(4).getPositions().add(new Position(Letter.C, 6));
    }

    private static Letter getRandomRow(List<Letter> array, List<Integer> usedIndex) {
        int rnd = -1;
        do {
            rnd = new Random().nextInt(array.size());
        } while (usedIndex.contains(rnd));
        usedIndex.add(rnd);
        return array.get(rnd);
    }

    private static Integer getRandomColumn(Integer maxColumnIndex) {
        List<Integer> columnIndex = new ArrayList<>();
        for (int i = 0; i < maxColumnIndex; i++) {
            columnIndex.add(i);
        }
        int rnd = new Random().nextInt(columnIndex.size());
        return columnIndex.get(rnd);
    }

    private static void showBattleField(boolean my, boolean showShips) {
        List<Letter> columns = Arrays.asList(Letter.A, Letter.B, Letter.C, Letter.D, Letter.E, Letter.F, Letter.G, Letter.H);
        if (my) {
            showBattleField(columns, myFleet, showShips);
        } else {
            showBattleField(columns, enemyFleet, showShips);
        }
    }

    private static void showBattleField(List<Letter> columns, List<Ship> fleet, boolean showShips) {
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++) {
                boolean isShipFragment = checkField(columns, i, j, fleet, false);
                boolean isHittedShipFragment = checkField(columns, i, j, fleet, true);
                if (isShipFragment) {
                    if (isHittedShipFragment) {
                        System.out.print(colorize(" X ", RED_TEXT()));
                    } else {
                        if (showShips) {
                            System.out.print(colorize(" H ", YELLOW_TEXT()));
                        } else {
                            System.out.print(colorize(" ~ ", BLUE_TEXT()));
                        }
                    }
                } else {
                    System.out.print(colorize(" ~ ", BLUE_TEXT()));
                }
                if (i == 7) {
                    System.out.println();
                }
            }
        }
    }

    private static boolean checkField(List<Letter> columns, int i, int j, List<Ship> fleet, boolean hittedFragment) {
        for (Ship s : fleet) {
            if (hittedFragment) {
                for (Position p : s.getHitPositions()) {
                    if (p.getRow() == i && columns.indexOf(p.getColumn()) == j) {
                        return true;
                    }
                }
            } else {
                for (Position p : s.getPositions()) {
                    if (p.getRow() == i && columns.indexOf(p.getColumn()) == j) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
