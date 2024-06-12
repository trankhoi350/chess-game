package main;
import javax.swing.JPanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import piece.*;


public class GamePanel  extends JPanel implements Runnable
{
    //width and height of game window
    public static final int WIDTH = 1100; 
    public static final int HEIGHT = 700;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();
    Piece active_piece, checking_piece;
    public static Piece castling_piece;

    //PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>(); 
    ArrayList<Piece> promoPieces = new ArrayList<>();

    //COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int current_color = WHITE;

    //BOOLEAN
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameOver;
    boolean stalemate;

    public GamePanel()
    {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        
        setPieces();
        //testPromotion();
        //testIllegal();
        copyPieces(pieces, simPieces);
    }
    public void launchGame()
    {
        gameThread = new Thread(this);
        gameThread.start();
    }


    public void setPieces()
    {
        // White team
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));    
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        // Black team
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 4, 0));
        pieces.add(new King(BLACK, 3, 0));
    }
    public void testPromotion()
    {
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new King(WHITE, 4, 7));
        pieces.add(new King(BLACK, 3, 0));
    }
    public void testIllegal()
    {
        pieces.add(new Pawn(WHITE, 5, 7));
        pieces.add(new King(WHITE, 3, 7));
        pieces.add(new King(BLACK, 0, 3));
        pieces.add(new Bishop(BLACK, 1, 4));
        pieces.add(new Queen(BLACK, 4, 5));
        pieces.add(new Rook(WHITE, 1, 7));
        //pieces.add(new King(WHITE, 2, 4));
        //pieces.add(new King(BLACK, 0, 3));
        //pieces.add(new Queen(WHITE, 2, 1));
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target)
    {
        //since when declare an array, all value in an array will be set to default value, which is 0
        //so we need to clear them out
        target.clear(); 
        for (int i = 0; i < source.size(); i++)
        {
            target.add(source.get(i)); //add element from source to target
        }
    }
    

    @Override
    public void run()
    {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null)
        {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1)
            {
                //1.UPDATE: update information such as position
                update();
                //2.DRAW: draw the screen with update information
                repaint();
                delta--;
            }
        }
    }

    private void update() //update things in the game like position of pieces or number of piece remained
    {
        if (promotion)
        {
            promoting();
        }
        else if (gameOver == false && stalemate == false)
        {
            //Mouse button pressed
            if (mouse.pressed)
            {
                if (active_piece == null) //the player is not holding the piece right now
                {
                    //if the active piece is not hold, check if we can pick it up
                    for (Piece piece : simPieces)
                    {
                    //if the  mouse is on an ally piece, pick it up as the active_piece
                        if (piece.color == current_color && 
                            piece.column == mouse.x / Board.SQUARE_SIZE && 
                            piece.row == mouse.y / Board.SQUARE_SIZE)
                        {
                            active_piece = piece;
                        }
                    }
                }
                else
                {
                    simulate();
                }
            }
            if (mouse.pressed == false)
            {
                if (active_piece != null)
                {
                    if (validSquare)
                    {
                        //MOVE CONFIRMED
                        //update a piece list in case a piece has been captured and removed during the simulation
                        copyPieces(simPieces, pieces);
                        active_piece.updatePosition();
                        if (castling_piece != null)
                        {
                            castling_piece.updatePosition();
                        }
                        
                        if (isKingInCheck() && isCheckmate())
                        {
                            //Game over
                            gameOver = true;
                        }
                        else if (isStalemate() && isKingInCheck() == false)
                        {
                            stalemate = true;
                        }
                        else
                        {
                            if (canPromote())
                            {
                                promotion = true;
                            }
                            else
                            {
                                changePlayer();
                            }
                        }
                    }
                    else
                    {
                        //The move is canceled so reset everything
                        copyPieces(pieces, simPieces);
                        active_piece.resetPosition();
                        active_piece = null;
                    }
                }
            }
        }
      
    }
    private void simulate()
    {
        canMove = false;
        validSquare = false;

        //Reset the piece list in every loop
        //This is basically for restoring the removed piece during the simulation
        copyPieces(pieces, simPieces);

        //Reset the castling piece's position
        if (castling_piece != null)
        {
            castling_piece.column = castling_piece.previous_column;
            castling_piece.x = castling_piece.getX(castling_piece.column);
            castling_piece = null;
        }

        //if a piece is being held, update its position
        active_piece.x = mouse.x - Board.HALF_SQUARE_SIZE; 
        active_piece.y = mouse.y - Board.HALF_SQUARE_SIZE;
        active_piece.column = active_piece.getColumn(active_piece.x);
        active_piece.row = active_piece.getRow(active_piece.y);

        //check if the piece is hovering over reachable square
        if (active_piece.canMove(active_piece.column, active_piece.row))
        {
            canMove = true;
            //if we can capture a piece, remove it from the list
            if (active_piece.hitting_piece != null)
            {
                simPieces.remove(active_piece.hitting_piece.getIndex());
            }
            
            checkCastling();
            
            if (isIllegal(active_piece) == false && opponentCanCaptureKing() == false)
            {
                validSquare = true;
            }
            
        }
    }
    
    private boolean isIllegal(Piece king)
    {
        if (king.type == Type.KING)
        {
            for (Piece piece : simPieces)
            {
                if (piece != king && piece.color != king.color && piece.canMove(king.column, king.row))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean opponentCanCaptureKing()
    {
        Piece king = getKing(false);
        
        for (Piece piece : simPieces)
        {
            if (piece.color != king.color && piece.canMove(king.column, king.row))
            {
                return true;
            }
        }
        return false;
    }
    private boolean isKingInCheck()
    {
        Piece king = getKing(true);
        if (active_piece.canMove(king.column, king.row))
        {
            checking_piece = active_piece;
            return true;
        }
        else
        {
            checking_piece = null;
        }
        return false;
    }
    
    private Piece getKing(boolean opponent)
    {
        Piece king = null;

        for (Piece piece: simPieces)
        {
            if (opponent)
            {
                if (piece.type == Type.KING && piece.color != current_color)
                {
                    king = piece;
                }
            }
            else
            {
                if (piece.type == Type.KING && piece.color == current_color)
                {
                    king = piece;
                }
            }
        }
        return king;
    }
    private boolean isCheckmate()
    {
        Piece king = getKing(true);

        if (kingCanMove(king))
        {
            return false;
        }
        else
        {
            //Check if we can block the checkmate with our pieces
            //Check the position of the checking piece and the king in check
            int column_difference = Math.abs(checking_piece.column - king.column);
            int row_difference = Math.abs(checking_piece.row - king.row);

            if (column_difference == 0)
            {
                //The checking piece is attacking vertically
                if (checking_piece.row < king.row)
                {
                    //The checking piece is above the King
                    for (int row = checking_piece.row; row < king.row; row++)
                    {
                        for (Piece piece : simPieces)
                        {
                            if (piece != king && piece.color != current_color && piece.canMove(checking_piece.column, row))
                            {
                                return false;
                            }
                        }
                    }
                }
                if (checking_piece.row > king.row)
                {
                    //The checking piece is below the King
                    for (int row = checking_piece.row; row > king.row; row--)
                    {
                        for (Piece piece : simPieces)
                        {
                            if (piece != king && piece.color != current_color && piece.canMove(checking_piece.column, row))
                            {
                                return false;
                            }
                        }
                    }
                }
            }
            else if (row_difference == 0)
            {
                //The checking piece is attacking horizontally
                if (checking_piece.column < king.column)
                {
                    //The checking piece is attacking at the left
                    for (int col = checking_piece.column; col < king.column; col++)
                    {
                        for (Piece piece : simPieces)
                        {
                            if (piece != king && piece.color != current_color && piece.canMove(col, checking_piece.row))
                            {
                                return false;
                            }
                        }
                    }
                }
                if (checking_piece.column > king.column)
                {
                    //The checking piece is attacking at the right
                    for (int col = checking_piece.column; col > king.column; col--)
                    {
                        for (Piece piece : simPieces)
                        {
                            if (piece != king && piece.color != current_color && piece.canMove(col, checking_piece.row))
                            {
                                return false;
                            }
                        }
                    }
                }
            }
            else if (column_difference == row_difference)
            {
                //The checking piece is attacking diagonally
                if (checking_piece.row < king.row)
                {
                    //The checking piece is above the king
                    if (checking_piece.column < king.column)
                    {
                        //The checking piece is upper left
                        for (int col = checking_piece.column, row = checking_piece.row; col < king.column; col++, row++)
                        {
                            for (Piece piece : simPieces)
                            {
                                if (piece != king && piece.color != current_color && piece.canMove(col, row))
                                {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checking_piece.column > king.column)
                    {
                        //The checking piece is upper right
                        for (int col = checking_piece.column, row = checking_piece.row; col > king.column; col--, row++)
                        {
                            for (Piece piece : simPieces)
                            {
                                if (piece != king && piece.color != current_color && piece.canMove(col, row))
                                {
                                    return false;
                                }
                            }
                        }
                    }
                }
                if (checking_piece.row > king.row)
                {
                    //The checking piece is below the king
                    if (checking_piece.column < king.column)
                    {
                        //The checking piece is lower left
                        for (int col = checking_piece.column, row = checking_piece.row; col < king.column; col++, row--)
                        {
                            for (Piece piece : simPieces)
                            {
                                if (piece != king && piece.color != current_color && piece.canMove(col, row))
                                {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checking_piece.column > king.column)
                    {
                        //The checking piece is lower right
                        for (int col = checking_piece.column, row = checking_piece.row; col > king.column; col--, row--)
                        {
                            for (Piece piece : simPieces)
                            {
                                if (piece != king && piece.color != current_color && piece.canMove(col, row))
                                {
                                    return false;
                                }
                            }
                        }

                    }
                }
            }
        }
        return true;
    }
    private boolean kingCanMove(Piece king)
    {
        //Simulate if there is any square that the king can move to
        if (isValidMove(king, 0, -1)) {return true;}
        if (isValidMove(king, 0, 1)) {return true;}
        if (isValidMove(king, -1, 0)) {return true;}
        if (isValidMove(king, 1, 0)) {return true;}
        if (isValidMove(king, -1, -1)) {return true;}
        if (isValidMove(king, -1, 1)) {return true;}
        if (isValidMove(king, 1, -1)) {return true;}
        if (isValidMove(king, 1, 1)) {return true;}
        return false;
    }
    private boolean isValidMove(Piece king, int column, int row)
    {
        boolean isValidMove = false;

        //Update the king's position for a second
        king.column += column;
        king.row += row;

        if (king.canMove(king.column, king.row))
        {
            if (king.hitting_piece != null)
            {
                simPieces.remove(king.hitting_piece.getIndex());
            }
            if (isIllegal(king) == false)
            {
                isValidMove = true;
            }
        }

        //Reset the king's position and restore the removed piece
        king.resetPosition();
        copyPieces(pieces, simPieces);
        return isValidMove;
    }

    private boolean isStalemate()
    {
        int count = 0;
        //Count the number of pieces
        for (Piece piece : simPieces)
        {
            if (piece.color != current_color)
            {
                count++;
            }
        }
        //If only one piece (the king) is left
        if (count == 1)
        {
            if (kingCanMove(getKing(true)) == false)
            {
                return true;
            }
        }


        return false;
    }

    private void checkCastling()
    {
        if (castling_piece != null)
        {
            if (castling_piece.column == 0)  //This is Rook on the left 
            {
                castling_piece.column += 3;
            }
            if (castling_piece.column == 7)  //This is Rook on the right
            {
                castling_piece.column -= 2;
            }
            castling_piece.x = castling_piece.getX(castling_piece.column);
        }
    }
    private void changePlayer()
    {
        if (current_color == WHITE)
        {
            current_color = BLACK;
            //Reset black's two stepped status
            for (Piece piece : pieces)
            {
                if (piece.color == BLACK)
                {
                    piece.twoStepped = false;
                }
            }
        }
        else
        {
            current_color = WHITE;
            //Reset white's two stepped status
            for (Piece piece : pieces)
            {
                if (piece.color == WHITE)
                {
                    piece.twoStepped = false;
                }
            }
        }
        active_piece = null;
    }

    private boolean canPromote()
    {
        if (active_piece.type == Type.PAWN)
        {
            if (current_color == WHITE && active_piece.row == 0 || current_color == BLACK && active_piece.row == 7)
            {
                promoPieces.clear();
                promoPieces.add(new Rook(current_color, 9, 2));
                promoPieces.add(new Knight(current_color, 9, 3));
                promoPieces.add(new Bishop(current_color, 9, 4));
                promoPieces.add(new Queen(current_color, 9, 5));
                return true;
            }
        }
        return false;
    }
    
    private void promoting()
    {
        if (mouse.pressed)
        {
            for (Piece piece : promoPieces)
            {
                if (piece.column == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE)
                {
                    switch (piece.type) 
                    {
                        case ROOK:
                            simPieces.add(new Rook(current_color, active_piece.column, active_piece.row));
                            break;
                        case KNIGHT:
                            simPieces.add(new Knight(current_color, active_piece.column, active_piece.row));
                            break;
                        case BISHOP:
                            simPieces.add(new Bishop(current_color, active_piece.column, active_piece.row));
                            break;
                        case QUEEN:
                            simPieces.add(new Queen(current_color, active_piece.column, active_piece.row));
                            break;
                        default:
                            break;
                    }
                    simPieces.remove(active_piece.getIndex());
                    copyPieces(simPieces, pieces);
                    active_piece = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g); //drawing the chess board, chess pieces,...
        Graphics2D g2 = (Graphics2D) g;
        
        //Draw a chessboard
        board.draw(g2);
        //Draw a chess pieces
        for (Piece p : simPieces)
        {
            p.draw(g2);
        }
        if (active_piece != null)
        {
            if (canMove)
            {
                if (isIllegal(active_piece) || opponentCanCaptureKing())
                {
                    g2.setColor(Color.red);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(active_piece.column * Board.SQUARE_SIZE, active_piece.row * Board.SQUARE_SIZE, 
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
                else
                {
                    //when the player is holding a piece and start to move the piece, draw a opaque rectangle 
                    //to define where the piece will come to
                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(active_piece.column * Board.SQUARE_SIZE, active_piece.row * Board.SQUARE_SIZE, 
                                Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }
           

            // Draw the active piece in the end so that it won't be hidden by the board or the square
            active_piece.draw(g2);
        }

        //STATUS MESSAGE
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if (promotion)
        {
            g2.drawString("Promote to", 680, 150);
            for (Piece piece : promoPieces)
            {
                g2.drawImage(piece.image, piece.getX(piece.column), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        }
        else
        {
            if (current_color == WHITE)
            {
                g2.drawString("White's turn", 760, 600);
                if (checking_piece != null && checking_piece.color == BLACK)
                {
                    g2.setColor(Color.red);
                    g2.drawString("The King is checked!", 690, 550);
                }
            }
            else
            {
                g2.drawString("Black's turn", 760, 150);
                if (checking_piece != null && checking_piece.color == WHITE)
                {
                    g2.setColor(Color.red);
                    g2.drawString("The King is checked!", 690, 100);
                }
            }
        }
        if (gameOver)
        {
            String result = "";
            if (current_color == WHITE)
            {
                result = "White wins!";
            }
            else
            {
                result = "Black wins!";
            }
            g2.setFont(new Font("Arial", Font.PLAIN, 75));
            g2.setColor(Color.green);
            g2.drawString(result, 200, 375);
        }
        if (stalemate)
        {
            g2.setFont(new Font("Arial", Font.PLAIN, 75));
            g2.setColor(Color.ORANGE);
            g2.drawString("Stalemate", 200, 375);
        }
    }
}