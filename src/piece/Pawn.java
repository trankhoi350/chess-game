package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece 
{
    public Pawn (int color, int column, int row)
    {
        super(color, column, row); //use the constructor of the parent class to get chess piece's position
        

        type = Type.PAWN;

        if (color == GamePanel.WHITE)
        {
            image = getImage("res/piece/w-pawn.png");
        }
        else
        {
            image = getImage("res/piece/b-pawn.png");
        }
    }
    public boolean canMove(int targetColumn, int targetRow)
    {
        if (isWithinBoard(targetColumn, targetRow) && isSameSquare(targetColumn, targetRow) == false)
        {
            //define the move value base on its color
            int move_value;
            if (color == GamePanel.WHITE)
            {
                move_value = -1;
            }
            else
            {
                move_value = 1;
            }
            //check the hitting piece
            hitting_piece = getHittingPiece(targetColumn, targetRow);
            //1 square movement
            if (targetColumn == previous_column && targetRow == previous_row + move_value && hitting_piece == null)
            {
                return true;
            }
            //2 square movement
            if (targetColumn == previous_column && targetRow == previous_row + move_value * 2 && 
                hitting_piece == null && pieceIsOnStraightLine(targetColumn, targetRow) == false && moved == false)
            {
                return true;
            }
            //Diagonal movement and capture 
            if (Math.abs(targetColumn - previous_column) == 1 &&  targetRow == previous_row + move_value && 
                hitting_piece != null && hitting_piece.color != color)
            {
                    return true;
            }
            
            //En Passant
            if (Math.abs(targetColumn - previous_column) == 1 && targetRow == previous_row + move_value)
            {
                for (Piece piece : GamePanel.simPieces)
                {
                    if (piece.column == targetColumn && piece.row == previous_row && piece.twoStepped == true)
                    {
                        hitting_piece = piece;
                        return true;    
                    }
                }
            }
            
        }
        return false;
    }
}
