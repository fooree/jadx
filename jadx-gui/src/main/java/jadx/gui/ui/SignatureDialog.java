package jadx.gui.ui;

import jadx.api.JavaClass;
import jadx.api.JavaMethod;
import jadx.core.dex.instructions.args.ArgType;
import jadx.gui.treemodel.JMethod;
import jadx.gui.treemodel.JNode;
import jadx.gui.ui.codearea.ShowSignatureAction;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureDialog extends JDialog {
	private static final long serialVersionUID = -5480561835067709906L;
	public static final String SPACE_2 = "  ";
	public static final String SPACE_4 = "    ";
	public static final String SPACE_6 = "      ";
	public static final String COLON = ":";
	public static final String LINE_SAPERATOR = "\n";
	private final JNode node;
	private static final Logger LOG = LoggerFactory.getLogger(ShowSignatureAction.class);

	public SignatureDialog(MainWindow mainWindow, JNode node) {
		super(mainWindow);
		this.node = node;

		String text = getText();

		JPanel signature = signaturePanel();
		JTextArea editText = editTextPanel(text);
		JPanel buttons = buttonsPanel(text);

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

	private JTextArea editTextPanel(String text) {
		JTextArea textArea = new JTextArea(text);
		textArea.setBorder(emptyBorder());
		return textArea;
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
		JLabel signature = new JLabel(this.node.makeLongStringHtml(), this.node.getIcon(), SwingConstants.LEFT);
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
			sb.append("class").append(declaringClass.getName()).append(COLON).append(LINE_SAPERATOR)
					.append(SPACE_2).append("name: ").append(getClassName(declaringClass)).append(LINE_SAPERATOR);

			if (method.isConstructor()) {
				sb.append(SPACE_2).append("constructor").append(COLON).append(LINE_SAPERATOR)
						.append(SPACE_4).append("isConstructor: true").append(LINE_SAPERATOR);
			} else {
				String name = method.getName();
				sb.append(SPACE_2).append(name).append(COLON).append(LINE_SAPERATOR)
						.append(SPACE_4).append("name: ").append(name).append(LINE_SAPERATOR);
			}
			sb.append(SPACE_4).append("params:");
			List<ArgType> arguments = method.getArguments();
			if (arguments.isEmpty()) {
				sb.append(" []");
			}
			sb.append(LINE_SAPERATOR);
			for (ArgType argument : arguments) {
				sb.append(SPACE_6).append("- ");
				String param = argument.toString();
				int idx = param.indexOf("<");
				if (idx > 0) {
					sb.append(param, 0, idx);
				} else {
					sb.append(param);
				}
				sb.append(LINE_SAPERATOR);
			}

		} else {
			sb.append(node.toString());
		}

		return sb.toString();
	}

	/**
	 * 内部类是用.表示关系的，需要替换为$表示
	 *
	 * @param javaClass
	 * @return
	 */
	private String getClassName(JavaClass javaClass) {
		JavaClass declaringClass = javaClass.getDeclaringClass();
		if (declaringClass == null) {
			return javaClass.getFullName();
		} else {
			return getClassName(declaringClass) + '$' + javaClass.getName();
		}
	}

	private JPanel buttonsPanel(final String text) {
		JButton okButton = new JButton("复制");
		okButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 9040667663098607812L;

			@Override
			public void actionPerformed(ActionEvent e) {
				LOG.warn("文本：" + text);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(text), null);
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

	private void onOK() {
		// add your code here
		dispose();
	}

}
