package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.Board;
import main.GamePanel;
import main.Type;

public class Piece 
{
    public Type type;
    public BufferedImage image;
    public int x, y;
    public int row, column, previous_row, previous_column;
    public int color;
    public Piece hitting_piece;
    public boolean moved, twoStepped;
    public Piece(int color, int column, int row)
    {
        this.color = color;
        this.column = column;
        this.row = row;
        x = getX(column);
        y = getY(row);
        previous_column = column;
        previous_row = row;
    }

    public BufferedImage getImage(String imagePath)
    {
        BufferedImage image = null;

        try 
        {
            image = ImageIO.read(new FileInputStream(imagePath));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return image;
    }

    public int getX(int column)
    {
        return column * Board.SQUARE_SIZE;
    }
    public int getY(int row)
    {
        return row * Board.SQUARE_SIZE;
    }
    public int getColumn(int x)
    {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }
    public int getRow(int y)
    {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }
    public int getIndex()
    {
        for (int index = 0; index < GamePanel.simPieces.size(); index++)
        {
            if (GamePanel.simPieces.get(index) == this)
            {
                return index;
            }
        }
        return 0;
    }
    public void updatePosition()
    {
        //to check En Passant
        if (type == Type.PAWN)
        {
            if (Math.abs(row - previous_row) == 2)
            {
                twoStepped = true;
            }
        }
        
        //adjust the position of chess piece to a center of a square
        x = getX(column);
        y = getY(row);
        //update previous row and columns since the move has been confirmed
        previous_column = getColumn(x);
        previous_row = getRow(y);
        moved = true;
    }
    public void resetPosition()
    {
        column = previous_column;
        row = previous_row;
        x = getX(column);
        y = getY(row);
    }
    public boolean canMove(int tagetColumn, int targetRow)
    {
        return false;
    }
    public boolean isWithinBoard(int targetColumn, int targetRow)
    {
        if (targetColumn >= 0 && targetColumn <= 7 && targetRow >= 0 && targetRow <= 7)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean isSameSquare(int targetColumn, int targetRow)
    {
        if (targetColumn == previous_column && targetRow == previous_row)
        {
            return true;
        }
        return false;
    }
    public Piece getHittingPiece(int targetColumn, int targetRow)
    {
        for (Piece piece : GamePanel.simPieces)
        {
            if (piece.column == targetColumn && piece.row == targetRow && piece != this)
            {
                return piece;
            }
        }
        return null;
    }
    public boolean isValidSquare(int targetColumn, int targetRow)
    {
        hitting_piece = getHittingPiece(targetColumn, targetRow);
        if (hitting_piece == null) //this square is VACANT
        {   
            return true; 
        }
        else
        {
            if (hitting_piece.color != this.color)
            {
                return true;
            }
            else
            {
                hitting_piece = null;
            }
        }
        return false;
    }
    public boolean pieceIsOnStraightLine(int targetColumn, int targetRow)
    {
        //When this piece is moving to the left
        for (int col = previous_column - 1; col > targetColumn; col--)
        {
            for (Piece piece : GamePanel.simPieces)
            {
                if (piece.column == col && piece.row == targetRow)
                {
                    hitting_piece = piece;
                    return true;
                }
            }
        }
        //When this piece is moving to the right
        for (int col = previous_column + 1; col < targetColumn; col++)
        {
            for (Piece piece : GamePanel.simPieces)
            {
                if (piece.column == col && piece.row == targetRow)
                {
                    hitting_piece = piece;
                    return true;
                }
            }
        }
        //When this piece is moving up
        for (int r = previous_row - 1; r > targetRow; r--)
        {
            for (Piece piece : GamePanel.simPieces)
            {
                if (piece.row == r && piece.column == targetColumn)
                {
                    hitting_piece = piece;
                    return true;
                }
            }
        }
        //When this piece is moving down
        for (int r = previous_row + 1; r < targetRow; r++)
        {
            for (Piece piece : GamePanel.simPieces)
            {
                if (piece.row == r && piece.column == targetColumn)
                {
                    hitting_piece = piece;
                    return true;
                }
            }
        }
        return false;
    }
    public boolean pieceIsOnDiagonalLine(int targetColumn, int targetRow)
    {
        if (targetRow < previous_row)
        {
            //The piece moves up left
            for (int col = previous_column - 1; col > targetColumn; col--)
            {
                int difference = Math.abs(col - previous_column);
                for (Piece piece : GamePanel.simPieces)
                {
                    if (piece.column == col && piece.row == previous_row - difference)
                    {
                        hitting_piece = piece;
                        return true;
                    }
                }
            }

            //The piece moves up right
            for (int col = previous_column + 1; col < targetColumn; col++)
            {
                int difference = Math.abs(col - previous_column);
                for (Piece piece : GamePanel.simPieces)
                {
                    if (piece.column == col && piece.row == previous_row - difference)
                    {
                        hitting_piece = piece;
                        return true;
                    }
                }
            }
        }

        if (targetRow > previous_row)
        {
            //The piece moves down left
            for (int col = previous_column - 1; col > targetColumn; col--)
            {
                int difference = Math.abs(col - previous_column);
                for (Piece piece :  GamePanel.simPieces)
                {
                    if (piece.column == col && piece.row == previous_row + difference)
                    {
                        hitting_piece = piece;
                        return true;
                    }
                }
            }
            //The piece moves down right
            for (int col = previous_column + 1; col < targetColumn; col++)
            {
                int difference = Math.abs(col - previous_column);
                for (Piece piece : GamePanel.simPieces)
                {
                    if (piece.column == col && piece.row == previous_row + difference)
                    {
                        hitting_piece = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public void draw(Graphics2D g2)
    {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}
