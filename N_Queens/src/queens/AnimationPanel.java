package queens;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AnimationPanel extends JPanel {
    private List<int[][]> steps;
    private int currentStep = 0;
    private Timer timer;
    private JLabel stepLabel;
    private JSlider speedSlider;
    private QueenBoard board;
    private Runnable onComplete;
    
    public AnimationPanel(List<int[][]> steps, QueenBoard board, Runnable onComplete) {
        this.steps = steps;
        this.board = board;
        this.onComplete = onComplete;
        
        setLayout(new BorderLayout());
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        stepLabel = new JLabel("Шаг 1/" + steps.size());
        
        JButton playButton = new JButton("▶");
        JButton pauseButton = new JButton("⏸");
        JButton resetButton = new JButton("↺");
        speedSlider = new JSlider(1, 20, 10);
        
        playButton.addActionListener(e -> startAnimation());
        pauseButton.addActionListener(e -> stopAnimation());
        resetButton.addActionListener(e -> resetAnimation());
        
        speedSlider.setMajorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setPreferredSize(new Dimension(150, 50));
        
        controlPanel.add(stepLabel);
        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resetButton);
        controlPanel.add(new JLabel("Скорость:"));
        controlPanel.add(speedSlider);
        
        add(controlPanel, BorderLayout.SOUTH);
        
        timer = new Timer(500, e -> nextStep());
    }
    
    private void startAnimation() {
        int speed = speedSlider.getValue();
        timer.setDelay(1000 / speed);
        timer.start();
    }
    
    private void stopAnimation() {
        timer.stop();
    }
    
    private void resetAnimation() {
        stopAnimation();
        currentStep = 0;
        stepLabel.setText("Шаг 1/" + steps.size());
        board.updateBoard(steps.get(currentStep));
    }
    
    private void nextStep() {
        currentStep++;
        if (currentStep >= steps.size()) {
            stopAnimation();
            if (onComplete != null) {
                onComplete.run();
            }
        } else {
            stepLabel.setText("Шаг " + (currentStep + 1) + "/" + steps.size());
            board.updateBoard(steps.get(currentStep));
        }
    }
}