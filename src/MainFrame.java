import info.clearthought.layout.TableLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: igorevsukov
 * Date: Dec 28, 2009
 * Time: 10:26:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainFrame extends JFrame {
    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
//    private JMenu sampleMenu = new JMenu("Sample");
    private JMenuItem openFileMenuItem = new JMenuItem("Open");
    private JMenuItem exitMenuItem = new JMenuItem("Exit");

    private JTabbedPane tabs = new JTabbedPane();

    Sample sample;

    private JTable sampleTable;
    private AbstractTableModel sampleTableModel;
//    private XYSeriesCollection sampleGraphDataset;
    private XYSeriesCollection autocorelationGraphDataset;

    private JTable spectrumsTable;
    private AbstractTableModel spectrumsTableModel;
    private XYSeriesCollection spectrumsGraphDataset;

    private JTextField taumTextField = new JTextField("0");
    private XYSeriesCollection spectrumDensityGraphDataset;
    private XYSeriesCollection spectrumNormalDensityGraphDataset;

    //Sinus Low
    private JTextField slMTextField = new JTextField("4");
    private JTextField slTTextField = new JTextField("1");
    private JTextField slBTextField = new JTextField(String.valueOf(0.1));
    private JButton slUpdateButton = new JButton("Update");
    private JTable slParamsTable;
    private AbstractTableModel slParamsTableModel;
    private XYSeriesCollection slGraphDataset = new XYSeriesCollection();
    private XYSeriesCollection slPassGraphDataset = new XYSeriesCollection();

    //Sinus High
    private JTextField shMTextField = new JTextField("4");
    private JTextField shTTextField = new JTextField("1");
    private JTextField shBTextField = new JTextField(String.valueOf(0.1));
    private JButton shUpdateButton = new JButton("Update");
    private JTable shParamsTable;
    private AbstractTableModel shParamsTableModel;
    private XYSeriesCollection shGraphDataset = new XYSeriesCollection();
    private XYSeriesCollection shPassGraphDataset = new XYSeriesCollection();

    //obscured filter
    private JTextField ofMTextField = new JTextField("4");
    private JTextField ofTTextField = new JTextField("1");
    private JTextField ofBTextField = new JTextField(String.valueOf(0.1));
    private JTextField ofFcTextField = new JTextField(String.valueOf(0.1));
    private JButton ofUpdateButton = new JButton("Update");
    private JTable ofParamsTable;
    private AbstractTableModel ofParamsTableModel;
    private XYSeriesCollection ofGraphDataset = new XYSeriesCollection();
    private XYSeriesCollection ofPassGraphDataset = new XYSeriesCollection();

    //pass filter
    private JTextField pfMTextField = new JTextField("4");
    private JTextField pfTTextField = new JTextField("1");
    private JTextField pfBTextField = new JTextField(String.valueOf(0.1));
    private JTextField pfFcTextField = new JTextField(String.valueOf(0.1));
    private JButton pfUpdateButton = new JButton("Update");
    private JTable pfParamsTable;
    private AbstractTableModel pfParamsTableModel;
    private XYSeriesCollection pfGraphDataset = new XYSeriesCollection();
    private XYSeriesCollection pfPassGraphDataset = new XYSeriesCollection();

    public MainFrame() {
        setTitle("RP - Lab 2 - Main Window");

        //menu
        openFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser openFileChooser = new JFileChooser();
                openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                openFileChooser.setMultiSelectionEnabled(false);
                // TODO: убрать в релизе
				openFileChooser.setCurrentDirectory(new File("/Users/igorevsukov/Documents/DNU/RP/RP_Lab_1/"));
                if (openFileChooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
                    String fileName = openFileChooser.getSelectedFile().getAbsolutePath();
                    sample = new Sample(fileName);
                    
//                    updateData();
                    sampleTable.tableChanged(null);
                    refreshAutocorelationGraphDataset();

                    spectrumsTable.tableChanged(null);
                    refreshSpectrumsGraphDataset();

                    refreshSpectrumDensities();

                    taumTextField.setText(String.valueOf(sample.getTaum()));
                }
            }

        });

        fileMenu.add(openFileMenuItem);
        fileMenu.addSeparator();

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        //original table
		sampleTableModel = new AbstractTableModel(){
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() { return 2; }

			@Override
			public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                else return Double.class;
			}

			@Override
			public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "i";
                    case 1: return "x[i]";
                    default: return "";
                }
			}

			@Override
			public int getRowCount() { return sample == null ? 0 : sample.size(); }
            
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
                    case 0: return rowIndex;
                    case 1: return sample.get(rowIndex);
                    default: return Double.NaN;
                }
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
		sampleTable = new JTable(sampleTableModel);


        autocorelationGraphDataset = new XYSeriesCollection();
        JFreeChart autocorelationChart = ChartFactory.createScatterPlot("Autocorelation","t","x(t)",autocorelationGraphDataset, PlotOrientation.VERTICAL,true,true,false);
        ChartPanel autocorelationChartPanel = new ChartPanel(autocorelationChart);
        autocorelationChartPanel.setDoubleBuffered(true);
        XYLineAndShapeRenderer autocorelationRenderer = new XYLineAndShapeRenderer();
        autocorelationRenderer.setSeriesLinesVisible(0, true);
        autocorelationRenderer.setSeriesShapesVisible(0, false);
        autocorelationChart.getXYPlot().setRenderer(autocorelationRenderer);

        JPanel sampleAutocorelationPanel = new JPanel(new TableLayout(new double[][] {{0.30,0.70},{TableLayout.FILL}}));
		sampleAutocorelationPanel.add(new JScrollPane(sampleTable),"0, 0");
        sampleAutocorelationPanel.add(autocorelationChartPanel,"1, 0");
		tabs.add(sampleAutocorelationPanel, "Sample && Autocorelation",0);

        //spectrums and autocorelation graph
        spectrumsTableModel = new AbstractTableModel(){
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() { return 4; }

			@Override
			public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                else return Double.class;
			}

			@Override
			public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "i";
                    case 1: return "A";
                    case 2: return "P";
                    case 3: return "E";
                    default: return "";
                }
			}

			@Override
			public int getRowCount() { return sample == null ? 0 : sample.getAmplitudeSpectrum().length; }

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
                    case 0: return rowIndex;
                    case 1: return sample.getAmplitudeSpectrum()[rowIndex];
                    case 2: return sample.getPhaseSpectrum()[rowIndex];
                    case 3: return sample.getEnergySpectrum()[rowIndex];
                    default: return Double.NaN;
                }
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
        spectrumsTable = new JTable(spectrumsTableModel);
        spectrumsGraphDataset = new XYSeriesCollection();
        JFreeChart spectrumsChart = ChartFactory.createScatterPlot("Spectrums","k","s(k)",spectrumsGraphDataset, PlotOrientation.VERTICAL,true,true,false);
        ChartPanel spectrumsChartPanel = new ChartPanel(spectrumsChart);
        spectrumsChartPanel.setDoubleBuffered(true);
        XYLineAndShapeRenderer spectrumsRenderer = new XYLineAndShapeRenderer();
        spectrumsRenderer.setSeriesLinesVisible(0, true);
        spectrumsRenderer.setSeriesShapesVisible(0, false);
        spectrumsRenderer.setSeriesLinesVisible(1, true);
        spectrumsRenderer.setSeriesShapesVisible(1, false);
        spectrumsRenderer.setSeriesLinesVisible(2, true);
        spectrumsRenderer.setSeriesShapesVisible(2, false);
        spectrumsChart.getXYPlot().setRenderer(spectrumsRenderer);

        JPanel spectrumsPanel = new JPanel(new TableLayout(new double[][] {{0.30,0.70},{TableLayout.FILL}}));
        spectrumsPanel.add(new JScrollPane(spectrumsTable),"0, 0");
		spectrumsPanel.add(spectrumsChartPanel,"1, 0");        
		tabs.add("Spectrums",spectrumsPanel);


        //spectrums density
        spectrumDensityGraphDataset = new XYSeriesCollection();
        JFreeChart spectrumDensityChart = ChartFactory.createScatterPlot("Spectrum Density","f","s(f)",spectrumDensityGraphDataset, PlotOrientation.VERTICAL,false,true,false);
        ChartPanel spectrumDensityChartPanel = new ChartPanel(spectrumDensityChart);
        spectrumDensityChartPanel.setDoubleBuffered(true);
        XYLineAndShapeRenderer spectrumDensityRenderer = new XYLineAndShapeRenderer();
        spectrumDensityRenderer.setSeriesLinesVisible(0, true);
        spectrumDensityRenderer.setSeriesShapesVisible(0, false);
        spectrumDensityChart.getXYPlot().setRenderer(spectrumDensityRenderer);

        spectrumNormalDensityGraphDataset = new XYSeriesCollection();
        JFreeChart spectrumNormalDensityChart = ChartFactory.createScatterPlot("Normalized Spectrum Density","f","s(f)",spectrumNormalDensityGraphDataset, PlotOrientation.VERTICAL,false,true,false);
        ChartPanel spectrumNormalDensityChartPanel = new ChartPanel(spectrumNormalDensityChart);
        spectrumNormalDensityChartPanel.setDoubleBuffered(true);
        XYLineAndShapeRenderer spectrumNormalDensityRenderer = new XYLineAndShapeRenderer();
        spectrumNormalDensityRenderer.setSeriesLinesVisible(0, true);
        spectrumNormalDensityRenderer.setSeriesShapesVisible(0, false);
        spectrumNormalDensityChart.getXYPlot().setRenderer(spectrumNormalDensityRenderer);

        JPanel spectrumsDensityPanel = new JPanel(new TableLayout(new double[][] {{0.50,0.50},{30,TableLayout.FILL}}));
        JPanel taumPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        taumPanel.add(new JLabel("taum"));        
        taumPanel.add(taumTextField);
        taumTextField.setMinimumSize(new Dimension(50,20));
        taumTextField.setPreferredSize(new Dimension(50,20));
        taumTextField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sample != null) {
                    sample.setTaum(Double.valueOf(taumTextField.getText()));
                    refreshSpectrumDensities();
                }
            }
        });
        spectrumsDensityPanel.add(taumPanel,"0, 0");
        spectrumsDensityPanel.add(spectrumDensityChartPanel,"0, 1");
        spectrumsDensityPanel.add(spectrumNormalDensityChartPanel,"1, 1");
        tabs.add("Spectrums Density",spectrumsDensityPanel);


        //Sinus Low
        slParamsTableModel = new AbstractTableModel(){
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() { return 4; }

			@Override
			public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                else return Double.class;
			}

			@Override
			public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "N";
                    case 1: return "a0";
                    case 2: return "a1";
                    case 3: return "b";
                    default: return "";
                }
			}

			@Override
			public int getRowCount() { return sample == null || sample.getASL() == null ? 0 : sample.getASL()[0].length; }

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
                    case 0: return rowIndex;
                    case 1: return sample.getASL()[0][rowIndex];
                    case 2: return sample.getASL()[1][rowIndex];
                    case 3: if (rowIndex < 3) return sample.getBSL()[rowIndex]; else return Double.NaN;
                    default: return Double.NaN;
                }
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
        slParamsTable = new JTable(slParamsTableModel);

        JFreeChart slChart = ChartFactory.createScatterPlot("Sinus Low","k","s(k)",slGraphDataset, PlotOrientation.VERTICAL,true,true,false);
        ChartPanel slChartPanel = new ChartPanel(slChart);
        slChartPanel.setDoubleBuffered(true);
        XYLineAndShapeRenderer slRenderer = new XYLineAndShapeRenderer();
        slRenderer.setSeriesLinesVisible(0, true);
        slRenderer.setSeriesShapesVisible(0, false);
        slRenderer.setSeriesLinesVisible(1, true);
        slRenderer.setSeriesShapesVisible(1, false);
        slChart.getXYPlot().setRenderer(slRenderer);

        JFreeChart slPassChart = ChartFactory.createScatterPlot("Transfer Func","k","s(k)",slPassGraphDataset, PlotOrientation.VERTICAL,false,true,false);
        ChartPanel slPassChartPanel = new ChartPanel(slPassChart);
        slPassChartPanel.setDoubleBuffered(true);
        XYLineAndShapeRenderer slPassRenderer = new XYLineAndShapeRenderer();
        slPassRenderer.setSeriesLinesVisible(0, true);
        slPassRenderer.setSeriesShapesVisible(0, false);
        slPassChart.getXYPlot().setRenderer(slPassRenderer);

        slUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int M = Integer.valueOf(slMTextField.getText());
                double T = Double.valueOf(slTTextField.getText());
                double B = Double.valueOf(slBTextField.getText());
                double[] y = sample.calcSinusLow(M, T, B);
                slParamsTable.tableChanged(null);

                slGraphDataset.removeAllSeries();
                XYSeries sampleSeries = new XYSeries("Sample");
                for (int i=0; i<sample.size(); i++) sampleSeries.add(i, sample.get(i));
                slGraphDataset.addSeries(sampleSeries);

                XYSeries slSeries = new XYSeries("Sinus Low");
                for (int i=0; i<y.length; i++) slSeries.add(i, y[i]);
                slGraphDataset.addSeries(slSeries);

                //передаточная функция
                slPassGraphDataset.removeAllSeries();
                XYSeries slPassSeries = new XYSeries("transfer function");
                double[] freq = new double[100];
                for (int i = 0;  i < 100;  i++) freq[i] = (double)i/200.0;
                double[] abz = Sample.CalcABZ(M, T, freq, sample.getASL(), sample.getBSL());
                for (int i = 0;  i < abz.length;  i++) slPassSeries.add(freq[i],abz[i]);
                slPassGraphDataset.addSeries(slPassSeries);
                
            }
        });

        JPanel slPanel = new JPanel(new TableLayout(new double[][] {{20,50, 10, 20,50, 10, 20,50, 10, 20,50, 10, TableLayout.FILL},{20,10,0.50,10,0.50}}));
        slPanel.add(new JLabel("M"),"0, 0");
        slPanel.add(slMTextField,"1, 0");
        slPanel.add(new JLabel("T"),"3, 0");
        slPanel.add(slTTextField,"4, 0");
        slPanel.add(new JLabel("B"),"6, 0");
        slPanel.add(slBTextField,"7, 0");
        slPanel.add(slUpdateButton,"12, 0");
        slPanel.add(new JScrollPane(slParamsTable),"0, 2, 10, 4");
        slPanel.add(slChartPanel,"12, 2");
        slPanel.add(slPassChartPanel, "12, 4");

        tabs.add("Sin Low", slPanel);



        //Sinus High
        shParamsTableModel = new AbstractTableModel(){
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() { return 4; }

			@Override
			public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                else return Double.class;
			}

			@Override
			public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "N";
                    case 1: return "a0";
                    case 2: return "a1";
                    case 3: return "b";
                    default: return "";
                }
			}

			@Override
			public int getRowCount() { return sample == null || sample.getASH() == null ? 0 : sample.getASH()[0].length; }

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
                    case 0: return rowIndex;
                    case 1: return sample.getASH()[0][rowIndex];
                    case 2: return sample.getASH()[1][rowIndex];
                    case 3: if (rowIndex < 3) return sample.getBSH()[rowIndex]; else return Double.NaN;
                    default: return Double.NaN;
                }
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
        shParamsTable = new JTable(shParamsTableModel);

        JFreeChart shChart = ChartFactory.createScatterPlot("Sinus High","k","s(k)",shGraphDataset, PlotOrientation.VERTICAL,true,true,false);
        ChartPanel shChartPanel = new ChartPanel(shChart);
        shChartPanel.setDoubleBuffered(true);
        XYLineAndShapeRenderer shRenderer = new XYLineAndShapeRenderer();
        shRenderer.setSeriesLinesVisible(0, true);
        shRenderer.setSeriesShapesVisible(0, false);
        shRenderer.setSeriesLinesVisible(1, true);
        shRenderer.setSeriesShapesVisible(1, false);
        shChart.getXYPlot().setRenderer(shRenderer);

        JFreeChart shPassChart = ChartFactory.createScatterPlot("Transfer Func","k","s(k)",shPassGraphDataset, PlotOrientation.VERTICAL,false,true,false);
        ChartPanel shPassChartPanel = new ChartPanel(shPassChart);
        shPassChartPanel.setDoubleBuffered(true);
        XYLineAndShapeRenderer shPassRenderer = new XYLineAndShapeRenderer();
        shPassRenderer.setSeriesLinesVisible(0, true);
        shPassRenderer.setSeriesShapesVisible(0, false);
        shPassChart.getXYPlot().setRenderer(shPassRenderer);

        shUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int M = Integer.valueOf(shMTextField.getText());
                double T = Double.valueOf(shTTextField.getText());
                double B = Double.valueOf(shBTextField.getText());
                double[] y = sample.calcSinusHigh(M, T, B);
                shParamsTable.tableChanged(null);

                shGraphDataset.removeAllSeries();
                XYSeries sampleSeries = new XYSeries("Sample");
                for (int i=0; i<sample.size(); i++) sampleSeries.add(i, sample.get(i));
                shGraphDataset.addSeries(sampleSeries);

                XYSeries shSeries = new XYSeries("Sinus High");
                for (int i=0; i<y.length; i++) shSeries.add(i, y[i]);
                shGraphDataset.addSeries(shSeries);

                //передаточная функция
                shPassGraphDataset.removeAllSeries();
                XYSeries shPassSeries = new XYSeries("transfer function");
                double[] freq = new double[100];
                for (int i = 0;  i < 100;  i++) freq[i] = (double)i/200.0;
                double[] abz = Sample.CalcABZ(M, T, freq, sample.getASH(), sample.getBSH());
                for (int i = 0;  i < abz.length;  i++) shPassSeries.add(freq[i],abz[i]);
                shPassGraphDataset.addSeries(shPassSeries);

            }
        });

        JPanel shPanel = new JPanel(new TableLayout(new double[][] {{20,50, 10, 20,50, 10, 20,50, 10, 20,50, 10, TableLayout.FILL},{20,10,0.50,10,0.50}}));
        shPanel.add(new JLabel("M"),"0, 0");
        shPanel.add(shMTextField,"1, 0");
        shPanel.add(new JLabel("T"),"3, 0");
        shPanel.add(shTTextField,"4, 0");
        shPanel.add(new JLabel("B"),"6, 0");
        shPanel.add(shBTextField,"7, 0");
        shPanel.add(shUpdateButton,"12, 0");
        shPanel.add(new JScrollPane(shParamsTable),"0, 2, 10, 4");
        shPanel.add(shChartPanel,"12, 2");
        shPanel.add(shPassChartPanel, "12, 4");

        tabs.add("Sin High", shPanel);

        //obscured filter
        ofParamsTableModel = new AbstractTableModel(){
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() { return 4; }

			@Override
			public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                else return Double.class;
			}

			@Override
			public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "N";
                    case 1: return "a0";
                    case 2: return "a1";
                    case 3: return "b";
                    default: return "";
                }
			}

			@Override
			public int getRowCount() { return sample == null || sample.getAOF() == null ? 0 : sample.getAOF()[0].length; }

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
                    case 0: return rowIndex;
                    case 1: return sample.getAOF()[0][rowIndex];
                    case 2: return sample.getAOF()[1][rowIndex];
                    case 3: if (rowIndex < 3) return sample.getBOF()[rowIndex]; else return Double.NaN;
                    default: return Double.NaN;
                }
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
        ofParamsTable = new JTable(ofParamsTableModel);

        JFreeChart ofChart = ChartFactory.createScatterPlot("Obscured Filter","k","s(k)",ofGraphDataset, PlotOrientation.VERTICAL,true,true,false);
        ChartPanel ofChartPanel = new ChartPanel(ofChart);
        ofChartPanel.setDoubleBuffered(true);
        XYLineAndShapeRenderer ofRenderer = new XYLineAndShapeRenderer();
        ofRenderer.setSeriesLinesVisible(0, true);
        ofRenderer.setSeriesShapesVisible(0, false);
        ofRenderer.setSeriesLinesVisible(1, true);
        ofRenderer.setSeriesShapesVisible(1, false);
        ofChart.getXYPlot().setRenderer(ofRenderer);

        JFreeChart ofPassChart = ChartFactory.createScatterPlot("Transfer Func","k","s(k)",ofPassGraphDataset, PlotOrientation.VERTICAL,false,true,false);
        ChartPanel ofPassChartPanel = new ChartPanel(ofPassChart);
        ofPassChartPanel.setDoubleBuffered(true);
        XYLineAndShapeRenderer ofPassRenderer = new XYLineAndShapeRenderer();
        ofPassRenderer.setSeriesLinesVisible(0, true);
        ofPassRenderer.setSeriesShapesVisible(0, false);
        ofPassChart.getXYPlot().setRenderer(ofPassRenderer);

        ofUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int M = Integer.valueOf(ofMTextField.getText());
                double T = Double.valueOf(ofTTextField.getText());
                double B = Double.valueOf(ofBTextField.getText());
                double Fc = Double.valueOf(ofFcTextField.getText());
                double[] y = sample.calcObscuredFilter(M, T, B, Fc);
                ofParamsTable.tableChanged(null);

                ofGraphDataset.removeAllSeries();
                XYSeries sampleSeries = new XYSeries("Sample");
                for (int i=0; i<sample.size(); i++) sampleSeries.add(i, sample.get(i));
                ofGraphDataset.addSeries(sampleSeries);

                XYSeries ofSeries = new XYSeries("Obscured Filter");
                for (int i=0; i<y.length; i++) ofSeries.add(i, y[i]);
                ofGraphDataset.addSeries(ofSeries);

                //передаточная функция
                ofPassGraphDataset.removeAllSeries();
                XYSeries ofPassSeries = new XYSeries("transfer function");
                double[] freq = new double[100];
                for (int i = 0;  i < 100;  i++) freq[i] = (double)i/200.0;
                double[] abz = Sample.CalcABZ(M, T, freq, sample.getAOF(), sample.getBOF());
                for (int i = 0;  i < abz.length;  i++) ofPassSeries.add(freq[i],abz[i]);
                ofPassGraphDataset.addSeries(ofPassSeries);

            }
        });

        JPanel ofPanel = new JPanel(new TableLayout(new double[][] {{20,50, 10, 20,50, 10, 20,50, 10, 20,50, 10, TableLayout.FILL},{20,10,0.50,10,0.50}}));
        ofPanel.add(new JLabel("M"),"0, 0");
        ofPanel.add(ofMTextField,"1, 0");
        ofPanel.add(new JLabel("T"),"3, 0");
        ofPanel.add(ofTTextField,"4, 0");
        ofPanel.add(new JLabel("B"),"6, 0");
        ofPanel.add(ofBTextField,"7, 0");
        ofPanel.add(new JLabel("Fc"),"9, 0");
        ofPanel.add(ofFcTextField,"10, 0");
        ofPanel.add(ofUpdateButton,"12, 0");
        ofPanel.add(new JScrollPane(ofParamsTable),"0, 2, 10, 4");
        ofPanel.add(ofChartPanel,"12, 2");
        ofPanel.add(ofPassChartPanel, "12, 4");

        tabs.add("Obscured Filter", ofPanel);


        //pass filter
        pfParamsTableModel = new AbstractTableModel(){
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() { return 4; }

			@Override
			public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                else return Double.class;
			}

			@Override
			public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "N";
                    case 1: return "a0";
                    case 2: return "a1";
                    case 3: return "b";
                    default: return "";
                }
			}

			@Override
			public int getRowCount() { return sample == null || sample.getAPF() == null ? 0 : sample.getAPF()[0].length; }

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
                    case 0: return rowIndex;
                    case 1: return sample.getAPF()[0][rowIndex];
                    case 2: return sample.getAPF()[1][rowIndex];
                    case 3: if (rowIndex < 3) return sample.getBPF()[rowIndex]; else return Double.NaN;
                    default: return Double.NaN;
                }
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
        pfParamsTable = new JTable(pfParamsTableModel);

        JFreeChart pfChart = ChartFactory.createScatterPlot("Pass Filter","k","s(k)",pfGraphDataset, PlotOrientation.VERTICAL,true,true,false);
        ChartPanel pfChartPanel = new ChartPanel(pfChart);
        pfChartPanel.setDoubleBuffered(true);
        XYLineAndShapeRenderer pfRenderer = new XYLineAndShapeRenderer();
        pfRenderer.setSeriesLinesVisible(0, true);
        pfRenderer.setSeriesShapesVisible(0, false);
        pfRenderer.setSeriesLinesVisible(1, true);
        pfRenderer.setSeriesShapesVisible(1, false);
        pfChart.getXYPlot().setRenderer(pfRenderer);

        JFreeChart pfPassChart = ChartFactory.createScatterPlot("Transfer Func","k","s(k)",pfPassGraphDataset, PlotOrientation.VERTICAL,false,true,false);
        ChartPanel pfPassChartPanel = new ChartPanel(pfPassChart);
        pfPassChartPanel.setDoubleBuffered(true);
        XYLineAndShapeRenderer pfPassRenderer = new XYLineAndShapeRenderer();
        pfPassRenderer.setSeriesLinesVisible(0, true);
        pfPassRenderer.setSeriesShapesVisible(0, false);
        pfPassChart.getXYPlot().setRenderer(pfPassRenderer);

        pfUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int M = Integer.valueOf(pfMTextField.getText());
                double T = Double.valueOf(pfTTextField.getText());
                double B = Double.valueOf(pfBTextField.getText());
                double Fc = Double.valueOf(pfFcTextField.getText());
                double[] y = sample.calcPassFilter(M, T, B, Fc);
                pfParamsTable.tableChanged(null);

                pfGraphDataset.removeAllSeries();
                XYSeries sampleSeries = new XYSeries("Sample");
                for (int i=0; i<sample.size(); i++) sampleSeries.add(i, sample.get(i));
                pfGraphDataset.addSeries(sampleSeries);

                XYSeries pfSeries = new XYSeries("Pass Filter");
                for (int i=0; i<y.length; i++) pfSeries.add(i, y[i]);
                pfGraphDataset.addSeries(pfSeries);

                //передаточная функция
                pfPassGraphDataset.removeAllSeries();
                XYSeries pfPassSeries = new XYSeries("transfer function");
                double[] freq = new double[100];
                for (int i = 0;  i < 100;  i++) freq[i] = (double)i/200.0;
                double[] abz = Sample.CalcABZ(M, T, freq, sample.getAPF(), sample.getBPF());
                for (int i = 0;  i < abz.length;  i++) pfPassSeries.add(freq[i],abz[i]);
                pfPassGraphDataset.addSeries(pfPassSeries);

            }
        });

        JPanel pfPanel = new JPanel(new TableLayout(new double[][] {{20,50, 10, 20,50, 10, 20,50, 10, 20,50, 10, TableLayout.FILL},{20,10,0.50,10,0.50}}));
        pfPanel.add(new JLabel("M"),"0, 0");
        pfPanel.add(pfMTextField,"1, 0");
        pfPanel.add(new JLabel("T"),"3, 0");
        pfPanel.add(pfTTextField,"4, 0");
        pfPanel.add(new JLabel("B"),"6, 0");
        pfPanel.add(pfBTextField,"7, 0");
        pfPanel.add(new JLabel("Fc"),"9, 0");
        pfPanel.add(pfFcTextField,"10, 0");
        pfPanel.add(pfUpdateButton,"12, 0");
        pfPanel.add(new JScrollPane(pfParamsTable),"0, 2, 10, 4");
        pfPanel.add(pfChartPanel,"12, 2");
        pfPanel.add(pfPassChartPanel, "12, 4");

        tabs.add("Pass Filter", pfPanel);
        

        getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabs, BorderLayout.CENTER);
    }

    public void refreshAutocorelationGraphDataset() {
        autocorelationGraphDataset.removeAllSeries();
        int tauMax = sample.size() / 3;
        XYSeries autocorelationSeries = new XYSeries("Autocorelation");
        for (int tau = 0; tau < tauMax; tau++) {
            autocorelationSeries.add(tau, sample.calcAutoCorrelation(tau));
        }
        autocorelationGraphDataset.addSeries(autocorelationSeries);
    }

    public void refreshSpectrumsGraphDataset() {
        spectrumsGraphDataset.removeAllSeries();
        XYSeries as = new XYSeries("Amplitude");
        XYSeries ps = new XYSeries("Phase");
        XYSeries pe = new XYSeries("Energy");
        int count;
        if (sample.size() % 2 == 0) {
            count = sample.size() / 2 - 1;
        } else {
            count = (sample.size() - 1) / 2;
        }
        double freq = 0;
        double fstep = 0.5 / count;
        for (int i=0; i<sample.getAmplitudeSpectrum().length; i++) {
            as.add(freq, sample.getAmplitudeSpectrum()[i]);
            ps.add(freq, sample.getPhaseSpectrum()[i]);
            pe.add(freq, sample.getEnergySpectrum()[i]);
            freq += fstep;
        }
        spectrumsGraphDataset.addSeries(as);
        spectrumsGraphDataset.addSeries(ps);
        spectrumsGraphDataset.addSeries(pe);
    }

    public void refreshSpectrumDensities() {
        spectrumDensityGraphDataset.removeAllSeries();
        spectrumNormalDensityGraphDataset.removeAllSeries();

        XYSeries sds = new XYSeries("Spectrum Density");
        XYSeries snds = new XYSeries("Spectrum Normal Density");
        for (double i = 0; i<= 0.501; i+= 0.01) {
            sds.add(i, sample.calcSpectrumDensity(i));
            snds.add(i, sample.calcNormalizedSpectrumDensity(i));
        }

        spectrumDensityGraphDataset.addSeries(sds);
        spectrumNormalDensityGraphDataset.addSeries(snds);
    }
}
