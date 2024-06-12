package piece;

import main.GamePanel;
import main.Type;

public class King extends Piece
{
    public King(int color, int column, int row)
    {
        super (color, column, row);

        type = Type.KING;

        if (color == GamePanel.WHITE)
        {
            image = getImage("res/piece/w-king.png");
        }
        else
        {
            image = getImage("res/piece/b-king.png");
        }
    }
    public boolean canMove(int targetColumn, int targetRow)
    {
        if (isWithinBoard(targetColumn, targetRow))
        {
            //MOVEMENT
            if (Math.abs(targetColumn - previous_column) + Math.abs(targetRow - previous_row) == 1 ||
                Math.abs(targetColumn - previous_column) * Math.abs(targetRow - previous_row) == 1)
            {
                if (isValidSquare(targetColumn, targetRow))
                {
                    return true;
                }
            }
        }
        //CASTLING
        if (moved == false)
        {
            //Right castling
            if (targetColumn == previous_column + 2 && targetRow == previous_row && pieceIsOnStraightLine(targetColumn, targetRow) == false)
            {
                for (Piece piece : GamePanel.simPieces)
                {
                    if (piece.column == previous_column + 3 && piece.row == previous_row && piece.moved == false) //this is position of Rook
                    {
                        GamePanel.castling_piece = piece;
                        return true;
                    }
                }
            }
            //Left castling
            if (targetColumn == previous_column - 2 && targetRow == previous_row && pieceIsOnStraightLine(targetColumn, targetRow) == false)
            {
                for (Piece piece : GamePanel.simPieces)
                {
                    if (piece.column == previous_column - 4 && piece.row == previous_row && piece.moved == false)
                    {
                        GamePanel.castling_piece = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
