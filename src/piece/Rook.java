package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece
{
    public Rook(int color, int column, int row)
    {
        super(color, column, row);

        type = Type.ROOK;

        if (color == GamePanel.WHITE)
        {
            image = getImage("res/piece/w-rook.png");
        }
        else
        {
            image = getImage("res/piece/b-rook.png");
        }
    }
    public boolean canMove(int targetColumn, int targetRow)
    {
        if (isWithinBoard(targetColumn, targetRow) && isSameSquare(targetColumn, targetRow) == false)
        {
            if (Math.abs(targetColumn - previous_column) <= 7 && (targetRow == previous_row) ||
                Math.abs(targetRow - previous_row) <= 7 && (targetColumn == previous_column))
            {
                if (isValidSquare(targetColumn, targetRow) && pieceIsOnStraightLine(targetColumn, targetRow) == false)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
