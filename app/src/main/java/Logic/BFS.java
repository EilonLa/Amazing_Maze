package Logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import UI.Board;

public class BFS {

    private Queue<Tile> mQueue;

    public BFS() {
        mQueue = new LinkedList<>();
    }

    public ArrayList<Tile> FindNeighbours(Tile tiles[][], Tile pointer, boolean gameMode) {
        ArrayList<Tile> neighbours = new ArrayList<>();
        if (gameMode){
            if (pointer.GetRow() != 0 && tiles[pointer.GetRow() - 1][pointer.GetCol()].IsStepped() && !tiles[pointer.GetRow() - 1][pointer.GetCol()].IsVisited())//first row
                neighbours.add(tiles[pointer.GetRow() - 1][pointer.GetCol()]);

            if (pointer.GetCol() != 0 && tiles[pointer.GetRow()][pointer.GetCol() - 1].IsStepped() && !tiles[pointer.GetRow()][pointer.GetCol() - 1].IsVisited())//first column
                neighbours.add(tiles[pointer.GetRow()][pointer.GetCol() - 1]);

            if (pointer.GetRow() != Board.NUM_OF_ROWS - 1 && tiles[pointer.GetRow() + 1][pointer.GetCol()].IsStepped() && !tiles[pointer.GetRow() + 1][pointer.GetCol()].IsVisited())//last row
                neighbours.add(tiles[pointer.GetRow() + 1][pointer.GetCol()]);

            if (pointer.GetCol() != Board.NUM_OF_COLS - 1 && tiles[pointer.GetRow()][pointer.GetCol() + 1].IsStepped() && !tiles[pointer.GetRow()][pointer.GetCol() + 1].IsVisited())//last col
                neighbours.add(tiles[pointer.GetRow()][pointer.GetCol() + 1]);

        }
        else {
            if (pointer.GetRow() != 0 && !tiles[pointer.GetRow() - 1][pointer.GetCol()].IsWall() && !tiles[pointer.GetRow() - 1][pointer.GetCol()].IsVisited())//first row
                neighbours.add(tiles[pointer.GetRow() - 1][pointer.GetCol()]);

            if (pointer.GetCol() != 0 && !tiles[pointer.GetRow()][pointer.GetCol() - 1].IsWall() && !tiles[pointer.GetRow()][pointer.GetCol() - 1].IsVisited())//first column
                neighbours.add(tiles[pointer.GetRow()][pointer.GetCol() - 1]);

            if (pointer.GetRow() != Board.NUM_OF_ROWS - 1 && !tiles[pointer.GetRow() + 1][pointer.GetCol()].IsWall() && !tiles[pointer.GetRow() + 1][pointer.GetCol()].IsVisited())//last row
                neighbours.add(tiles[pointer.GetRow() + 1][pointer.GetCol()]);

            if (pointer.GetCol() != Board.NUM_OF_COLS - 1 && !tiles[pointer.GetRow()][pointer.GetCol() + 1].IsWall() && !tiles[pointer.GetRow()][pointer.GetCol() + 1].IsVisited())//last col
                neighbours.add(tiles[pointer.GetRow()][pointer.GetCol() + 1]);
        }
        return neighbours;
    }

    public ArrayList<Tile> Bfs(Tile tiles[][], Tile node, boolean gameMode) {
        ArrayList<Tile> list = new ArrayList<>();
        mQueue.add(node);
        node.SetVisited(true);
        while (!mQueue.isEmpty()) {
            Tile element = mQueue.remove();
            list.add(element);
            ArrayList<Tile> neighbours = FindNeighbours(tiles, element, gameMode);
            for (Tile tile : neighbours){
                if (tile != null && !tile.IsVisited()) {
                    mQueue.add(tile);
                    tile.SetVisited(true);
                }

            }
        }
        return list;
    }
}
