import javax.swing.*;
import java.awt.*;

public class JBrainTetris extends JTetris{

    private DefaultBrain defBrain;
    private Brain.Move currBestMove;
    private JCheckBox checkBrain;
    private JPanel little;
    private JSlider adversary;
    private JLabel oke;
    /**
     * Creates a new JTetris where each tetris square
     * is drawn with the given number of pixels.
     *
     * @param pixels
     */
    JBrainTetris(int pixels) {
        super(pixels);
        defBrain = new DefaultBrain();
    }

    @Override
    public void startGame() {
        currBestMove = new Brain.Move();
        super.startGame();
    }

    @Override
    public void addNewPiece() {
        super.addNewPiece();
        board.undo();
        defBrain.bestMove(board, currentPiece, board.getHeight() - TOP_SPACE, currBestMove);
    }

    @Override
    public Piece pickNextPiece() {
        int badness = random.nextInt(100);
        if(adversary.getValue() > badness){
            Piece bad = null;
            double score = Double.MIN_VALUE;
            for(Piece p : pieces){
                defBrain.bestMove(board, p, board.getHeight() - TOP_SPACE, currBestMove);
                if(score < currBestMove.score){
                    score = currBestMove.score;
                    bad = p;
                }
            }
            oke.setText("*ok*");
            return bad;
        }else{
            oke.setText("ok");
            return super.pickNextPiece();
        }
    }

    @Override
    public void tick(int verb) {
        super.tick(verb);
        if(currBestMove != null && checkBrain.isSelected() && verb == DOWN){
            makeBrainActions();
        }
    }

    private void makeBrainActions() {
        if(!currentPiece.equals(currBestMove.piece)) { super.tick(ROTATE); }

        if (currBestMove.x < currentX) {
            super.tick(LEFT);
        } else if (currBestMove.x > currentX) {
            super.tick(RIGHT);
        } /*else {
            super.tick(DROP);
        }*/
    }

    @Override
    public JComponent createControlPanel() {
        JComponent brainJcomp = super.createControlPanel();
        checkBrain = new JCheckBox("Brain");
        brainJcomp.add(checkBrain);

        little = new JPanel();
        GridLayout grL = new GridLayout(3, 1);
        grL.setVgap(5);
        little.setLayout(grL);
        JLabel advLabel = new JLabel("Adversary:");
        advLabel.setHorizontalAlignment(SwingConstants.CENTER);
        advLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        little.add(advLabel);

        adversary = new JSlider(0, 100, 0);
        adversary.setPreferredSize(new Dimension(100, 15));
        little.add(adversary);

        oke = new JLabel("ok");
        oke.setHorizontalAlignment(SwingConstants.CENTER);
        oke.setVerticalAlignment(SwingConstants.TOP);
        little.add(oke, BorderLayout.CENTER);
        brainJcomp.add(little);

        return brainJcomp;
    }

    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        JBrainTetris tetris = new JBrainTetris(20);
        JFrame frame = JBrainTetris.createFrame(tetris);
        frame.setVisible(true);
    }
}
