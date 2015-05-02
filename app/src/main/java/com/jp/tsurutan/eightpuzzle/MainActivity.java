package com.jp.tsurutan.eightpuzzle;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.math.BigDecimal;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {
    private static int TRUE = 1;
    private static int FALSE = 2;
    private static int GOAL = 2;
    private static int SIZE = 9;
    private static int MAX_STATE = 181440;
    private float startTime;
    private float stopTime;
    final Handler handler = new Handler();
    private ProgressDialog progressDialog;

    @InjectView(R.id.one_puzzle)
    TextView onePuzzle;
    @InjectView(R.id.two_puzzle)
    TextView twoPuzzle;
    @InjectView(R.id.three_puzzle)
    TextView threePuzzle;
    @InjectView(R.id.four_puzzle)
    TextView fourPuzzle;
    @InjectView(R.id.five_puzzle)
    TextView fivePuzzle;
    @InjectView(R.id.six_puzzle)
    TextView sixPuzzle;
    @InjectView(R.id.seven_puzzle)
    TextView sevenPuzzle;
    @InjectView(R.id.eight_puzzle)
    TextView eightPuzzle;
    @InjectView(R.id.zero_puzzle)
    TextView zeroPuzzle;
    @InjectView(R.id.time_text)
    TextView timeText;
    @InjectView(R.id.step)
    TextView step;

    @OnClick(R.id.eight_puzzle)
    void clickOne() {
        int keep = initState[7];
        initState[7] = initState[8];
        initState[8] = keep;
        showInit();
    }

    @OnClick(R.id.start_button)
    void start() {
        progressDialog.setMessage("思考中・・・・");
        progressDialog.show();
        startTime = System.nanoTime();
        search();
    }

    private int[][] adjacent = new int[][]{
            {1, 3, -1, -1, -1},
            {0, 4, 2, -1, -1},
            {1, 5, -1, -1, -1},
            {0, 4, 6, -1, -1},
            {1, 3, 5, 7, -1},
            {2, 4, 8, -1, -1},
            {3, 7, -1, -1, -1},
            {4, 6, 8, -1, -1},
            {5, 7, -1, -1, -1},
    };

    private int state[][] = new int[MAX_STATE + 1][SIZE];
    private int[] checkTable = new int[MAX_STATE * 2];
    private int[] spacePosition = new int[MAX_STATE];
    private int[] prevState = new int[MAX_STATE];
    private int keepNumber = 0;

    private int[] initState = new int[]{
            8, 6, 7, 2, 5, 4, 3, 0, 1
    };

    private int[] finalState = new int[]{
            1, 2, 3, 4, 5, 6, 7, 8, 0
    };

    private int[][] keepState = new int[MAX_STATE][SIZE];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        showInit();
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    int changeNumber(int[] board) {
        int[] work = new int[SIZE];
        int[] factTable = new int[]{
                40320, 5040, 720, 120, 24, 6, 2, 1, 0
        };
        int value = 0;
        for (int i = 0; i < SIZE; i++) {
            work[i] = board[i];
        }
        for (int i = 0; i < SIZE; i++) {
            value += factTable[i] * work[i];
            for (int j = i + 1; j < SIZE; j++) {
                if (work[i] < work[j]) {
                    work[j]--;
                }
            }
        }
        return value;
    }

    void search() {
        int front = 0, rear = 1, space = 0;
        for (int i = 0; i < SIZE; i++) {
            state[0][i] = initState[i];
        }
        for (int i = 0; i < SIZE; i++) {
            if (initState[i] == 0) {
                break;
            };
            space++;
        }
        spacePosition[0] = space;
        prevState[0] = -1;
        checkTable[changeNumber(initState)] = TRUE;
        checkTable[changeNumber(finalState)] = GOAL;
        int n, k;
        while (front < rear) {
            int s = spacePosition[front];
            for (int i = 0; (n = adjacent[s][i]) != -1; i++) {
                for (int j = 0; j < SIZE; j++) {
                    state[rear][j] = state[front][j];
                }
                state[rear][s] = state[rear][n];
                state[rear][n] = 0;
                spacePosition[rear] = n;
                prevState[rear] = front;
                k = changeNumber(state[rear]);
                if (checkTable[k] == GOAL) {
                    stopTime = System.nanoTime();
                    String time = String.valueOf(stopTime - startTime);
                    String[] keepTime = time.split("\\.");
                    timeText.setText(keepTime[0] + "." + keepTime[1].substring(0, 3) + "n秒");
                    step.setText(String.valueOf(rear) + "通り");
                    progressDialog.dismiss();
                    showAnswer(rear);
                    show(keepNumber - 1);
                    return;
                } else if (checkTable[k] != TRUE) {
                    checkTable[k] = TRUE;
                    rear++;
                }
            }
            front++;
        }
    }

    void showAnswer(final int n) {

        keepState[keepNumber] = state[n];
        keepNumber++;
        if (n != 0) {
            showAnswer(prevState[n]);
        }
    }

    void show(final int n) {
        if (n < 0) {
            return;
        }
        final TextView[] puzzles = new TextView[]{
                onePuzzle,
                twoPuzzle,
                threePuzzle,
                fourPuzzle,
                fivePuzzle,
                sixPuzzle,
                sevenPuzzle,
                eightPuzzle,
                zeroPuzzle
        };
        if (n != 0) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    show(n - 1);
                }
            }, 400);
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int finalN = n;
                final int finalI = i;
                final int finalJ = j;
                if (keepState[finalN][finalI * 3 + finalJ] == 0) {
                    puzzles[finalI * 3 + finalJ].setText(String.valueOf(keepState[finalN][finalI * 3 + finalJ]));
                    puzzles[finalI * 3 + finalJ].setBackgroundColor(getResources().getColor(R.color.white));
                    puzzles[finalI * 3 + finalJ].setTextColor(getResources().getColor(R.color.black));
                } else {
                    puzzles[finalI * 3 + finalJ].setText(String.valueOf(keepState[finalN][finalI * 3 + finalJ]));
                    puzzles[finalI * 3 + finalJ].setBackgroundColor(getResources().getColor(R.color.material_blue_grey_800));
                    puzzles[finalI * 3 + finalJ].setTextColor(getResources().getColor(R.color.white));
                }
            }
        }
    }

    void showInit() {
        final TextView[] puzzles = new TextView[]{
                onePuzzle,
                twoPuzzle,
                threePuzzle,
                fourPuzzle,
                fivePuzzle,
                sixPuzzle,
                sevenPuzzle,
                eightPuzzle,
                zeroPuzzle
        };
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int finalI = i;
                final int finalJ = j;
                if (initState[finalI * 3 + finalJ] == 0) {
                    puzzles[finalI * 3 + finalJ].setText(String.valueOf(initState[finalI * 3 + finalJ]));
                    puzzles[finalI * 3 + finalJ].setBackgroundColor(getResources().getColor(R.color.white));
                    puzzles[finalI * 3 + finalJ].setTextColor(getResources().getColor(R.color.black));
                } else {
                    puzzles[finalI * 3 + finalJ].setText(String.valueOf(initState[finalI * 3 + finalJ]));
                    puzzles[finalI * 3 + finalJ].setBackgroundColor(getResources().getColor(R.color.material_blue_grey_800));
                    puzzles[finalI * 3 + finalJ].setTextColor(getResources().getColor(R.color.white));
                }
            }
        }
    }
}
