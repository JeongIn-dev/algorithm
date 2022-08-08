package solutions;

import java.util.*;

public class Tetris{

    private int numberOfBlocks;
    private int[] direction;
    private int minSize;
    private char[][] result;

    public void start(List<char[][]> arg) {
        numberOfBlocks = arg.size();
        minSize = numberOfBlocks * 9 * 9;
//        createDirectionArray();

        List<int[]> permutatedIndiesList = getPermutatedIndiesList(arg);

        for (int[] permutation : permutatedIndiesList) {
            saveMinSizeOfBlocksOnResultMap(arg, permutation);
        }

        showResult();
    }

    private List<int[]> getPermutatedIndiesList(List<char[][]> arg) {
        List<int[]> permutatedIndiesList = new ArrayList<>();
        int count = 0;
        int[] permutation = new int[arg.size()];
        boolean[] isSelected = new boolean[arg.size()];
        permutate(count, permutation, isSelected, permutatedIndiesList);
        return permutatedIndiesList;
    }

    private void permutate(int count, int[] permutation, boolean[] isSelected, List<int[]> permutatedIndiesList) {
        if ( count == permutation.length ) {
            permutatedIndiesList.add(permutation.clone());
            return;
        }

        for (int i = 0; i < numberOfBlocks; i++) {
            if ( isSelected[i] ) {
                continue;
            }
            permutation[count] = i;
            isSelected[i] = true;
            permutate(count+1, permutation, isSelected, permutatedIndiesList);
            isSelected[i] = false;
        }
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

    private void saveMinSizeOfBlocksOnResultMap(List<char[][]> blockList, int[] permutation) {
        List<char[][]> permutatedBlockList = getPermutatedBlockList(blockList, permutation);
        char[][] table = createTable();
        
        // 첫 번째 블록 입력 
        stackFirstBlockOnTable(permutatedBlockList.get(0), table);
        // 나머지 블록 입력 및 마지막 결과 저장
        stackRemainBlocksOnTableAndSaveResult(permutatedBlockList, table);
    }

    private List<char[][]> getPermutatedBlockList(List<char[][]> blockList, int[] indies) {
        List<char[][]> permutatedBlockList = new ArrayList<>();
        for ( int index : indies) {
            permutatedBlockList.add(blockList.get(index));
        }
        return permutatedBlockList;
    }

    private char[][] createTable() {
        double sqrt = Math.sqrt(numberOfBlocks);
        //Math.ceil() : 소수점 올림
        int num = (int) Math.ceil(sqrt);
        if ( num % 2 == 0 ) {
            num++;
        }
        int tableLength = (int) Math.pow(num,2);
//        int tableLength = 3 * ( numberOfBlocks * 2 - 1 );
        return new char[tableLength][tableLength];
    }

    private void stackFirstBlockOnTable(char[][] block, char[][] table) {
        int centerStartIndex = getCenterStartIndex(table);
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
                    list.addAll(stackBlocksOnTable(blockList, indexOfBlock, rotateNum, table));
                }
            } else {
                List<char[][]> copy = new ArrayList<>(list);
                list.clear();
                for (int rotateNum = 0; rotateNum < 4; rotateNum++) {
                    for (int j = 0; j < copy.size(); j++) {
                        list.addAll(stackBlocksOnTable(blockList, indexOfBlock, rotateNum, copy.get(j)));
                    }
                }
            }
        }
    }

    private List<char[][]> stackBlocksOnTable(List<char[][]> blockList, int indexOfBlock, int rotateNum, char[][] table) {
        char[][] block = blockList.get(indexOfBlock);
        List<char[][]> stackedTables = stackBlockOnTable(table, rotateBlock(block, rotateNum));

        // 결과 검사
        if ( indexOfBlock == blockList.size() - 1 ) {
            putResultIfSmallestEver(stackedTables);
        }

        return stackedTables;
    }

    // 블록 회전
    // rotateNum : 회전 수
    private char[][] rotateBlock(char[][] block, int rotateNum) {
        if ( rotateNum == 0 ) {
            return block;
        }
        for (int i = 0; i < rotateNum; i++) {
            block = rotate(block);
        }
        return block;
    }

    private char[][] rotate(char[][] block) {
        char[][] rotatedBlock = new char[3][3];
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

    private List<char[][]> stackBlockOnTable(char[][] table, char[][] block) {
        List<char[][]> stackedTableList = new ArrayList<>();
        char[][] newTable = deepCopy(table);
        boolean isStacked = true;

        for (int i = 0; i < newTable.length - 2; i++) {
            for (int j = 0; j < newTable[i].length - 2; j++) {
                Point point = new Point(j, i);

                for (int k = 0; k < block.length; k++) {
                    if ( !isStacked ) {
                        break;
                    }
                    for (int l = 0; l < block[k].length; l++) {
                        if ( block[k][l] != '\u0000' ) {
                            // 위치에 놓을 수 있으면 입력, 아니면 break
                            if ( newTable[point.getY() +k][point.getX() +l] != '\u0000' ) {
                                isStacked = false;
                                break;
                            } else {
                                newTable[point.getY() +k][point.getX() +l] = block[k][l];
                            }
                        }
                    }
                }

                if ( !isStacked ) {
                    isStacked = true;
                } else {
                    stackedTableList.add(newTable);
                }
                newTable = deepCopy(table);
            }
        }
        return stackedTableList;
    }

    private int getCenterStartIndex(char[][] table) {
        return ( table.length - 3 ) / 2;
//        return (numberOfBlocks - 1) * 3;
    }

    private void putResultIfSmallestEver(List<char[][]> stackedTables) {
        for (char[][] stackedTable : stackedTables) {
            int size = getSizeOfCombinedBlocks(stackedTable);
            if ( size < minSize ) {
                minSize = size;
                result = stackedTable;
            }
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

    private void showResult() {
        System.out.println("Size : " + minSize);
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
//        arr1[0][0] = 'a';
//        arr1[0][1] = 'a';
//        arr1[0][2] = 'a';
        arr1[1][0] = 'a';
        arr1[1][1] = 'a';
        arr1[1][2] = 'a';
        arr1[2][0] = 'a';
        arr1[2][1] = 'a';
//        arr1[2][2] = 'a';
        char[][] arr2 = new char[3][3];
        arr2[0][0] = 'b';
        arr2[0][1] = 'b';
        arr2[0][2] = 'b';
        arr2[1][0] = 'b';
//        arr2[1][1] = 'b';
//        arr2[1][2] = 'b';
//        arr2[2][0] = 'b';
//        arr2[2][1] = 'b';
//        arr2[2][2] = 'b';
        char[][] arr3 = new char[3][3];
        arr3[0][0] = 'c';
        arr3[0][1] = 'c';
        arr3[0][2] = 'c';
        arr3[1][0] = 'c';
        arr3[1][1] = 'c';
        arr3[1][2] = 'c';
//        arr3[2][0] = 'c';
//        arr3[2][1] = 'c';
//        arr3[2][2] = 'c';
        char[][] arr4 = new char[3][3];
        arr4[0][0] = 'd';
        arr4[1][0] = 'd';
        arr4[2][0] = 'd';
//        arr4[1][0] = 'd';
//        arr4[1][1] = 'd';
//        arr4[1][2] = 'd';
//        arr4[2][0] = 'd';
//        arr4[2][1] = 'd';
//        arr4[2][2] = 'd';

        list.add(arr1);
        list.add(arr2);
        list.add(arr3);
        list.add(arr4);

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
