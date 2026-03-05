package queens;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QueensSolver extends JFrame {
    private QueenBoard board;
    private JList<String> solutionsList;
    private DefaultListModel<String> listModel;
    private List<int[][]> allSolutions;
    private JPanel controlPanel;
    private AnimationPanel animationPanel;
    private JButton showAnimationButton;
    private JButton showSolutionButton;
    private boolean showingAnimation = false;
    
    public QueensSolver() {
        setTitle("Задача о 8 ферзях - все решения");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        allSolutions = Queen.findAllSolutions();
        
        board = new QueenBoard(allSolutions.get(0));
        add(board, BorderLayout.CENTER);
        
        createControlPanel();
        add(controlPanel, BorderLayout.EAST);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void createControlPanel() {
        controlPanel = new JPanel(new BorderLayout());
        controlPanel.setPreferredSize(new Dimension(250, 500));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Решения"));
        
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        showAnimationButton = new JButton("Показать анимацию");
        showSolutionButton = new JButton("Показать решение");
        showSolutionButton.setEnabled(false);
        
        showAnimationButton.addActionListener(e -> showAnimation());
        showSolutionButton.addActionListener(e -> showSolution());
        
        topPanel.add(showAnimationButton);
        topPanel.add(showSolutionButton);
        controlPanel.add(topPanel, BorderLayout.NORTH);
        
        listModel = new DefaultListModel<>();
        for (int i = 0; i < allSolutions.size(); i++) {
            listModel.addElement("Решение " + (i + 1));
        }
        
        solutionsList = new JList<>(listModel);
        solutionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        solutionsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = solutionsList.getSelectedIndex();
                if (index >= 0 && !showingAnimation) {
                    board.updateBoard(allSolutions.get(index));
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(solutionsList);
        controlPanel.add(scrollPane, BorderLayout.CENTER);
        
        JLabel countLabel = new JLabel("Всего решений: " + allSolutions.size());
        countLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        controlPanel.add(countLabel, BorderLayout.SOUTH);
    }
    
    private List<int[][]> generateSteps(int solutionIndex) {
        List<int[][]> steps = new ArrayList<>();
        int[][] finalSolution = allSolutions.get(solutionIndex);
        
        // Начинаем с пустой доски
        int[][] currentBoard = new int[8][2];
        for (int i = 0; i < 8; i++) {
            currentBoard[i][0] = 0;
            currentBoard[i][1] = i + 1;
        }
        steps.add(copyBoard(currentBoard));
        
        // Расставляем ферзей по колонкам
        for (int col = 0; col < 8; col++) {
            int targetRow = finalSolution[col][0];
            
            // Плавно перемещаем ферзя вниз до целевой позиции
            for (int row = 1; row <= targetRow; row++) {
                currentBoard[col][0] = row;
                steps.add(copyBoard(currentBoard));
                
                // Добавляем промежуточные шаги для плавности
                if (row < targetRow) {
                    for (int j = 0; j < 2; j++) {
                        steps.add(copyBoard(currentBoard));
                    }
                }
            }
            
            // Пауза после установки ферзя
            for (int j = 0; j < 3; j++) {
                steps.add(copyBoard(currentBoard));
            }
        }
        
        return steps;
    }
    
    private int[][] copyBoard(int[][] original) {
        int[][] copy = new int[8][2];
        for (int i = 0; i < 8; i++) {
            copy[i][0] = original[i][0];
            copy[i][1] = original[i][1];
        }
        return copy;
    }
    
    private void showAnimation() {
        int selectedIndex = solutionsList.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Выберите решение из списка");
            return;
        }
        
        showingAnimation = true;
        showAnimationButton.setEnabled(false);
        showSolutionButton.setEnabled(true);
        
        List<int[][]> steps = generateSteps(selectedIndex);
        
        // Создаем новую доску для анимации
        QueenBoard animationBoard = new QueenBoard(steps.get(0));
        animationBoard.setPreferredSize(new Dimension(480, 480));
        
        // Создаем панель с анимацией
        animationPanel = new AnimationPanel(steps, animationBoard, () -> {
            // По окончании анимации показываем финальное решение
            SwingUtilities.invokeLater(() -> {
                showSolution();
            });
        });
        
        // Создаем контейнер для доски и анимации
        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.add(animationBoard, BorderLayout.CENTER);
        boardContainer.add(animationPanel, BorderLayout.SOUTH);
        
        // Заменяем содержимое окна
        getContentPane().removeAll();
        getContentPane().add(boardContainer, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.EAST);
        getContentPane().revalidate();
        pack();
    }
    
    private void showSolution() {
        showingAnimation = false;
        showAnimationButton.setEnabled(true);
        showSolutionButton.setEnabled(false);
        
        // Возвращаем обычную доску
        getContentPane().removeAll();
        getContentPane().add(board, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.EAST);
        getContentPane().revalidate();
        pack();
        
        // Показываем выбранное решение
        int selectedIndex = solutionsList.getSelectedIndex();
        if (selectedIndex >= 0) {
            board.updateBoard(allSolutions.get(selectedIndex));
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new QueensSolver().setVisible(true);
        });
    }
}