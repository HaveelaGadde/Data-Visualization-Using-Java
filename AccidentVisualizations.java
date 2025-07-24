import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.CategoryLabelWidthType;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import static org.jfree.chart.ChartFactory.createBarChart;


public class AccidentVisualizations {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Home().setVisible(true));
    }
}

class Home extends JFrame {
    private static final String BACKGROUND_IMAGE_PATH = "C:/Users/Leena/Downloads/img.jpg"; // Update with your image path
    private Image backgroundImage;

    public Home () {
        // Load the background image
        try {
            backgroundImage = ImageIO.read(new File(BACKGROUND_IMAGE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Home");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create a panel with a background image
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw the background image
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }

                // Add semi-transparent overlay
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.SrcOver.derive(0.9f)); // 70% opacity
                g2d.setColor(new Color(0, 0, 0, 150)); // Semi-transparent black
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        imagePanel.setLayout(new GridBagLayout());

        // Title
        JLabel titleLabel = new JLabel("Transportation: Road Accidents Statewise/UT Data (2012)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 35));
        titleLabel.setForeground(Color.WHITE);

        // Objective
        JLabel objectiveLabel = new JLabel("<html><body style='width: 600px'>"
                + "Objective: The objective is to analyze road accident causes across various States/UTs in India "
                + "based on factors such as driver fault, cyclist fault, pedestrian fault, vehicle condition defects, "
                + "road condition defects, weather, and other causes. This data will provide insights into the "
                + "predominant causes of accidents in each state and help in formulating targeted road safety interventions."
                + "</body></html>");
        objectiveLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        objectiveLabel.setForeground(Color.WHITE);

        // Key Insights
        JLabel insightsLabel = new JLabel("<html><body style='width: 600px'>"
                + "Key Insights:<br>"
                + "• Human error, especially driver fault, is the primary cause of accidents.<br>"
                + "• Cyclist, pedestrian, and weather-related incidents contribute minimally.<br>"
                + "• Defects in vehicles and poor road conditions also play a role, though less significant.<br>"
                + "• Miscellaneous or \"other causes\" account for a notable share of accidents.<br>"
                + "• Accident patterns vary by region, requiring tailored safety measures."
                + "</body></html>");
        insightsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        insightsLabel.setForeground(Color.WHITE);

        // Start Application Button
        JButton startButton = new JButton("Start Application");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.addActionListener(e -> {
            new Visualize().setVisible(true);
            dispose();
        });

        // Layout the components with space
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0); // Add top and bottom padding
        imagePanel.add(titleLabel, gbc);

        // Add space after the title
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0); // Add more space between title and objective
        imagePanel.add(new JLabel(), gbc); // Empty JLabel to create space

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0); // Add top and bottom padding for objective
        imagePanel.add(objectiveLabel, gbc);

