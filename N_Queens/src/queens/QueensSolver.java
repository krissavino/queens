package queens;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class QueensSolver extends JFrame {
    private QueenBoard board;
    private JList<String> solutionsList;
    private DefaultListModel<String> listModel;
    private List<int[][]> allSolutions;
    private List<Integer> solutionCosts;                // стоимости решений
    private JPanel controlPanel;
    private JPanel settingsPanel;
    private AnimationPanel animationPanel;
    private JButton showAnimationButton;
    private JButton showSolutionButton;
    private boolean showingAnimation = false;
    private int boardSize = 8;                          // размер доски по умолчанию
    private JSpinner sizeSpinner;
    private JButton generateButton;
    private JCheckBox sortAscendingCheck;
    private Map<Integer, QueenState> fixedStates = new HashMap<>();
    private Map<Integer, Integer> priorities = new HashMap<>();
    private JPanel fixedPanel;
    private List<JSpinner> rowSpinners = new ArrayList<>();
    private List<JSpinner> prioritySpinners = new ArrayList<>();

    public QueensSolver() {
        setTitle("Задача о N ферзях с агентами");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Создаем панель настроек
        createSettingsPanel();

        // Инициализация доски с размером по умолчанию
        board = new QueenBoard(new int[boardSize][2], boardSize);
        add(board, BorderLayout.CENTER);

        createControlPanel();
        add(controlPanel, BorderLayout.EAST);

        // Заполняем БД для начального размера
        DatabaseManager.fillRandomStates(boardSize);
        refreshSolutions();

        pack();
        setLocationRelativeTo(null);
    }

    private void createSettingsPanel() {
        settingsPanel = new JPanel(new BorderLayout());
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topRow.add(new JLabel("Размер доски (N):"));
        
        sizeSpinner = new JSpinner(new SpinnerNumberModel(8, 4, 12, 1));
        sizeSpinner.addChangeListener(e -> {
            boardSize = (int) sizeSpinner.getValue();
            // Обновить панель фиксации позиций
            updateFixedPanel();
            DatabaseManager.fillRandomStates(boardSize);
            // Пересоздать доску
            board = new QueenBoard(new int[boardSize][2], boardSize);
            refreshSolutions();
            // Перестроить GUI
            getContentPane().removeAll();
            getContentPane().add(settingsPanel, BorderLayout.NORTH);
            getContentPane().add(board, BorderLayout.CENTER);
            getContentPane().add(controlPanel, BorderLayout.EAST);
            pack();
        });
        topRow.add(sizeSpinner);
        
        generateButton = new JButton("Сгенерировать решения");
        generateButton.addActionListener(e -> refreshSolutions());
        topRow.add(generateButton);
        
        settingsPanel.add(topRow, BorderLayout.NORTH);

        // Панель для фиксации позиций и приоритетов
        fixedPanel = new JPanel(new GridLayout(0, 3));
        fixedPanel.setBorder(BorderFactory.createTitledBorder("Фиксация позиций (колонка: ряд, приоритет)"));
        updateFixedPanel();
        
        JScrollPane fixedScrollPane = new JScrollPane(fixedPanel);
        fixedScrollPane.setPreferredSize(new Dimension(300, 200));
        settingsPanel.add(fixedScrollPane, BorderLayout.CENTER);

        add(settingsPanel, BorderLayout.NORTH);
    }

    private void updateFixedPanel() {
        fixedPanel.removeAll();
        rowSpinners.clear();
        prioritySpinners.clear();

        fixedPanel.add(new JLabel("Колонка"));
        fixedPanel.add(new JLabel("Ряд (0-свободно)"));
        fixedPanel.add(new JLabel("Приоритет (меньше-выше)"));

        for (int col = 1; col <= boardSize; col++) {
            JLabel colLabel = new JLabel(String.valueOf(col));
            JSpinner rowSpinner = new JSpinner(new SpinnerNumberModel(0, 0, boardSize, 1));
            JSpinner prioritySpinner = new JSpinner(new SpinnerNumberModel(col, 1, boardSize, 1));

            // Сохраняем ссылки
            rowSpinners.add(rowSpinner);
            prioritySpinners.add(prioritySpinner);

            // Обработчики изменений
            rowSpinner.addChangeListener(e -> updateFixedStates());
            prioritySpinner.addChangeListener(e -> updatePriorities());

            fixedPanel.add(colLabel);
            fixedPanel.add(rowSpinner);
            fixedPanel.add(prioritySpinner);
        }
        fixedPanel.revalidate();
        fixedPanel.repaint();
    }

    private void updateFixedStates() {
        fixedStates.clear();
        for (int i = 0; i < boardSize; i++) {
            int col = i + 1;
            int row = (int) rowSpinners.get(i).getValue();
            if (row > 0) {
                QueenState state = DatabaseManager.getState(row, col, boardSize);
                if (state == null) {
                    state = new QueenState(row, "BLUE", 0);
                }
                fixedStates.put(col, state);
            }
        }
    }

    private void updatePriorities() {
        priorities.clear();
        for (int i = 0; i < boardSize; i++) {
            int col = i + 1;
            int priority = (int) prioritySpinners.get(i).getValue();
            priorities.put(col, priority);
        }
    }

    private void refreshSolutions() {
        // Обновляем фиксированные состояния и приоритеты из спиннеров
        updateFixedStates();
        updatePriorities();

        // Поиск решений с учётом параметров
        allSolutions = Queen.findAllSolutions(boardSize, fixedStates, priorities);
        solutionCosts = new ArrayList<>();

        listModel.clear();
        for (int i = 0; i < allSolutions.size(); i++) {
            int cost = DatabaseManager.calculateTotalCost(allSolutions.get(i), boardSize);
            solutionCosts.add(cost);
            listModel.addElement(String.format("Решение %d (стоимость %d)", i + 1, cost));
        }

        // Применяем сортировку, если включена
        if (sortAscendingCheck.isSelected()) {
            sortSolutions(true);
        }

        if (!allSolutions.isEmpty()) {
            solutionsList.setSelectedIndex(0);
            board.updateBoard(allSolutions.get(0));
        } else {
            board.updateBoard(new int[boardSize][2]); // пустая доска
        }

        // Обновляем надпись с количеством решений
        JLabel countLabel = (JLabel) ((JPanel) controlPanel.getComponent(2)).getComponent(0);
        countLabel.setText("Всего решений: " + allSolutions.size());
    }

    private void sortSolutions(boolean ascending) {
        if (allSolutions.isEmpty()) return;

        // Создаём список индексов и сортируем по стоимости
        Integer[] indices = new Integer[allSolutions.size()];
        for (int i = 0; i < indices.length; i++) indices[i] = i;
        Arrays.sort(indices, (a, b) -> ascending
                ? Integer.compare(solutionCosts.get(a), solutionCosts.get(b))
                : Integer.compare(solutionCosts.get(b), solutionCosts.get(a)));

        List<int[][]> sortedSolutions = new ArrayList<>();
        List<Integer> sortedCosts = new ArrayList<>();
        for (int idx : indices) {
            sortedSolutions.add(allSolutions.get(idx));
            sortedCosts.add(solutionCosts.get(idx));
        }
        allSolutions = sortedSolutions;
        solutionCosts = sortedCosts;

        listModel.clear();
        for (int i = 0; i < allSolutions.size(); i++) {
            listModel.addElement(String.format("Решение %d (стоимость %d)", i + 1, solutionCosts.get(i)));
        }
    }

    private void createControlPanel() {
        controlPanel = new JPanel(new BorderLayout());
        controlPanel.setPreferredSize(new Dimension(300, 500));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Решения"));

        JPanel topPanel = new JPanel(new GridLayout(4, 1));
        showAnimationButton = new JButton("Показать анимацию");
        showSolutionButton = new JButton("Показать решение");
        showSolutionButton.setEnabled(false);
        
        sortAscendingCheck = new JCheckBox("Сортировать по возрастанию стоимости");
        sortAscendingCheck.addActionListener(e -> {
            sortSolutions(sortAscendingCheck.isSelected());
            solutionsList.setSelectedIndex(0);
            if (!allSolutions.isEmpty()) {
                board.updateBoard(allSolutions.get(0));
            }
        });

        showAnimationButton.addActionListener(e -> showAnimation());
        showSolutionButton.addActionListener(e -> showSolution());

        topPanel.add(showAnimationButton);
        topPanel.add(showSolutionButton);
        topPanel.add(sortAscendingCheck);
        controlPanel.add(topPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
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

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel countLabel = new JLabel("Всего решений: 0");
        countLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        southPanel.add(countLabel);
        controlPanel.add(southPanel, BorderLayout.SOUTH);
    }

    // Генерация шагов анимации для выбранного решения
    private List<int[][]> generateSteps(int solutionIndex) {
        List<int[][]> steps = new ArrayList<>();
        int[][] finalSolution = allSolutions.get(solutionIndex);

        // Пустая доска
        int[][] currentBoard = new int[boardSize][2];
        for (int i = 0; i < boardSize; i++) {
            currentBoard[i][0] = 0;
            currentBoard[i][1] = i + 1;
        }
        steps.add(copyBoard(currentBoard));

        // Расставляем ферзей по колонкам в порядке возрастания колонки (для анимации)
        for (int col = 0; col < boardSize; col++) {
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
        int[][] copy = new int[boardSize][2];
        for (int i = 0; i < boardSize; i++) {
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
        QueenBoard animationBoard = new QueenBoard(steps.get(0), boardSize);
        animationBoard.setPreferredSize(new Dimension(boardSize * 60, boardSize * 60));

        // Создаем панель с анимацией
        animationPanel = new AnimationPanel(steps, animationBoard, () -> {
            // По окончании анимации показываем финальное решение
            SwingUtilities.invokeLater(() -> showSolution());
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
        getContentPane().add(settingsPanel, BorderLayout.NORTH);
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
        // Установка Look and Feel для лучшего отображения
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new QueensSolver().setVisible(true);
        });
    }
}