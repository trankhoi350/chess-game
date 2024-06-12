package piece;

import main.GamePanel;
import main.Type;

public class Queen extends Piece
{
    public Queen(int color, int column, int row)
    {
        super (color, column, row);

        type = Type.QUEEN;

        if (color == GamePanel.WHITE)
        {
            image = getImage("res/piece/w-queen.png");
        }
        else
        {
            image = getImage("res/piece/b-queen.png");
        }
    }
    public boolean canMove(int targetColumn, int targetRow)
    {
        if (isWithinBoard(targetColumn, targetRow) && isSameSquare(targetColumn, targetRow) == false)
        {
            //Moves vertically and horizontally
            if (targetColumn == previous_column || targetRow == previous_row)
            {
                if (isValidSquare(targetColumn, targetRow) && pieceIsOnStraightLine(targetColumn, targetRow) == false)
                {
                    return true;
                }
            }
            //Moves diagonally
            if (Math.abs(targetColumn - previous_column) == Math.abs(targetRow - previous_row))
            {
                if (isValidSquare(targetColumn, targetRow) && pieceIsOnDiagonalLine(targetColumn, targetRow) == false)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
