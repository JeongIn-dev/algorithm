package solutions;

import java.util.*;

public class Tetris{

    private static int numberOfBlocks;
    private static final Map<Integer, int[][]> resultMap = new HashMap<>();

    public int[][] start(List<int[][]> arg) {
        numberOfBlocks = arg.size();
        //todo arg 순서 iter
        List<int[][]> blockList = new ArrayList<>();
        saveMinSizeOfBlocks(blockList);

        int minSize = 0;
        for (Integer result : resultMap.keySet()) {
            minSize = Math.min(minSize, result);
        }
        return resultMap.get(minSize);
    }

    private void saveMinSizeOfBlocks(List<int[][]> blockList) {
        int[][] table = createTable();
        stackFirstBlockOnTable(blockList.get(0), table);
        for (int indexOfBlock = 0; indexOfBlock < blockList.size(); indexOfBlock++) {
            stackRemainBlocksOnTable(blockList, indexOfBlock, 0, table);
        }
    }

    private int[][] createTable() {
        int tableLength = ( numberOfBlocks * 2 - 1 ) * 3;
        return new int[tableLength][tableLength];
    }

    private void stackFirstBlockOnTable(int[][] block, int[][] table) {
        int centerStartIndex = ( numberOfBlocks - 1 ) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                table[centerStartIndex+i][centerStartIndex+j] = block[i][j];
            }
        }
    }

    private void stackRemainBlocksOnTable(List<int[][]> blockList, int indexOfBlock, int rotateNum, int[][] table) {
        if ( indexOfBlock == blockList.size() ) {
            return;
        }
        if ( rotateNum == 3 ) {
            return;
        }
        //4 방향 분기
        List<int[][]> newBlockList = new ArrayList<>(blockList);
        newBlockList.set(indexOfBlock, rotateBlock(blockList.get(indexOfBlock)));
        int[][] newTable = new int[3][3];
        System.arraycopy(table, 0, newTable, 0, table.length);
        stackRemainBlocksOnTable(newBlockList, indexOfBlock, rotateNum+1, newTable);

        //block 넣기
        int[][] block = blockList.get(indexOfBlock);
        stackBlockOnCenterOfTable(table, block);
    }

    private int[][] rotateBlock(int[][] block) {
        int[][] rotatedBlock = new int[3][3];
        rotatedBlock[0][0] = block[2][0];
        rotatedBlock[0][1] = block[1][0];
        rotatedBlock[0][2] = block[0][0];
        rotatedBlock[1][0] = block[2][1];
        rotatedBlock[1][1] = block[1][1];
        rotatedBlock[1][2] = block[0][1];
        rotatedBlock[2][0] = block[2][2];
        rotatedBlock[2][1] = block[1][2];
        rotatedBlock[2][2] = block[0][2];
        return rotatedBlock;
    }

    private void stackBlockOnCenterOfTable(int[][] table, int[][] block) {
        int moveCount = 0;
        if ( getStackedTable(table, block, moveCount) == null ) {
            //todo move

        }
    }

    private int[][] getStackedTable(int[][] table, int[][] block, int moveCount) {
        int centerStartIndex = ( numberOfBlocks - 1 ) * 3;
        int[][] newTable = new int[3][3];
        System.arraycopy(table, 0, newTable, 0, table.length);

        //stack
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ( block[i][j] == 1 ) {
                    if ( newTable[centerStartIndex +i][centerStartIndex +j] == 1 ) {
                        block = moveBlock(block, moveCount);
                        return getStackedTable(table, block, moveCount+1);
                    } else {
                        newTable[centerStartIndex +i][centerStartIndex +j] = 1;
                    }
                }
            }
        }
        return newTable;
    }

    private int[][] moveBlock(int[][] block, int moveCount) {
        int move = (int) Math.round(moveCount / 2.0);
        int directionNum = moveCount%4;
        if ( directionNum == 0 ) {

        } else if ( directionNum == 1) {

        } else if ( directionNum == 2) {

        } else if ( directionNum == 3) {

        } else {
           return null;
        }
    }

    public static void main(String[] args) {
        Tetris tetris = new Tetris();
        List<int[][]> list = new ArrayList<>();
        int[][] arr1 = new int[3][3];
        arr1[1][0] = 1;
        arr1[1][1] = 1;
        arr1[1][2] = 1;
        arr1[2][0] = 1;
        arr1[2][1] = 1;
        int[][] arr2 = new int[3][3];
        list.add(arr1);
        list.add(arr2);
        int[][] result = tetris.start(list);

        for (int[] ints : result) {
            for (int i : ints) {
                System.out.print(i);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    enum Direction {
        UP(1), RIGHT(2), Down(3), LEFT(4);

        Direction(int num) {
            this.num = num;
        }

        int num;


    }
}
