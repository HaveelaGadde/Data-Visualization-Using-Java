# Accident Data Visualization in Java

This project visualizes accident-related data using Java and relevant libraries. It loads accident data from a CSV file (`Sample_data.csv`) and generates visual insights using charts/graphs for better understanding of patterns in the dataset.

## 📁 Files Included

- `AccidentVisualizations.java` – The main Java program for data visualization.
- `Sample_data.csv` – Sample dataset containing accident-related information.
- `.gitattributes` – Git LFS configuration file (optional, if large files are used).

## 📊 Features

- Load CSV accident data.
- Generate visual representations (e.g., pie chart, bar chart).
- Parse and analyze accident statistics from structured data.

## 🔧 Requirements

- Java Development Kit (JDK 8 or later)
- Java IDE or command-line tools (e.g., IntelliJ, Eclipse, or javac/java)
- JFreeChart library (for chart visualizations)

## 📦 Setup Instructions

1. **Clone the repository**:

   ```bash
   git clone https://github.com/yourusername/AccidentDataVisualization.git
   cd AccidentDataVisualization
   ```

2. **Compile the Java file** (ensure the required libraries like JFreeChart are in the classpath):

   ```bash
   javac -cp ".:path/to/jfreechart.jar:path/to/other/libs/*" AccidentVisualizations.java
   ```

3. **Run the application**:

   ```bash
   java -cp ".:path/to/jfreechart.jar:path/to/other/libs/*" AccidentVisualizations
   ```

4. **View results** – Visual charts will appear in new windows.

## 📝 Notes

- The sample dataset can be modified to include more real-world accident data.
- Make sure to match column names and data types when using custom datasets.

## 📄 License

This project is licensed under the MIT License.
