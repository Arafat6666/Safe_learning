package com.safelearning.view;

import com.safelearning.controller.IssueController;
import com.safelearning.model.IssueReport;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.table.DefaultTableCellRenderer;

public class TrackingView extends JFrame {

    private static final String WINDOW_TITLE = "Issue Tracking";
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 500;
    private static final int TABLE_ROW_HEIGHT = 30;

    private static final String[] COLUMN_NAMES = {
            "ID",
            "Location",
            "Hazard",
            "Priority",
            "Status",
            "Reporter"
    };

    private static final String NO_SELECTION_MESSAGE =
            "Please select a report first.";

    private static final String NO_SELECTION_TITLE =
            "No Selection";

    private final IssueController controller;

    private JTable reportTable;
    private DefaultTableModel tableModel;

    private JButton viewDetailsButton;
    private JButton backButton;

    public TrackingView(IssueController controller) {

        if (controller == null) {
            throw new IllegalArgumentException(
                    "Controller cannot be null"
            );
        }

        this.controller = controller;

        setTitle(WINDOW_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10,10));

        initializeComponents();

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadReports();

        registerEventHandlers();

        setVisible(true);

    }

    private void initializeComponents() {

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportTable = new JTable(tableModel);

        viewDetailsButton = new JButton("View Details");

        backButton = new JButton("Back");
    }

    private JPanel createTitlePanel() {

        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Issue Tracking", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitle = new JLabel(
                "View all reported safety hazards",
                SwingConstants.CENTER
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));

        panel.add(title, BorderLayout.CENTER);
        panel.add(subtitle, BorderLayout.SOUTH);

        return panel;
    }

    private JScrollPane createTablePanel() {

        reportTable.setRowHeight(TABLE_ROW_HEIGHT);

        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        reportTable.getTableHeader().setReorderingAllowed(false);

        reportTable.setFillsViewportHeight(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < reportTable.getColumnCount(); i++) {
            reportTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return new JScrollPane(reportTable);

    }

    private JPanel createButtonPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,15,15));

        panel.add(viewDetailsButton);
        panel.add(backButton);

        return panel;

    }

    public void loadReports() {

        clearReportTable();

        for (IssueReport report : controller.getAllReports()) {
            tableModel.addRow(toTableRow(report));
        }
    }

    private void clearReportTable() {
        tableModel.setRowCount(0);
    }

    private Object[] toTableRow(IssueReport report) {

        return new Object[]{
                report.getId(),
                report.getLocation(),
                report.getHazardType(),
                report.getPriority(),
                report.getStatus(),
                report.getReportedBy().getName()
        };
    }

    private void registerEventHandlers() {

        backButton.addActionListener(e -> dispose());
        viewDetailsButton.addActionListener(e -> openSelectedReport());

    }

    private void openSelectedReport() {

        int selectedRow = reportTable.getSelectedRow();

        if (!isReportSelected(selectedRow)) {
            showNoSelectionWarning();
            return;
        }

        openReportDetails(selectedRow);
    }

    private boolean isReportSelected(int selectedRow) {
        return selectedRow >= 0;
    }

    private void showNoSelectionWarning() {

        JOptionPane.showMessageDialog(
                this,
                NO_SELECTION_MESSAGE,
                NO_SELECTION_TITLE,
                JOptionPane.WARNING_MESSAGE
        );
    }


    private void openReportDetails(int selectedRow) {

        IssueReport selectedReport =
                controller.getReport(selectedRow);

        new EscalationView(
                controller,
                selectedReport,
                this
        );
    }

    JButton getBackButton() {
        return backButton;
    }

}

