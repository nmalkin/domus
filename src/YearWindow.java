import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class YearWindow extends JFrame {
	JList useList;
	JList ignoreList;
	DefaultListModel ignoreHandler;
	DefaultListModel useHandler;

	protected YearWindow() {
		super("Select years");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// look and feel
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e) { e.printStackTrace(); }
		this.setPreferredSize(new Dimension(345,220));
		this.setResizable(false);

		ignoreHandler = new DefaultListModel();
		for(int year: State.getInstance().getIgnoredYears()) ignoreHandler.addElement(year);
		sort(ignoreHandler);

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

		useHandler = new DefaultListModel();
		for(int year: State.getInstance().getYears()) useHandler.addElement(year);
		sort(useHandler);

		useList = new JList(useHandler);
		useList.setBackground(new Color(238, 238, 238));
		useList.setLayoutOrientation(JList.VERTICAL);
		useList.setFixedCellWidth(120);
		useList.setFixedCellHeight(20);

		JPanel usePanel = new JPanel(new BorderLayout());
		usePanel.setPreferredSize(new Dimension(120,160));
		usePanel.setMaximumSize(new Dimension(120,160));
		usePanel.setBorder(new LineBorder(Color.GRAY));
		usePanel.add(useList, BorderLayout.NORTH);

		JButton add = new JButton(">>");
		add.addActionListener(new AddListener());
		JButton remove = new JButton("<<");
		remove.addActionListener(new RemoveListener());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(add);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		buttonPanel.add(remove);

		this.add(Box.createRigidArea(new Dimension(20, 0)));
		this.add(ignorePanel);
		this.add(Box.createRigidArea(new Dimension(10, 0)));
		this.add(buttonPanel);
		this.add(Box.createRigidArea(new Dimension(10, 0)));
		this.add(usePanel);

		this.pack();
		this.setVisible(true);
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
