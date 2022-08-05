package solutions;

import java.util.*;

public class Tetris{

    private int numberOfBlocks;
    private int[] direction;
    private final Map<Integer, int[][]> resultMap = new HashMap<>();

    public void start(List<int[][]> arg) {
        numberOfBlocks = arg.size();
        createDirectionArray();

        //todo 순서 iter
        List<int[][]> blockList = arg;
        saveMinSizeOfBlocksOnResultMap(blockList);

        int minSize = numberOfBlocks * 9 * 9;
        for (Integer result : resultMap.keySet()) {
            minSize = Math.min(minSize, result);
        }

        showResult(minSize);
    }

    // 이동 횟수에 따른 방향 확인용 array 생성 
    private void createDirectionArray() {
        double size = Math.pow(6 * numberOfBlocks - 5, 2);
        direction = new int[(int) size + 1];

        int num = 1;
        int repeat = 1;
        for (int i = 1; i < direction.length; i++) {
            if ( repeat > (int) Math.round( num / 2.0) ) {
                repeat = 1;
                num++;
                i--;
            } else {
                repeat++;
                direction[i] = num%4;
            }
        }
    }

    private void saveMinSizeOfBlocksOnResultMap(List<int[][]> blockList) {
        int[][] table = createTable();
        
        // 첫 번째 블록 입력 
        stackFirstBlockOnTable(blockList.get(0), table);
        // 나머지 블록 입력 및 마지막 결과 저장
        stackRemainBlocksOnTableAndSaveResult(blockList, table);
    }

    private int[][] createTable() {
        double sqrt = Math.sqrt(numberOfBlocks);
        //Math.ceil() : 소수점 올림
        int num = (int) Math.ceil(sqrt);
        if ( num % 2 == 0 ) {
            num++;
        }
        int tableLength = (int) Math.pow(num,2);
        return new int[tableLength][tableLength];
    }

