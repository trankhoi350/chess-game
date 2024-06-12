package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece
{
    public Bishop(int color, int column, int row)
    {
        super(color, column, row);

        type = Type.BISHOP;

        if (color == GamePanel.WHITE)
        {
            image = getImage("res/piece/w-bishop.png");
        }
        else
        {
            image = getImage("res/piece/b-bishop.png");
        }
    }
    public boolean canMove(int targetColumn, int targetRow)
    {
        if (isWithinBoard(targetColumn, targetRow) && isSameSquare(targetColumn, targetRow) == false)
        {
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
