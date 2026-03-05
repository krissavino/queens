package queens;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QueensSolver extends JFrame {
    private QueenBoard board;
    private JList<String> solutionsList;
    private DefaultListModel<String> listModel;
    private List<int[][]> allSolutions;
    private JPanel controlPanel;
    
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
        controlPanel.setPreferredSize(new Dimension(200, 500));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Решения"));
        
        listModel = new DefaultListModel<>();
        for (int i = 0; i < allSolutions.size(); i++) {
            listModel.addElement("Решение " + (i + 1));
        }
        
        solutionsList = new JList<>(listModel);
        solutionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        solutionsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = solutionsList.getSelectedIndex();
                if (index >= 0) {
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new QueensSolver().setVisible(true);
        });
    }
}