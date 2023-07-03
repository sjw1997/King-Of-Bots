<template>
    <ContentField>
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">游戏规则</h5>
                <p class="card-text">确切地说，这并不是贪吃蛇。 与传统单人贪吃蛇不同的是，本贪吃蛇为双人对战，每回合玩家同时做出决策控制自己的蛇。</p>
                <p class="card-text">玩家在13*14的网格中操纵一条蛇(蛇是一系列坐标构成的有限不重复有顺序的序列，序列中相邻坐标均相邻，即两坐标的x轴坐标或y轴坐标相同，坐标从0开始，x轴代表行，y轴代表列，序列中最后一个坐标代表蛇头)，玩家只能控制蛇头的朝向(上、下、左、右)来控制蛇。蛇以恒定的速度前进(前进即为序列末尾插入蛇头指向方向下一格坐标，并删除序列头坐标)。蛇的初始位置在网格中的左下角(地图位置[11,1])与右上角(地图位置[1,12])，初始长度为1格。与传统贪吃蛇不同，本游戏在网格中并没有豆子，但蛇会自动长大(长大即为不删除序列头坐标的前进)，前10回合每回合长度增加1，从第11回合开始，每3回合长度增加1。</p>
                <p class="card-text">地图为13*14的网格，由1*1的草地与障碍物构成。</p>
                <p class="card-text">蛇头在网格外、障碍物、自己蛇的身体(即序列重复)、对方蛇的身体(即与对方序列有相同坐标)，或非法操作均判定为死亡。任何一条蛇死亡时，游戏结束。若蛇同时死亡，判定为平局，否则先死的一方输，另一方赢。</p>
            </div>
        </div>

        <div class="card" style="margin-top: 2vh;">
            <div class="card-body">
                <h5 class="card-title">交互方式</h5>
                <p class="card-text">输入给定一个字符串，表示当前局势，输出在此局势下您所操纵蛇的最佳运动方向。每回合，我们将运行一遍您的代码，并根据代码输出的运动方向作为本回合您的蛇的运动方向。</p>
                <p class="card-text">输入格式: &lt;初始地图信息&gt;#&lt;自己的蛇的初始x轴坐标&gt;#&lt;自己的蛇的初始y轴坐标&gt;#(&lt;自己的蛇自开始以来每回合的运动方向&gt;)#&lt;对手的蛇的初始x轴坐标&gt;#&lt;对手的蛇的初始y轴坐标&gt;#(&lt;对手的蛇自开始以来每回合的运动方向&gt;)</p>
                <p class="card-text">输出格式: 一个整数，代表运动方向。0代表向上，1代表向右，2代表向下，3代表向左。</p>
                <p class="card-text">输入样例：
11111111111111100011010000011001000000000110001100001001100000001000011001000000000110000100100001100000000010011000010000000110010000110001100000000010011000001011000111111111111111#11#1#(011101210010)#1#12#(233322233033)
</p>
                <p class="card-text">输出样例: <br>3</p>
                <p class="card-text">
                    样例解释：<br>
                    对于输入，分别拆解。<br>
                    地图信息：11111111111111100011010000011001000000000110001100001001100000001000011001000000000110000100100001100000000010011000010000000110010000110001100000000010011000001011000111111111111111<br>
                    二维地图信息被按行压缩成一维，地图位置[i,j](0&le;i&le;12, 0&le;j&le;13)信息在第i*14+j位，1表示该位置为障碍物，0表示为草地。<br>
                    自己的蛇的初始x轴坐标：11。<br>
                    自己的蛇的初始y轴坐标：1。<br>
                    自己的蛇自开始以来每回合的运动方向：011101210010。<br>
                    对手的蛇的初始x轴坐标：1。<br>
                    对手的蛇的初始y轴坐标：12。<br>
                    对手的蛇自开始以来每回合的运动方向：233322233033。<br>
                    对于这样的局势，输出为3，即向左运动。
                </p>
            </div>
        </div>

        <div class="card" style="margin-top: 2vh;">
            <div class="card-body">
                <h5 class="card-title" style="margin-bottom: 1vh;">样例程序（Java版本）</h5>
                <VAceEditor
                    v-model:value="content"
                    lang="c_cpp"
                    theme="textmate"
                    min-lines="2"
                    max-lines="50"
                    readonly="true"
                    :options="{
                    enableBasicAutocompletion: true, //启用基本自动完成
                    enableSnippets: true, // 启用代码段
                    enableLiveAutocompletion: true, // 启用实时自动完成
                    fontSize: 16, //设置字号
                    tabSize: 4, // 标签大小
                    showPrintMargin: false, //去除编辑器里的竖线
                    highlightActiveLine: true,
                    }"
                />
            </div>
        </div>

        <!-- <div class="card" style="margin-top: 2vh;">
            <div card-body>
                <h5 class="card-title">样例程序（Java版本）</h5>
                <pre class="card-text">
                    {{ content }}
                </pre>
            </div>
        </div> -->
    </ContentField>

    
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import { VAceEditor } from 'vue3-ace-editor';

export default {
    components: {
        ContentField,
        VAceEditor,
    },
    setup() {
        const content = `\
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static class Cell {
        int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // 检验当前回合蛇的长度是否会增加
    private static boolean checkTailIncreasing(int steps) {
        if (steps < 10) {
            return true;
        }
        return steps % 3 == 1;
    }

    private static List<Cell> getCells(int sx, int sy, String steps) {
        steps = steps.substring(1, steps.length() - 1);
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0 ,1, 0}, dy = {0, 1, 0, -1};

        int x = sx, y = sy;
        res.add(new Cell(x, y));
        int step = 0;
        for (int i = 0; i < steps.length(); i ++ ) {
            int d = steps.charAt(i) - '0';
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!checkTailIncreasing(++ step)) {
                res.remove(0);  // 删除蛇尾
            }
        }

        return res;
    }

    private static int nextMove(String input) {
        String[] strs = input.split("#");

        int rows = 13, cols = 14;
        int[][] g = new int[rows][cols];
        for (int i = 0, k = 0; i < rows; i ++ ) {
            for (int j = 0; j < cols; j ++ , k ++ ) {
                if (strs[0].charAt(k) == '1') {
                    g[i][j] = 1;
                }
            }
        }

        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);
        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);

        List<Cell> aCells = getCells(aSx, aSy, strs[3]);
        List<Cell> bCells = getCells(bSx, bSy, strs[6]);

        for (Cell c : aCells) g[c.x][c.y] = 1;
        for (Cell c : bCells) g[c.x][c.y] = 1;

        int[] dx = {-1, 0 ,1, 0}, dy = {0, 1, 0, -1};
        List<int> validDirections = new ArrayList<>();
        for (int i = 0; i < 4; i ++ ) {
            int x = aCells.get(aCells.size() - 1).x + dx[i];
            int y = aCells.get(aCells.size() - 1).y + dy[i];
            if (x >= 0 && x < rows && y >= 0 && y < cols && 0 == g[x][y]) {
                validDirections.add(i);
            }
        }

        if (validDirections.isEmpty()) {
            return 0;
        }

        Random random = new Random();
        return validDirections.get(random.nextInt(validDirections.size()));
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        System.out.println(nextMove(input));
    }
}
`

        return {
            content,
        }
    }
}
</script>

<style scoped>
</style>