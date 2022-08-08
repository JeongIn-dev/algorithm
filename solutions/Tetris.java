package solutions;

import java.util.*;

public class Tetris{

    private int numberOfBlocks;
    private int[] direction;
    private final Map<Integer, char[][]> resultMap = new HashMap<>();

    public void start(List<char[][]> arg) {
        numberOfBlocks = arg.size();
        createDirectionArray();

        //todo 순서 iter
        List<char[][]> blockList = arg;
        Set<Integer> selected = new HashSet<>();

        for (int i = 0; i < arg.size(); i++) {
            selected.add(i);
            int[] indies = choiceNotSelectedIndex(arg.size(), selected);
            List<char[][]> permutatedBlockList = new ArrayList<>();
            for ( int index : indies) {
                permutatedBlockList.add(blockList.get(index));
            }
            saveMinSizeOfBlocksOnResultMap(permutatedBlockList);
        }


        int minSize = numberOfBlocks * 9 * 9;
        for (Integer result : resultMap.keySet()) {
            minSize = Math.min(minSize, result);
        }

        showResult(minSize);
    }

    private int[] choiceNotSelectedIndex(int size, Set<Integer> selected) {
        for (int j = 0; j < size; j++) {
            if ( selected.contains(j) ) {
                continue;
            } else {
                selected.add(j);
                if ( selected.size() == size ) {
                    return selected.stream()
                            .mapToInt(Integer::intValue)
                            .toArray();
                } else {
                    return choiceNotSelectedIndex(size, selected);
                }
            }
        }
        return selected.stream().mapToInt(Integer::intValue).toArray();
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

    private void saveMinSizeOfBlocksOnResultMap(List<char[][]> blockList) {
        char[][] table = createTable();
        
        // 첫 번째 블록 입력 
        stackFirstBlockOnTable(blockList.get(0), table);
        // 나머지 블록 입력 및 마지막 결과 저장
        stackRemainBlocksOnTableAndSaveResult(blockList, table);
    }

    private char[][] createTable() {
        double sqrt = Math.sqrt(numberOfBlocks);
        //Math.ceil() : 소수점 올림
        int num = (int) Math.ceil(sqrt);
        if ( num % 2 == 0 ) {
            num++;
        }
        int tableLength = (int) Math.pow(num,2);
        return new char[tableLength][tableLength];
    }

    private void stackFirstBlockOnTable(char[][] block, char[][] table) {
        int centerStartIndex = ( table.length - 3 ) / 2;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                table[centerStartIndex+i][centerStartIndex+j] = block[i][j];
            }
        }
    }

    private void stackRemainBlocksOnTableAndSaveResult(List<char[][]> blockList, char[][] table) {
        List<char[][]> list = new ArrayList<>();
        for (int indexOfBlock = 1; indexOfBlock < blockList.size(); indexOfBlock++) {
            if ( list.isEmpty() ) {
                for (int rotateNum = 0; rotateNum < 4; rotateNum++) {
                    list.add(stackBlocksOnTable(blockList, indexOfBlock, rotateNum, table));
                }
            } else {
                List<char[][]> copy = new ArrayList<>(list);
                list.clear();
                for (int rotateNum = 0; rotateNum < 4; rotateNum++) {
                    for (int j = 0; j < copy.size(); j++) {
                        list.add(stackBlocksOnTable(blockList, indexOfBlock, rotateNum, copy.get(j)));
                    }
                }
            }
        }
    }

    private char[][] stackBlocksOnTable(List<char[][]> blockList, int indexOfBlock, int rotateNum, char[][] table) {
        char[][] block = blockList.get(indexOfBlock);
        char[][] stackedTable;

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
    private char[][] rotateBlock(char[][] block, int rotateNum) {
        char[][] rotatedBlock = new char[3][3];
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

    private char[][] stackBlockOnTable(char[][] table, char[][] block) {
        int moveCount = 1;
        int centerStartIndex = ( table.length - 3 ) / 2;
        Point point = new Point(centerStartIndex, centerStartIndex);
        return getStackedTable(table, block, moveCount, point);
    }

    private char[][] getStackedTable(char[][] table, char[][] block, int moveCount, Point point) {
        char[][] newTable = deepCopy(table);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ( block[i][j] != '\u0000' ) {
                    // 위치에 놓을 수 있으면 입력, 아니면 이동
                    if ( newTable[point.getY() +i][point.getX() +j] != '\u0000' ) {
                        movePoint(point, moveCount);
                        return getStackedTable(table, block, moveCount+1, point);
                    } else {
                        newTable[point.getY() +i][point.getX() +j] = block[i][j];
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

    private void saveTable(char[][] stackedTable) {
        int size = getSizeOfCombinedBlocks(stackedTable);
        if ( !resultMap.containsKey(size) ) {
            resultMap.put(size, stackedTable);
        }
    }

    // 결합된 블록의 크기
    private int getSizeOfCombinedBlocks(char[][] table) {
        int firstX = table.length;
        int firstY = table.length;
        int lastX = -1;
        int lastY = -1;
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                if ( table[i][j] != '\u0000' ) {
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
        char[][] result = resultMap.get(key);
        for (char[] chars : result) {
            for (char c : chars) {
                System.out.print(c);
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
    static char[][] deepCopy(char[][] src){
        char[][] target = new char[src.length][src[0].length];
        for(int i=0; i<src.length; i++){
            for(int j=0; j<src[0].length; j++){
                target[i][j] = src[i][j];
            }
        }
        return target;
    }

    public static void main(String[] args) {
        Tetris tetris = new Tetris();
        List<char[][]> list = new ArrayList<>();
        char[][] arr1 = new char[3][3];
        arr1[1][0] = 'a';
        arr1[1][1] = 'a';
        arr1[1][2] = 'a';
        arr1[2][0] = 'a';
        arr1[2][1] = 'a';
        char[][] arr2 = new char[3][3];
        arr2[1][0] = 'b';
        arr2[1][1] = 'b';
        arr2[1][2] = 'b';
        arr2[2][0] = 'b';
        char[][] arr3 = new char[3][3];
        arr3[0][0] = 'c';
        arr3[0][1] = 'c';
        arr3[0][2] = 'c';
        arr3[1][0] = 'c';
        arr3[1][1] = 'c';
        arr3[1][2] = 'c';
        arr3[2][0] = 'c';
        arr3[2][1] = 'c';
        arr3[2][2] = 'c';

        list.add(arr1);
        list.add(arr2);
        list.add(arr3);

        for (char[][] arr : list) {
            for (char[] chars : arr) {
                for (char c : chars) {
                    System.out.print(c);
                    System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println();
        }
        tetris.start(list);
    }
}
