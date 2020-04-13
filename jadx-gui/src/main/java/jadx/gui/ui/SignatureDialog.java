package jadx.gui.ui;

import jadx.api.JavaClass;
import jadx.api.JavaMethod;
import jadx.core.dex.instructions.args.ArgType;
import jadx.gui.treemodel.JMethod;
import jadx.gui.treemodel.JNode;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class SignatureDialog extends JDialog {
	private static final long serialVersionUID = -5480561835067709906L;
	public static final String SPACE_2 = "  ";
	public static final String SPACE_4 = "    ";
	public static final String SPACE_6 = "      ";
	public static final String COLON = ":";
	public static final String lineSeparator = "\n";
	private final JNode node;
	private static final Logger LOG = LoggerFactory.getLogger(SignatureDialog.class);

	private final JTextArea textArea = new JTextArea();

	public SignatureDialog(MainWindow mainWindow, JNode node) {
		super(mainWindow);
		this.node = node;

		textArea.setBorder(emptyBorder());
		textArea.setCaretPosition(0);


		String text = getText();

		JPanel signature = signaturePanel();
		JScrollPane editText = editTextPanel();
		JPanel buttons = buttonsPanel();

		JPanel all = new JPanel();
		all.setBorder(emptyBorder());
		all.setLayout(new BoxLayout(all, BoxLayout.Y_AXIS));
//		all.add(newSeparator());
		all.add(signature, BorderLayout.PAGE_START);
//		all.add(newSeparator());
		all.add(editText, BorderLayout.CENTER);
//		all.add(newSeparator());
		all.add(buttons, BorderLayout.PAGE_END);

		Container contentPane = getContentPane();
		contentPane.add(all);

		setTitle("方法签名");
		pack();
		setSize(800, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.MODELESS);

		registerKeyboard();

		mainWindow.getSettings().loadWindowPos(this);
	}

	private JScrollPane editTextPanel() {
		textArea.setText(getText());
		return new JScrollPane(textArea,
				VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	private void registerKeyboard() {
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private JPanel signaturePanel() {
		JLabel text = new JLabel("方法签名：");
		JLabel signature = new JLabel(node.makeLongStringHtml(), node.getIcon(), SwingConstants.LEFT);
		text.setLabelFor(signature);

		JPanel container = new JPanel();
		container.setLayout(new FlowLayout(FlowLayout.LEFT));
		container.add(text);
		container.add(signature);
		container.setBorder(emptyBorder());
		return container;
	}

	private Border emptyBorder() {
		return BorderFactory.createEmptyBorder(10, 10, 10, 10);
	}

	private String getText() {
		StringBuilder sb = new StringBuilder();
		if (node instanceof JMethod) {
			JavaMethod method = (JavaMethod) node.getJavaNode();

			JavaClass declaringClass = method.getDeclaringClass();
			sb.append("class").append(declaringClass.getName()).append(COLON).append(lineSeparator)
					.append(SPACE_2).append("name: ").append(getClassName(declaringClass)).append(lineSeparator);

			if (method.isConstructor()) {
				sb.append(SPACE_2).append("constructor").append(COLON).append(lineSeparator)
						.append(SPACE_4).append("isConstructor: true").append(lineSeparator);
			} else {
				String name = method.getName();
				sb.append(SPACE_2).append(name).append(COLON).append(lineSeparator)
						.append(SPACE_4).append("name: ").append(name).append(lineSeparator);
			}
			sb.append(SPACE_4).append("params:");
			List<ArgType> arguments = method.getArguments();
			if (arguments.isEmpty()) {
				sb.append(" []");
			}
			sb.append(lineSeparator);
			for (ArgType argument : arguments) {
				sb.append(SPACE_6).append("- ");
				String param = argument.toString();
				int idx = param.indexOf("<");
				if (idx > 0) {
					sb.append(param, 0, idx);
				} else {
					sb.append(param);
				}
				sb.append(lineSeparator);
			}

		} else {
			sb.append(node.toString());
		}

		return sb.toString();
	}

	/**
	 * 内部类是用.表示关系的，需要替换为$表示
	 *
	 * @param javaClass -
	 * @return -
	 */
	private String getClassName(JavaClass javaClass) {
		JavaClass declaringClass = javaClass.getDeclaringClass();
		if (declaringClass == null) {
			return javaClass.getFullName();
		} else {
			return getClassName(declaringClass) + '$' + javaClass.getName();
		}
	}

	private JPanel buttonsPanel() {
		JButton okButton = new JButton("复制");
		okButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 9040667663098607812L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(textArea.getText()), null);
				dispose();
			}
		});
		JButton cancelButton = new JButton("取消");
		cancelButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 9040667663098607812L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		JPanel buttons = new JPanel();
		buttons.add(okButton, BorderLayout.LINE_END);
		buttons.add(cancelButton, BorderLayout.LINE_END);
//		buttons.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		return buttons;
	}

}