        // Add space after the objective
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0); // Add more space between objective and insights
        imagePanel.add(new JLabel(), gbc); // Empty JLabel to create space

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 10, 0); // Add top and bottom padding for insights
        imagePanel.add(insightsLabel, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(20, 0, 0, 0); // Add top padding before the button
        imagePanel.add(startButton, gbc);

        // Add the panel to the frame
        add(imagePanel, BorderLayout.CENTER);
    }

    class Visualize extends JFrame {
        private JComboBox<String> xAxisComboBox;
        private JComboBox<String> yAxisComboBox;
        private JButton uploadButton;
        private JButton viewDataButton;
        private JButton descriptionButton;
        private JTextArea dataArea;
        private JTable dataTable;
        private DefaultTableModel tableModel;
        private List<String> headers;
        private List<String[]> data;
        private File selectedFile;
        private JPanel chartPanel;
        private Map<String, List<Double>> dataMap;

        public Visualize() {
            setTitle("Visualize");
            setSize(1000, 800);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            // Top Navigation Bar
            JPanel topNavPanel = new JPanel();
            topNavPanel.setBackground(Color.DARK_GRAY);
            topNavPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            uploadButton = new JButton("Upload File");
            viewDataButton = new JButton("View Data");
            descriptionButton = new JButton("Dataset Description");

            // Combo boxes
            xAxisComboBox = new JComboBox<>();
            yAxisComboBox = new JComboBox<>();

            JLabel xAxisLabel = new JLabel("X-Axis:");
            xAxisLabel.setForeground(Color.WHITE); // Set text color to white

            JLabel yAxisLabel = new JLabel("Y-Axis:");
            yAxisLabel.setForeground(Color.WHITE); // Set text color to white

            topNavPanel.add(uploadButton);
            topNavPanel.add(viewDataButton);
            topNavPanel.add(descriptionButton);
            topNavPanel.add(xAxisLabel);
            topNavPanel.add(xAxisComboBox);
            topNavPanel.add(yAxisLabel);
            topNavPanel.add(yAxisComboBox);

            // Data Display Area
            dataArea = new JTextArea();
            dataArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(dataArea);

            // Table for displaying data
            tableModel = new DefaultTableModel();
            dataTable = new JTable(tableModel);
            JScrollPane tableScrollPane = new JScrollPane(dataTable);

            // Sidebar with Chart Buttons
            JPanel sideBarPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    int width = getWidth();
                    int height = getHeight();
                    Color color1 = Color.DARK_GRAY;
                    Color color2 = Color.GRAY;
                    LinearGradientPaint gradient = new LinearGradientPaint(0, 0, width, height,
                            new float[]{0.0f, 1.0f}, new Color[]{color1, color2});
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, width, height);
                    g2d.dispose();
                }
            };
            sideBarPanel.setLayout(new GridLayout(5, 1));
            sideBarPanel.setPreferredSize(new Dimension(200, getHeight()));

            JButton barChartButton = new JButton("Bar Chart");
            JButton lineChartButton = new JButton("Line Chart");
            JButton areaChartButton = new JButton("Area Chart");
            JButton pieChartButton = new JButton("Pie Chart");
            JButton radarChartButton = new JButton("Radar Chart");


            JButton[] buttons = {barChartButton, lineChartButton, areaChartButton, pieChartButton, radarChartButton};
            for (JButton button : buttons) {
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                button.setOpaque(true);
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.BLACK);
            }

            sideBarPanel.add(barChartButton);
            sideBarPanel.add(lineChartButton);
            sideBarPanel.add(areaChartButton);
            sideBarPanel.add(pieChartButton);
            sideBarPanel.add(radarChartButton);

            // Chart Panel
            chartPanel = new JPanel(new BorderLayout());

            // Action Listeners
            uploadButton.addActionListener(e -> uploadFile());
            viewDataButton.addActionListener(e -> displayData());
            descriptionButton.addActionListener(e -> showDatasetDescription());
            barChartButton.addActionListener(e -> displayBarChart());
            lineChartButton.addActionListener(e -> displayLineChart());
            areaChartButton.addActionListener(e -> displayAreaChart());
            pieChartButton.addActionListener(e -> displayPieChart());
            radarChartButton.addActionListener(e -> displayRadarChart());

            // Add components to the frame
            add(topNavPanel, BorderLayout.NORTH);
            add(tableScrollPane, BorderLayout.CENTER);
            add(sideBarPanel, BorderLayout.WEST);
            add(chartPanel, BorderLayout.EAST);
        }

        private void uploadFile() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                JOptionPane.showMessageDialog(this, "File loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        }

        private void loadData() {
            if (selectedFile == null) return;

            dataMap = new HashMap<>();
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                boolean isHeader = true;
                headers = new ArrayList<>();
                data = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (isHeader) {
                        headers = Arrays.asList(values);
                        isHeader = false;
                    } else {
                        data.add(values);
                        // Store data for charts
                        for (int i = 0; i < values.length; i++) {
                            String header = headers.get(i);
                            dataMap.putIfAbsent(header, new ArrayList<>());
                            try {
                                dataMap.get(header).add(Double.parseDouble(values[i]));
                            } catch (NumberFormatException e) {
                                // Ignore non-numeric values
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Update combo boxes
            xAxisComboBox.setModel(new DefaultComboBoxModel<>(headers.toArray(new String[0])));
            yAxisComboBox.setModel(new DefaultComboBoxModel<>(headers.toArray(new String[0])));
            updateTable();
        }

        private void updateTable() {
            tableModel.setColumnIdentifiers(headers.toArray());
            tableModel.setRowCount(0);
            for (String[] row : data) {
                tableModel.addRow(row);
            }
        }

        private void displayData() {
            if (data == null || data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No data available to display.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Display data in the JTextArea
            StringBuilder sb = new StringBuilder();
            for (String[] row : data) {
                sb.append(String.join(", ", row)).append("\n");
            }
            dataArea.setText(sb.toString());
        }

        private void showDatasetDescription() {
            if (data == null || data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No data loaded!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create a JTabbedPane
            JTabbedPane tabbedPane = new JTabbedPane();

            // Dataset Description Tab
            JPanel descriptionPanel = new JPanel(new BorderLayout());

            JTextArea descriptionArea = new JTextArea();
            descriptionArea.setEditable(false);
            descriptionArea.setText(getDatasetDescription());
            descriptionArea.setCaretPosition(0); // Scroll to top
            JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
            descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);

            // Descriptive Statistics Tab
            JPanel statsPanel = new JPanel(new BorderLayout());
            JTable statsTable = new JTable();
            statsTable.setModel(new DefaultTableModel(getStatisticsTableData(), getStatisticsTableHeaders()));
            JScrollPane statsScrollPane = new JScrollPane(statsTable);
            statsPanel.add(statsScrollPane, BorderLayout.CENTER);

            // Add tabs to tabbedPane
            tabbedPane.addTab("Dataset Description", descriptionPanel);
            tabbedPane.addTab("Descriptive Statistics", statsPanel);

            // Show tabbedPane in a JOptionPane
            JOptionPane.showMessageDialog(this, tabbedPane, "Dataset Details", JOptionPane.INFORMATION_MESSAGE);
        }

        private String getDatasetDescription() {
            StringBuilder description = new StringBuilder();
            description.append("Number of rows: ").append(data.size()).append("\n");
            description.append("Number of columns: ").append(headers.size()).append("\n");
            description.append("Headers and Data Types:\n");

            for (String header : headers) {
                description.append(header).append(": ");
                boolean hasString = false;
                boolean hasNumeric = false;

                for (String[] row : data) {
                    String value = row[getHeaderIndex(header)];
                    try {
                        Double.parseDouble(value);
                        hasNumeric = true;
                    } catch (NumberFormatException e) {
                        hasString = true;
                    }
                }

                if (hasNumeric && hasString) {
                    description.append("Mixed");
                } else if (hasNumeric) {
                    description.append("Numeric");
                } else {
                    description.append("String");
                }
                description.append("\n");
            }

            return description.toString();
        }

        private int getHeaderIndex(String header) {
            if (headers != null) {
                for (int i = 0; i < headers.size(); i++) {
                    if (headers.get(i).equals(header)) {
                        return i;
                    }
                }
            }
            return -1;
        }

        private Object[][] getStatisticsTableData() {
            Object[][] data = new Object[headers.size()][6];
            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i);
                List<Double> values = dataMap.get(header);
                if (values != null && !values.isEmpty()) {
                    data[i][0] = header;
                    data[i][1] = calculateMean(values);
                    data[i][2] = calculateMedian(values);
                    data[i][3] = calculateStandardDeviation(values);
                    data[i][4] = Collections.max(values);
                    data[i][5] = Collections.min(values);
                }
            }
            return data;
        }

        private String[] getStatisticsTableHeaders() {
            return new String[]{"Column", "Mean", "Median", "Std Dev", "Max", "Min"};
        }

        private double calculateMean(List<Double> values) {
            return values.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
        }

        private double calculateMedian(List<Double> values) {
            Collections.sort(values);
            int size = values.size();
            if (size % 2 == 0) {
                return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
            } else {
                return values.get(size / 2);
            }
        }

        private double calculateStandardDeviation(List<Double> values) {
            double mean = calculateMean(values);
            double variance = values.stream().mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(Double.NaN);
            return Math.sqrt(variance);
        }

        private void showChartFullScreen(JFreeChart chart, String title, String storyline) {
            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Create a ChartPanel and add it to the JFrame
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 600));
            frame.getContentPane().add(chartPanel, BorderLayout.CENTER);

            // Create a JPanel for the storyline
            JPanel southPanel = new JPanel();
            southPanel.setLayout(new BorderLayout());
            JLabel storylineLabel = new JLabel("<html><b>Storyline:</b> " + storyline + "</html>", JLabel.CENTER);
            storylineLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            southPanel.add(storylineLabel, BorderLayout.CENTER);

            // Add the southPanel to the south side of the JFrame
            frame.getContentPane().add(southPanel, BorderLayout.SOUTH);

            // Pack and display the JFrame in full screen
            frame.pack();
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the window
            frame.setVisible(true);
        }

        private String getStoryline(String chartType) {
            switch (chartType) {
                case "Bar Chart":
                    return "The bar chart depicts the number of pedestrian-related accidents across various States and Union Territories in India due to pedestrian fault.\n Maharashtra and Gujarat show the highest numbers, with over 2,500 and 900 cases respectively. \n States like Andhra Pradesh and Uttar Pradesh also have significant figures, while smaller regions like Lakshadweep and Daman & Diu report minimal or no incidents. \n The chart helps in comparing pedestrian fault accidents across different regions, highlighting the major contributors.";
                case "Line Chart":
                    return "The graph displays the relationship between driver fault and defects in the condition of motor vehicles. \n As the number of driver faults increases, defects in vehicle conditions show a steady upward trend. \n The graph reveals a noticeable acceleration in vehicle defects when driver faults exceed 40,000.\n This correlation suggests that increased driver error may contribute to or coincide with higher occurrences of vehicle defects.\n The rising slope after 30,000 driver faults indicates critical thresholds where the vehicle's condition deteriorates more rapidly.";
                case "Pie Chart":
                    return "\n" +
                            "The pie chart on road defects shows that Andhra Pradesh has the highest share, contributing 20% (1,339 cases), followed by Tamil Nadu at 16% (1,072 cases).\n Madhya Pradesh accounts for 12% (818 cases), and Uttar Pradesh for 11% (756 cases). \n States like West Bengal and Chhattisgarh contribute 10% and 5% respectively, while several Union Territories show negligible or zero defects.";
                case "Area Chart":
                    return "The area chart illustrates the correlation between weather conditions and defects in the condition of motor vehicles.\n As weather conditions worsen (with higher values on the x-axis), the number of vehicle defects increases significantly. \n Initially, defects remain low but start to rise after weather conditions reach a value of 200. The increase becomes more prominent around the 400 mark and continues to accelerate as weather conditions worsen. \nThe expanding area emphasizes how adverse weather can impact vehicle performance and safety, highlighting the increasing vulnerability of vehicles under deteriorating weather conditions.";
                case "Radar Chart":
                    return "The radar chart for \"Weather Condition\" highlights varying weather conditions across different states and union territories in India.\n A few regions, such as Bihar and Chhattisgarh, show significant deviations from the center, indicating pronounced weather-related issues.\n Other states like West Bengal and Uttar Pradesh also display moderate levels of impact. \nMost regions, including Delhi, Lakshadweep, and Punjab, are close to the center, suggesting minimal weather-related concerns. Overall, weather conditions seem concentrated in only a few states.";
                default:
                    return "No storyline available.";
            }
        }


        private JFreeChart createBarChart(String xAxisColumn, String yAxisColumn) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            int xIndex = getHeaderIndex(xAxisColumn);
            int yIndex = getHeaderIndex(yAxisColumn);

            // Check if indices are valid
            if (xIndex < 0 || yIndex < 0) {
                JOptionPane.showMessageDialog(this, "Invalid x-axis or y-axis column.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // Populate the dataset
            for (String[] row : data) {
                if (row != null && row.length > Math.max(xIndex, yIndex)) {
                    try {
                        double value = Double.parseDouble(row[yIndex]);
                        String category = row[xIndex];
                        dataset.addValue(value, yAxisColumn, category);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in row: " + Arrays.toString(row));
                    }
                }
            }

            // Create the bar chart
            JFreeChart chart = ChartFactory.createBarChart(
                    xAxisColumn + " vs " + yAxisColumn,  // Chart title
                    xAxisColumn,                        // X-axis label
                    yAxisColumn,                        // Y-axis label
                    dataset,                            // Dataset
                    PlotOrientation.VERTICAL,           // Orientation
                    true,                               // Include legend
                    true,                               // Tooltips
                    false                               // URLs
            );

            // Customize the x-axis labels
            CategoryPlot plot = chart.getCategoryPlot();
            CategoryAxis domainAxis = plot.getDomainAxis();

            // Set label font and rotate labels
            domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
            domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));

            // Rotate labels to prevent overlap
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4.0));

            return chart;
        }

        private void displayBarChart() {
            if (xAxisComboBox.getSelectedItem() == null || yAxisComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Select both X and Y axes.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String xAxis = (String) xAxisComboBox.getSelectedItem();
            String yAxis = (String) yAxisComboBox.getSelectedItem();

            JFreeChart chart = createBarChart(xAxis, yAxis);

            if (chart != null) {
                showChartFullScreen(chart, xAxis + " vs " + yAxis, getStoryline("Bar Chart"));
            }
        }


        private void displayLineChart() {
            if (xAxisComboBox.getSelectedItem() == null || yAxisComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Select both X and Y axes.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String xAxis = (String) xAxisComboBox.getSelectedItem();
            String yAxis = (String) yAxisComboBox.getSelectedItem();

            List<Double> xValues = dataMap.get(xAxis);
            List<Double> yValues = dataMap.get(yAxis);

            if (xValues == null || yValues == null) {
                JOptionPane.showMessageDialog(this, "Data for selected axes is missing.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            XYSeries series = new XYSeries("Line Chart");
            for (int i = 0; i < xValues.size(); i++) {
                series.add(xValues.get(i), yValues.get(i));
            }

            XYSeriesCollection dataset = new XYSeriesCollection(series);
            JFreeChart chart = ChartFactory.createXYLineChart(
                    xAxis + " vs " + yAxis,
                    xAxis,
                    yAxis,
                    dataset
            );

            showChartFullScreen(chart, xAxis + " vs " + yAxis, getStoryline("Line Chart"));
        }

        private void displayAreaChart() {
            if (xAxisComboBox.getSelectedItem() == null || yAxisComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Select both X and Y axes.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String xAxis = (String) xAxisComboBox.getSelectedItem();
            String yAxis = (String) yAxisComboBox.getSelectedItem();

            List<Double> xValues = dataMap.get(xAxis);
            List<Double> yValues = dataMap.get(yAxis);

            if (xValues == null || yValues == null) {
                JOptionPane.showMessageDialog(this, "Data for selected axes is missing.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            XYSeries series = new XYSeries("Area Chart");
            for (int i = 0; i < xValues.size(); i++) {
                series.add(xValues.get(i), yValues.get(i));
            }

            XYSeriesCollection dataset = new XYSeriesCollection(series);
            JFreeChart chart = ChartFactory.createXYAreaChart(
                    xAxis + " vs " + yAxis,
                    xAxis,
                    yAxis,
                    dataset
            );

            showChartFullScreen(chart, xAxis + " vs " + yAxis, getStoryline("Area Chart"));
        }

        private void displayRadarChart() {
            if (xAxisComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Select a category for the radar chart.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedCategory = (String) xAxisComboBox.getSelectedItem();

            // Determine the index of the selected category column
            int categoryIndex = getHeaderIndex(selectedCategory);
            if (categoryIndex == -1) {
                JOptionPane.showMessageDialog(this, "Category column not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create a dataset for the radar chart
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // Create a map to hold state/UT values
            Map<String, Double> stateValueMap = new LinkedHashMap<>(); // LinkedHashMap maintains insertion order

            // Iterate over data rows to aggregate values by state/UT
            for (String[] row : data) {
                String stateUT = row[0]; // Assuming states/UTs are in the first column
                Double value;
                try {
                    value = Double.parseDouble(row[categoryIndex]); // Parse the value from the selected column
                } catch (NumberFormatException e) {
                    continue; // Skip rows with invalid numerical values
                }

                // Add value to the map
                stateValueMap.put(stateUT, value);
            }

            // Check if stateValueMap is empty
            if (stateValueMap.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No valid data found for the selected category.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Populate the dataset
            for (Map.Entry<String, Double> entry : stateValueMap.entrySet()) {
                String stateUT = entry.getKey();
                double value = entry.getValue();
                dataset.addValue(value, "Values", stateUT);
            }

            // Create the radar chart plot
            SpiderWebPlot radarPlot = new SpiderWebPlot(dataset);
            JFreeChart radarChart = new JFreeChart(
                    "Radar Chart for " + selectedCategory,
                    JFreeChart.DEFAULT_TITLE_FONT,
                    radarPlot,
                    false
            );

            // Show the chart full screen
            showChartFullScreen(radarChart, "Radar Chart", getStoryline("Radar Chart"));
        }

        private void displayPieChart() {
            // Ensure a category (column) is selected
            if (xAxisComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Select a category for the pie chart.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedCategory = (String) xAxisComboBox.getSelectedItem();

            // Determine the index of the selected category column
            int categoryIndex = getHeaderIndex(selectedCategory);
            if (categoryIndex == -1) {
                JOptionPane.showMessageDialog(this, "Category column not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create a dataset for the pie chart
            DefaultPieDataset dataset = new DefaultPieDataset();

            // Iterate over data rows to aggregate values by state/UT
            for (String[] row : data) {
                String stateUT = row[0]; // Assuming states/UTs are in the first column
                Double value;
                try {
                    value = Double.parseDouble(row[categoryIndex]); // Parse the value from the selected column
                } catch (NumberFormatException e) {
                    continue; // Skip rows with invalid numerical values
                }

                // Add value to the dataset
                dataset.setValue(stateUT + " (" + String.format("%.1f", value) + ")", value);
            }

            // Check if dataset is empty
            if (dataset.getKeys().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No valid data found for the selected category.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create the pie chart
            JFreeChart chart = ChartFactory.createPieChart(
                    "Pie Chart for " + selectedCategory,
                    dataset,
                    true,
                    true,
                    false
            );

            // Customize the chart to show percentages in the legend
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: ({2})"));

            // Show the chart full screen
            showChartFullScreen(chart, "Pie Chart", getStoryline("Pie Chart"));
        }
        // header index method for reference
        private int getHeaderIndex1(String columnName) {
            // Example header array with column names
            String[] header = {"States/UTs", "Driver Fault", "Fault of Cyclist", "Fault of Pedestrian", "Defect in Condition of Motor Vehicle", "Defect in Road Condition", "Weather Condition", "Other Causes"};

            // Find the index of the column in the header
            for (int i = 0; i < header.length; i++) {
                if (header[i].equalsIgnoreCase(columnName)) {
                    return i;
                }
            }
            return -1; // Return -1 if column is not found
        }
    }
}