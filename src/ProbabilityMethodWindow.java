import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class ProbabilityMethodWindow extends JFrame {
	JList useList;
	JList ignoreList;
	DefaultListModel ignoreHandler;
	DefaultListModel useHandler;
	JRadioButton _regressionMethod;
	JRadioButton _stupidMethod;

	protected ProbabilityMethodWindow() {
		super("Probability Model");
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// look and feel
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e) { e.printStackTrace(); }
		this.setPreferredSize(new Dimension(400,250));
		this.setResizable(false);
		
		JPanel yearLists = new JPanel();
		yearLists.setLayout(new BoxLayout(yearLists, BoxLayout.X_AXIS));
		
		ignoreHandler = new DefaultListModel();
		for(int year: State.getInstance().getIgnoredYears()) ignoreHandler.addElement(year);
		sort(ignoreHandler);

		JPanel ignoreContainer = new JPanel(new BorderLayout());
		
		JLabel ignoreTitle = new JLabel("years to ignore");
		
		ignoreList = new JList(ignoreHandler);
		ignoreList.setBackground(new Color(238, 238, 238));
		ignoreList.setLayoutOrientation(JList.VERTICAL);
		ignoreList.setFixedCellWidth(120);
		ignoreList.setFixedCellHeight(20);

		JPanel ignorePanel = new JPanel(new BorderLayout());
		ignorePanel.setPreferredSize(new Dimension(120,160));
		ignorePanel.setMaximumSize(new Dimension(120,160));
		ignorePanel.setBorder(new LineBorder(Color.GRAY));
		ignorePanel.add(ignoreList, BorderLayout.NORTH);

		ignoreContainer.add(ignoreTitle, BorderLayout.NORTH);
		ignoreContainer.add(ignorePanel, BorderLayout.AFTER_LINE_ENDS);
		
		useHandler = new DefaultListModel();
		for(int year: State.getInstance().getYears()) useHandler.addElement(year);
		sort(useHandler);

		JPanel useContainer = new JPanel(new BorderLayout());
		
		JLabel useTitle = new JLabel("years to use");
		
		useList = new JList(useHandler);
		useList.setBackground(new Color(238, 238, 238));
		useList.setLayoutOrientation(JList.VERTICAL);
		useList.setFixedCellWidth(120);
		useList.setFixedCellHeight(20);

		JPanel usePanel = new JPanel(new BorderLayout());
		usePanel.setPreferredSize(new Dimension(120,160));
		usePanel.setMaximumSize(new Dimension(120,200));
		usePanel.setBorder(new LineBorder(Color.GRAY));
		usePanel.add(useList, BorderLayout.NORTH);
		
		useContainer.add(useTitle, BorderLayout.NORTH);
		useContainer.add(usePanel, BorderLayout.AFTER_LINE_ENDS);

		JButton add = new JButton(">>");
		add.addActionListener(new AddListener());
		JButton remove = new JButton("<<");
		remove.addActionListener(new RemoveListener());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(add);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		buttonPanel.add(remove);

		yearLists.add(Box.createRigidArea(new Dimension(50, 0)));
		yearLists.add(ignoreContainer);
		yearLists.add(Box.createRigidArea(new Dimension(10, 0)));
		yearLists.add(buttonPanel);
		yearLists.add(Box.createRigidArea(new Dimension(10, 0)));
		yearLists.add(useContainer);

		_regressionMethod = new JRadioButton("Calculate probability using regression (best results)");
		_regressionMethod.addItemListener(new MethodListener());
		this.add(_regressionMethod);
		
		_stupidMethod = new JRadioButton("Calculate probability using past results for lottery number");
		_stupidMethod.addItemListener(new MethodListener());
		this.add(_stupidMethod);
		
		if(State.getInstance().useRegressionProbability()) {
			_regressionMethod.setSelected(true);
			useList.setEnabled(false);
			ignoreList.setEnabled(false);
		}
		else _stupidMethod.setSelected(true);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(_regressionMethod);
		buttonGroup.add(_stupidMethod);
		
		this.add(yearLists);
		
		this.pack();
		this.setVisible(true);
	}
	
	public class MethodListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getItemSelectable() == _regressionMethod) {
				State.getInstance().useRegressionProbability(true);
				useList.setEnabled(false);
				ignoreList.setEnabled(false);
			}
			else {
				State.getInstance().useRegressionProbability(false);
				useList.setEnabled(true);
				ignoreList.setEnabled(true);
			}
			
		}
		
	}

	public class AddListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int index = ignoreList.getSelectedIndex();

			if(index != -1) {
				Integer year = (Integer) ignoreList.getSelectedValue();
				ignoreHandler.remove(index);
				useHandler.addElement(year);
				sort(useHandler);

				State.getInstance().addYear(year);
			}
		}
	}

	public class RemoveListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int index = useList.getSelectedIndex();

			if(index != -1) {
				Integer year = (Integer) useList.getSelectedValue();
				useHandler.remove(index);
				ignoreHandler.addElement(year);
				sort(ignoreHandler);

				State.getInstance().removeYear(year);
			}
		}
	}

	public void sort(DefaultListModel listHandler) {
		if(listHandler.size() != 1 && listHandler.size() != 0) {
			Integer[] years = new Integer[listHandler.size()];

			for(int i = 0; i < years.length; i++) years[i] = (Integer) listHandler.elementAt(i);
			years = quicksort(years, 0, years.length - 1);

			listHandler.clear();
			for(int i = 0; i < years.length; i++) listHandler.insertElementAt(years[i], i);
		}
	}

	private Integer[] quicksort(Integer[] years, int first, int last) {
		int i = first;
		int j = last;

		int middle = years[(last + first) /2];

		while (i <= j) {
			while (years[i] < middle) i++;
			while (years[j] > middle) j--;

			if (i <= j) {
				int temp = years[i];
				years[i] = years[j];
				years[j] = temp;

				i++;
				j--;
			}
		}

		if (first < j) quicksort(years, first, j);
		if (i < last) quicksort(years, i, last);

		return years;
	}
}