    private void stackFirstBlockOnTable(int[][] block, int[][] table) {
        int centerStartIndex = ( table.length - 3 ) / 2;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                table[centerStartIndex+i][centerStartIndex+j] = block[i][j];
            }
        }
    }

    private void stackRemainBlocksOnTableAndSaveResult(List<int[][]> blockList, int[][] table) {
        List<int[][]> list = new ArrayList<>();
        for (int indexOfBlock = 1; indexOfBlock < blockList.size(); indexOfBlock++) {
            if ( list.isEmpty() ) {
                for (int rotateNum = 0; rotateNum < 4; rotateNum++) {
                    list.add(stackBlocksOnTable(blockList, indexOfBlock, rotateNum, table));
                }
            } else {
                List<int[][]> copy = new ArrayList<>(list);
                list.clear();
                for (int rotateNum = 0; rotateNum < 4; rotateNum++) {
                    for (int j = 0; j < copy.size(); j++) {
                        list.add(stackBlocksOnTable(blockList, indexOfBlock, rotateNum, copy.get(j)));
                    }
                }
            }
        }
    }

    private int[][] stackBlocksOnTable(List<int[][]> blockList, int indexOfBlock, int rotateNum, int[][] table) {
        int[][] block = blockList.get(indexOfBlock);
        int[][] stackedTable;

        if ( rotateNum == 0 ) { //회전 X
            stackedTable = stackBlockOnTable(table, block);
        } else { //회전 O
            stackedTable = stackBlockOnTable(table, rotateBlock(block, rotateNum));
        }

        // 결과 저장
        if ( indexOfBlock == blockList.size() - 1 ) {
            saveTable(stackedTable);
        }

        return stackedTable;
    }

    // 블록 회전
    // rotateNum : 회전 수
    private int[][] rotateBlock(int[][] block, int rotateNum) {
        int[][] rotatedBlock = new int[3][3];
        for (int i = 0; i < rotateNum; i++) {
            rotatedBlock[0][0] = block[2][0];
            rotatedBlock[0][1] = block[1][0];
            rotatedBlock[0][2] = block[0][0];
            rotatedBlock[1][0] = block[2][1];
            rotatedBlock[1][1] = block[1][1];
            rotatedBlock[1][2] = block[0][1];
            rotatedBlock[2][0] = block[2][2];
            rotatedBlock[2][1] = block[1][2];
            rotatedBlock[2][2] = block[0][2];
        }
        return rotatedBlock;
    }

    private int[][] stackBlockOnTable(int[][] table, int[][] block) {
        int moveCount = 1;
        int centerStartIndex = ( table.length - 3 ) / 2;
        Point point = new Point(centerStartIndex, centerStartIndex);
        return getStackedTable(table, block, moveCount, point);
    }

    private int[][] getStackedTable(int[][] table, int[][] block, int moveCount, Point point) {
        int[][] newTable = deepCopy(table);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ( block[i][j] == 1 ) {
                    // 위치에 놓을 수 있으면 입력, 아니면 이동
                    if ( newTable[point.getY() +i][point.getX() +j] == 1 ) {
                        movePoint(point, moveCount);
                        return getStackedTable(table, block, moveCount+1, point);
                    } else {
                        newTable[point.getY() +i][point.getX() +j] = 1;
                    }
                }
            }
        }
        return newTable;
    }

    // 포인트 위치 변경
    private void movePoint(Point point, int moveCount) {
        int directionNum = direction[moveCount];

        if ( directionNum == 0 ) {
            point.left();
        } else if ( directionNum == 1) {
            point.up();
        } else if ( directionNum == 2) {
            point.right();
        } else {
            point.down();
        }
    }

    private void saveTable(int[][] stackedTable) {
        int size = getSizeOfCombinedBlocks(stackedTable);
        if ( !resultMap.containsKey(size) ) {
            resultMap.put(size, stackedTable);
        }
    }

    // 결합된 블록의 크기
    private int getSizeOfCombinedBlocks(int[][] table) {
        int firstX = table.length;
        int firstY = table.length;
        int lastX = -1;
        int lastY = -1;
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                if ( table[i][j] == 1 ) {
                    firstX = Math.min(firstX, j);
                    firstY = Math.min(firstY, i);
                    lastX = Math.max(lastX, j);
                    lastY = Math.max(lastY, i);
                }
            }
        }

        return (lastX - firstX + 1) * (lastY - firstY + 1);
    }

    private void showResult(int key) {
        System.out.println("Size : " + key);
        int[][] result = resultMap.get(key);
        for (int[] ints : result) {
            for (int i : ints) {
                System.out.print(i);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    static class Point {
        public int x,y;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void up() {
            this.y-=1;
        }

        public void right() {
            this.x+=1;
        }

        public void down() {
            this.y+=1;
        }

        public void left() {
            this.x-=1;
        }

    }
    static int[][] deepCopy(int[][] src){
        int[][] target = new int[src.length][src[0].length];
        for(int i=0; i<src.length; i++){
            for(int j=0; j<src[0].length; j++){
                target[i][j] = src[i][j];
            }
        }
        return target;
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
        arr2[1][0] = 1;
        arr2[1][1] = 1;
        arr2[1][2] = 1;
        arr2[2][0] = 1;
        int[][] arr3 = new int[3][3];
        arr3[0][0] = 1;
        arr3[0][1] = 1;
        arr3[0][2] = 1;
        arr3[1][0] = 1;
        arr3[1][1] = 1;
        arr3[1][2] = 1;
        arr3[2][0] = 1;
        arr3[2][1] = 1;
        arr3[2][2] = 1;

        list.add(arr1);
        list.add(arr2);
        list.add(arr3);

        for (int[][] arr : list) {
            for (int[] ints : arr) {
                for (int i : ints) {
                    System.out.print(i);
                    System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println();
        }
        tetris.start(list);
    }
}
