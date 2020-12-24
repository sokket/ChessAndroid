package ru.oceancraft.chess.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessGame {
    private final Tile[][] gameBoard = new Tile[8][8];
    private final ChessView chessView;
    private ActionTransmitter actionTransmitter;
    private final boolean netMode;

    private int highLightSrcX;
    private int highLightSrcY;

    private final boolean whiteGame;
    private boolean whiteTurn = true;

    private boolean checkmate = false;

    private Position lastPawnMove = null;

    private boolean allowedCastlingForLWR = true;
    private boolean allowedCastlingForLBR = true;
    private boolean allowedCastlingForRWR = true;
    private boolean allowedCastlingForRBR = true;

    private final List<Movement> allowedMoves = new ArrayList<>();
    private final List<Movement> showedMoves = new ArrayList<>();

    public ChessGame(ChessView chessView, ActionTransmitter actionTransmitter, boolean whiteGame) {
        this.chessView = chessView;
        this.actionTransmitter = actionTransmitter;
        this.whiteGame = whiteGame;
        this.netMode = true;
    }

    public ChessGame(ChessView chessView) {
        this.chessView = chessView;
        this.whiteGame = true;
        this.netMode = false;
    }

    private boolean isCastlingAllowed(Tile[][] board, Castling castling) {
        Tile[][] boardCopy = copyOfBoard(board);
        if (isCheck(boardCopy, true))
            return false;
        int y = castling.kingOldPosition.y;
        if (castling.longCastling) {
            for (int x = castling.kingOldPosition.x; x > castling.highLighted.x; x--) {
                changeBoardWithMove(boardCopy, new SimpleMovement(
                        new Position(x, y),
                        new Position(x - 1, y)
                ));
                if (isCheck(boardCopy, true))
                    return false;
            }
        } else {
            for (int x = castling.kingOldPosition.x; x < castling.highLighted.x; x++) {
                changeBoardWithMove(boardCopy, new SimpleMovement(
                        new Position(x, y),
                        new Position(x + 1, y)
                ));
                if (isCheck(boardCopy, true))
                    return false;
            }
        }
        return true;
    }

    private List<Movement> getMovements(Tile[][] board, int fromX, int fromY, boolean notRealMove) {
        return getMovements(board, fromX, fromY, notRealMove, false);
    }

    private List<Movement> getMovements(Tile[][] board, int fromX, int fromY, boolean notRealMove, boolean ignoreCastling) {
        ArrayList<Movement> trimmed = new ArrayList<>();
        TileType tileType = board[fromY][fromX].getTileType();

        boolean isPawn = tileType == TileType.WHITE_PAWN || tileType == TileType.BLACK_PAWN;
        boolean isKing = tileType == TileType.WHITE_KING || tileType == TileType.BLACK_KING;

        boolean lastTrimForCastlingAdd = false;

        boolean isSrcWhite = tileType.isWhite();
        Position[][] moves = tileType.getMovesFor(fromX, fromY);

        for (int i = 0; i < moves.length; i++)
            for (Position position : moves[i])
                if (checkOverLap(position)) {
                    TileType targetTileType = board[position.y][position.x].getTileType();
                    boolean targetColor = isBlack(targetTileType);

                    if (targetTileType == TileType.BLANK && (!isPawn || i == 0) && !isKing) {
                        trimmed.add(new SimpleMovement(
                                new Position(fromX, fromY),
                                position
                        ));
                    } else if (
                            targetTileType == TileType.BLACK_KING ||
                                    targetTileType == TileType.WHITE_KING) {
                        if (isSrcWhite != targetColor && notRealMove && (!isPawn || i != 0)) {
                            trimmed.add(new EatMovement(
                                    new Position(fromX, fromY),
                                    position
                            ));
                        }
                        break;
                    } else if (isPawn && i != 0 && lastPawnMove != null &&
                            (fromY == 3 || fromY == 4) &&
                            Math.abs(position.y - lastPawnMove.y) == 1 &&
                            position.x == lastPawnMove.x &&
                            isSrcWhite != board[lastPawnMove.y][lastPawnMove.x].getTileType().isWhite()
                    ) {
                        trimmed.add(new EnPassant(
                                new Position(lastPawnMove.x, lastPawnMove.y),
                                new Position(fromX, fromY),
                                position
                        ));
                    } else if (isKing && (i != 8 && i != 9) && targetTileType == TileType.BLANK) {
                        trimmed.add(new SimpleMovement(
                                new Position(fromX, fromY),
                                position
                        ));
                    } else if (isKing && i == 8 && targetTileType == TileType.BLANK) {
                        if ((allowedCastlingForRWR && whiteTurn) || (allowedCastlingForLBR && !whiteTurn)) {
                            Castling castling = new Castling(
                                    new Position(fromX, fromY),
                                    position,
                                    new Position(position.x + 1, fromY),
                                    new Position(position.x - 1, fromY),
                                    false
                            );
                            if (!ignoreCastling && isCastlingAllowed(board, castling))
                                trimmed.add(castling);
                        } else {
                            break;
                        }
                    } else if (isKing && targetTileType == TileType.BLANK) {
                        if ((allowedCastlingForLWR && whiteTurn) || (allowedCastlingForRBR && !whiteTurn)) {
                            Castling castling = new Castling(
                                    new Position(fromX, fromY),
                                    position,
                                    new Position(position.x - 2, fromY),
                                    new Position(position.x + 1, fromY),
                                    true
                            );
                            if (!ignoreCastling && isCastlingAllowed(board, castling)) {
                                trimmed.add(castling);
                                lastTrimForCastlingAdd = true;
                            }
                        } else {
                            break;
                        }
                    } else if (isKing && i == 9) {
                        if (lastTrimForCastlingAdd) {
                            trimmed.remove(trimmed.size() - 1);
                        }
                        break;
                    } else if (
                            targetTileType != TileType.BLANK &&
                                    isSrcWhite != targetColor && (!isPawn || i != 0)) {
                        trimmed.add(new EatMovement(
                                new Position(fromX, fromY),
                                position
                        ));
                        break;
                    } else
                        break;
                }

        if (!notRealMove && (!allowedMoves.isEmpty() || checkmate)) {
            return trimmed.stream()
                    .filter(allowedMoves::contains)
                    .collect(Collectors.toList());

        }

        return trimmed;
    }

    boolean isCheck(Tile[][] board) {
        return isCheck(board, false);
    }

    boolean isCheck(Tile[][] board, boolean ignoreCastling) {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                TileType tileType = board[i][j].getTileType();
                if (tileType != TileType.BLANK && tileType.isWhite() != whiteTurn) {
                    TileType defKing = whiteTurn ? TileType.WHITE_KING : TileType.BLACK_KING;
                    List<Movement> nTrim = getMovements(board, j, i, true, ignoreCastling);
                    for (Movement mov : nTrim) {
                        Position pos = mov.highLighted;
                        TileType type = board[pos.y][pos.x].getTileType();
                        if (type == defKing)
                            return true;
                    }
                }
            }

        return false;
    }

    void changeBoardWithMove(Tile[][] board, Movement movement) {
        Position highLighted = movement.highLighted;
        if (movement instanceof SimpleMovement) {
            SimpleMovement simpleMovement = (SimpleMovement) movement;
            Position oldPosition = simpleMovement.oldPosition;
            TileType oldTileType = board[oldPosition.y][oldPosition.x].getTileType();
            board[highLighted.y][highLighted.x].setTileType(oldTileType);
            board[oldPosition.y][oldPosition.x].setTileType(TileType.BLANK);
        } else if (movement instanceof EatMovement) {
            EatMovement eatMovement = (EatMovement) movement;
            Position attackerPosition = eatMovement.attackerPosition;
            TileType attackerTileType = board[attackerPosition.y][attackerPosition.x].getTileType();
            board[highLighted.y][highLighted.x].setTileType(attackerTileType);
            board[attackerPosition.y][attackerPosition.x].setTileType(TileType.BLANK);
        } else if (movement instanceof EnPassant) {
            EnPassant enPassant = ((EnPassant) movement);
            Position oldPosition = enPassant.oldPosition;
            Position deadPawn = enPassant.deadPawn;
            TileType oldTileType = board[oldPosition.y][oldPosition.x].getTileType();
            board[oldPosition.y][oldPosition.x].setTileType(TileType.BLANK);
            board[highLighted.y][highLighted.x].setTileType(oldTileType);
            board[deadPawn.y][deadPawn.x].setTileType(TileType.BLANK);
        } else if (movement instanceof Castling) {
            Castling castling = (Castling) movement;
            Position kingOldPosition = castling.kingOldPosition;
            Position rookNewPosition = castling.rookNewPosition;
            Position rookOldPosition = castling.rookOldPosition;
            TileType kingOldTileType = board[kingOldPosition.y][kingOldPosition.x].getTileType();
            board[highLighted.y][highLighted.x].setTileType(kingOldTileType);
            TileType rookOldTileType = board[rookOldPosition.y][rookOldPosition.x].getTileType();
            board[rookNewPosition.y][rookNewPosition.x].setTileType(rookOldTileType);
            board[kingOldPosition.y][kingOldPosition.x].setTileType(TileType.BLANK);
            board[rookOldPosition.y][rookOldPosition.x].setTileType(TileType.BLANK);
        }
    }

    private List<Movement> calculateCheckResolveMoves(Tile[][] board) {
        ArrayList<Movement> positions = new ArrayList<>();
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++) {
                TileType tileType = board[y][x].getTileType();
                if (tileType != TileType.BLANK && tileType.isWhite() == whiteTurn) {
                    List<Movement> nTrim = getMovements(board, x, y, true);
                    for (Movement movement : nTrim) {
                        Tile[][] boardCopy = copyOfBoard(board);
                        changeBoardWithMove(boardCopy, movement);
                        if (!isCheck(boardCopy)) {
                            positions.add(movement);
                            Position moveHL = movement.highLighted;
                            System.out.println(x + ", " + y + " --> " +
                                    moveHL.x + ", " + moveHL.y + " " +
                                    "Figure: " +
                                    boardCopy[moveHL.y][moveHL.x].getTileType().getName());
                        }
                    }
                }
            }
        return positions;
    }

    private Tile[][] copyOfBoard(Tile[][] originalBoard) {
        Tile[][] copy = new Tile[8][8];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                Tile original = originalBoard[i][j];
                copy[i][j] =
                        new Tile(
                                original.isBlack(),
                                original.getTileType(),
                                ignore -> {
                                },
                                ignore -> {
                                }
                        );
            }
        return copy;
    }

    private boolean isBlack(TileType tileType) {
        return tileType.isWhite();
    }

    private void syncWithView() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                chessView.onChangeTile(i, j, gameBoard[j][i].getTileType());
                gameBoard[i][j].setHighLighted(false);
            }
    }

    private void setupGameBoard() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                final boolean isBlack =
                        (i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0);
                final TileType initFigure = startingLineup(i, j);

                int finalY = i;
                int finalX = j;

                gameBoard[finalY][finalX] = new Tile(
                        isBlack,
                        initFigure,
                        isHighLighted -> chessView.onHighLight(finalX, finalY, isHighLighted, isBlack),
                        tileType -> chessView.onChangeTile(finalX, finalY, tileType)
                );
            }
    }

    void nextTurn() {
        whiteTurn = !whiteTurn;
        allowedMoves.clear();

        List<Movement> positions = calculateCheckResolveMoves(gameBoard);
        allowedMoves.addAll(positions);
    }

    void saveDataForEnPassant(int srcY, int x, int y, TileType targetTileType) {
        if (Math.abs(srcY - y) == 2 &&
                (targetTileType == TileType.BLACK_PAWN ||
                        targetTileType == TileType.WHITE_PAWN))
            lastPawnMove = new Position(x, y);
        else
            lastPawnMove = null;
    }

    void onMove(Movement movement, boolean fromUs) {
        clearHighLight();

        Position position = movement.highLighted;
        int x = position.x;
        int y = position.y;

        changeBoardWithMove(gameBoard, movement);

        Position hlPosition = movement.highLighted;
        Tile targetTile = gameBoard[hlPosition.y][hlPosition.x];
        TileType targetTileType = targetTile.getTileType();

        if (movement instanceof SimpleMovement) {
            SimpleMovement simpleMovement = (SimpleMovement) movement;
            saveDataForEnPassant(simpleMovement.oldPosition.y, x, y, targetTileType);
        }

        checkOnCastling(targetTileType);

        if (netMode) {
            if (fromUs) {
                if (movement instanceof SimpleMovement) {
                    SimpleMovement simpleMovement = (SimpleMovement) movement;
                    Position oldPosition = simpleMovement.oldPosition;
                    actionTransmitter.makeMove(oldPosition.x, oldPosition.y, x, y);
                } else if (movement instanceof EatMovement) {
                    EatMovement eatMovement = (EatMovement) movement;
                    Position oldPosition = eatMovement.attackerPosition;
                    actionTransmitter.makeMove(oldPosition.x, oldPosition.y, x, y);
                } else if (movement instanceof EnPassant) {
                    EnPassant enPassant = (EnPassant) movement;
                    Position oldPosition = enPassant.oldPosition;
                    actionTransmitter.enPassant(oldPosition.x, oldPosition.y);
                } else if (movement instanceof Castling) {
                    Castling castling = (Castling) movement;
                    actionTransmitter.castling(castling.longCastling);
                }
            }
        } else {
            chessView.onMoveFinished(!whiteTurn);
            syncWithView();
        }

        nextTurn();

        boolean castling = movement instanceof Castling;
        boolean longCastling = castling && ((Castling) movement).longCastling;
        boolean check = isCheck(gameBoard);
        checkmate = check && allowedMoves.isEmpty();

        chessView.onNewLogLine(
                new LogLine(
                        highLightSrcX, highLightSrcY,
                        x, y,
                        targetTileType.getName(),
                        check,
                        checkmate,
                        castling,
                        longCastling
                )
        );
    }

    Movement findShowedMove(int x, int y) {
        return showedMoves.stream()
                .filter(m -> m.highLighted.x == x && m.highLighted.y == y)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private void onClickTile(int x, int y) {
        Tile currentTile = gameBoard[y][x];
        if (currentTile.isLighted() && (!netMode || whiteGame == whiteTurn)) {
            Movement movement = findShowedMove(x, y);
            onMove(movement, true);
        } else {
            highLightSrcX = x;
            highLightSrcY = y;
            drawHighLight(x, y);
        }
    }

    public void initGame() {
        setupGameBoard();

        chessView.setOnPressListener(this::onClickTile);
        chessView.setResetOnPressListener(this::reset);

        if (netMode) {
            actionTransmitter.setOnMakeMoveListener((oX, oY, nX, nY) -> {
                Position oldPosition = new Position(oX, oY);
                Position newPosition = new Position(nX, nY);
                Movement move;
                if (gameBoard[nY][nX].getTileType() != TileType.BLANK)
                    move = new EatMovement(oldPosition, newPosition);
                else
                    move = new SimpleMovement(oldPosition, newPosition);
                onMove(move, false);
            });
            actionTransmitter.setOnEnPassantListener((x, y) -> {
                Position newPawnPosition;
                Position deadPawnPosition;
                if (whiteTurn) {
                    if (gameBoard[y][x-1].getTileType() == TileType.BLACK_PAWN) {
                        newPawnPosition = new Position(x - 1, y);
                        deadPawnPosition = new Position(x - 1, y - 1);
                    } else {
                        newPawnPosition = new Position(x + 1, y);
                        deadPawnPosition = new Position(x + 1, y - 1);
                    }
                } else {
                    if (gameBoard[y][x-1].getTileType() == TileType.WHITE_PAWN) {
                        newPawnPosition = new Position(x - 1, y);
                        deadPawnPosition = new Position(x - 1, y + 1);
                    } else {
                        newPawnPosition = new Position(x + 1, y);
                        deadPawnPosition = new Position(x + 1, y + 1);
                    }
                }
                EnPassant enPassant = new EnPassant(
                        newPawnPosition,
                        new Position(x, y),
                        deadPawnPosition
                );
                onMove(enPassant, false);
            });
            actionTransmitter.setOnCastlingListener((longCastling) -> {
                Position kingOldPosition;
                Position kingNewPosition;
                Position rookOldPosition;
                Position rookNewPosition;
                if (whiteTurn) {
                    if (longCastling) {
                        kingOldPosition = new Position(4, 7);
                        kingNewPosition = new Position(2, 7);
                        rookOldPosition = new Position(0, 7);
                        rookNewPosition = new Position(3, 7);
                    } else {
                        kingOldPosition = new Position(4, 7);
                        kingNewPosition = new Position(6, 7);
                        rookOldPosition = new Position(7, 7);
                        rookNewPosition = new Position(5, 7);
                    }
                } else {
                    if (longCastling) {
                        kingOldPosition = new Position(4, 0);
                        kingNewPosition = new Position(2, 0);
                        rookOldPosition = new Position(0, 0);
                        rookNewPosition = new Position(3, 0);
                    } else {
                        kingOldPosition = new Position(4, 0);
                        kingNewPosition = new Position(6, 0);
                        rookOldPosition = new Position(7, 0);
                        rookNewPosition = new Position(5, 0);
                    }
                }
                Castling castling = new Castling(
                        kingOldPosition,
                        kingNewPosition,
                        rookOldPosition,
                        rookNewPosition,
                        longCastling
                );
                onMove(castling, false);
            });
        }
    }

    void drawHighLight(int x, int y) {
        clearHighLight();
        TileType currentTileType = gameBoard[y][x].getTileType();
        if (netMode ? currentTileType.isWhite() == whiteGame : currentTileType.isWhite() == whiteTurn) {
            List<Movement> generatedMoves = getMovements(gameBoard, x, y, false);
            showedMoves.clear();
            showedMoves.addAll(generatedMoves);
            for (Movement movement : generatedMoves) {
                Position pos = movement.highLighted;
                Tile highLightedTile = gameBoard[pos.y][pos.x];
                highLightedTile.setHighLighted(true);
            }
        }
    }

    TileType startingLineup(int i, int j) {
        if (i == 1) return TileType.BLACK_PAWN;
        else if (i == 6) return TileType.WHITE_PAWN;
        else if (i == 0 && (j == 0 || j == 7)) return TileType.BLACK_ROOK;
        else if (i == 7 && (j == 0 || j == 7)) return TileType.WHITE_ROOK;
        else if (i == 0 && (j == 1 || j == 6)) return TileType.BLACK_KNIGHT;
        else if (i == 7 && (j == 1 || j == 6)) return TileType.WHITE_KNIGHT;
        else if (i == 0 && (j == 2 || j == 5)) return TileType.BLACK_BISHOP;
        else if (i == 7 && (j == 2 || j == 5)) return TileType.WHITE_BISHOP;
        else if (i == 0 && j == 3) return TileType.BLACK_QUEEN;
        else if (i == 7 && j == 3) return TileType.WHITE_QUEEN;
        else if (i == 0 && j == 4) return TileType.BLACK_KING;
        else if (i == 7 && j == 4) return TileType.WHITE_KING;
        return TileType.BLANK;
    }

    void reset() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                gameBoard[i][j].setTileType(startingLineup(i, j));
                gameBoard[i][j].setHighLighted(false);
            }
        chessView.cleanLog();
        resetCastling();
        whiteTurn = true;
    }

    void clearHighLight() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                gameBoard[i][j].setHighLighted(false);
    }

    private boolean checkOverLap(Position position) {
        int x = position.x;
        int y = position.y;
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    void resetCastling() {
        allowedCastlingForLWR = true;
        allowedCastlingForLBR = true;
        allowedCastlingForRWR = true;
        allowedCastlingForRBR = true;
    }

    void checkOnCastling(TileType targetTileType) {
        if (targetTileType == TileType.BLACK_ROOK ||
                targetTileType == TileType.WHITE_ROOK ||
                targetTileType == TileType.WHITE_KING ||
                targetTileType == TileType.BLACK_KING) {
            if (highLightSrcY == 7 && highLightSrcX == 7)
                allowedCastlingForRWR = false;
            else if (highLightSrcY == 7 && highLightSrcX == 0)
                allowedCastlingForLWR = false;
            else if (highLightSrcY == 0 && highLightSrcX == 7)
                allowedCastlingForLBR = false;
            else if (highLightSrcY == 0 && highLightSrcX == 0)
                allowedCastlingForRBR = false;
            else if (targetTileType == TileType.WHITE_KING) {
                allowedCastlingForLWR = false;
                allowedCastlingForRWR = false;
            } else {
                allowedCastlingForLBR = false;
                allowedCastlingForRBR = false;
            }
        }
    }
}
