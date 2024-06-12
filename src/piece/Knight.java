package piece;

import main.GamePanel;
import main.Type;

public class Knight extends Piece
{
    public Knight (int color, int column, int row)
    {
        super(color, column, row);

        type = Type.KNIGHT;

        if (color == GamePanel.WHITE)
        {
            image = getImage("res/piece/w-knight.png");
        }
        else
        {
            image = getImage("res/piece/b-knight.png");
        }
    }
    public boolean canMove(int targetColumn, int targetRow)
    {
        if (isWithinBoard(targetColumn, targetRow))
        {
            if (Math.abs(targetColumn - previous_column) == 2 && Math.abs(targetRow - previous_row) == 1 ||
                Math.abs(targetRow - previous_row) == 2 && Math.abs(targetColumn - previous_column) == 1)
            {
                if (isValidSquare(targetColumn, targetRow))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
